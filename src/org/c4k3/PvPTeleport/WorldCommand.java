package org.c4k3.PvPTeleport;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;

public class WorldCommand {

	public static boolean main(Player player) {
		/** Teleports player to a random location in the pvp world
		 * Returns true if it was properly handled (teleport successful, or player was deteremined ineligible for teleport)
		 * Returns false if it failed
		 */

		if ( player != null ) {

			String sworld = player.getWorld().getName();

			String splayer = player.getName();

			if ( sworld.equals("world") ) {

				if ( !EnchantmentBan.TeleportCheck(player)) {

					if ( !combatLogCheck(player)) {

						if ( player.isInsideVehicle() ) player.leaveVehicle(); // Forces players out of vehicles

						Location pLoc = player.getLocation();
						int x = pLoc.getBlockX();
						int y = pLoc.getBlockY();
						int z = pLoc.getBlockZ();

						SQL.putPlayer(splayer, x, y, z);

						player.teleport(PvPTeleport.instance.getServer().getWorld("pvp").getSpawnLocation());
						player.sendMessage(ChatColor.GOLD + "Teleporting you to the pvp world.");

					} else {
						/* combat log check failed */
						player.sendMessage(ChatColor.RED + "You cannot use this command while within 50 blocks of any other players, or 5 blocks of any hostile mobs");
						return true;
					}

				}

				else {
					/* Inventory check failed */
					player.sendMessage(ChatColor.RED + "Enchanted items are forbidden from use in the pvp world. Please remove any enchanted items from your hotbar and armor slots, then try again.");
					return true;
				}

			}

			if ( sworld.equals("pvp") ) {

				if ( isInSpawn(player)) {

					Location tloc = SQL.getPlayer(splayer);

					if ( tloc == null ) return false;

					if ( player.isInsideVehicle() ) player.leaveVehicle();

					PvPTeleport.instance.getLogger().info("Teleporting " + splayer + " back to " + tloc.getBlockX() + " " + tloc.getBlockY() + " " + tloc.getBlockZ());

					player.teleport(tloc);

					player.sendMessage(ChatColor.GOLD + "Teleporting you back to your saved location in the overworld");

				} else {
					/* combat log check failed */
					player.sendMessage(ChatColor.RED + "You must be inside the protected area to teleport back.");
					return true;
				}

			}

			/* To prevent players from using command while not in overworld or pvpworld */
			if ( !sworld.equals("world") && !sworld.equals("pvp") ) {

				player.sendMessage(ChatColor.RED + "You must be in the overworld to use this command");
				return true;

			}

		} else {
			/* player is null */
			PvPTeleport.instance.getLogger().info("Only players can use this command");

		}

		return true;

	}

	private static boolean combatLogCheck(Player player) {
		/** Checks that player is not trying to combatlog
		 * Returns true if player is within 50 blocks of any other players, or 5 blocks of any hostile mobs
		 * else returns false
		 */

		Location pLoc = player.getLocation();

		World world = player.getWorld();

		for ( Player p : world.getPlayers() ) {
			/* Check if there are any players within 50 blocks */

			if ( !p.equals(player) && p.getLocation().distance(pLoc) < 50 && player.canSee(p) ) return true;

		}
		
		for ( Entity entity : world.getEntitiesByClasses(Blaze.class, Creeper.class, Enderman.class, Ghast.class, PigZombie.class, Skeleton.class, Spider.class, Witch.class, CaveSpider.class, Slime.class, MagmaCube.class, Silverfish.class, Zombie.class) ) {
			/* Check if there are any hostile mobs within 50 blocks */

			if ( entity.getLocation().distance(pLoc) < 5 ) return true;

		}

		return false;

	}
	
	private static boolean isInSpawn(Player player) {
		/** Checks whether player is inside spawn (of fixed size) */
		
		Location loc = player.getLocation();
		
		Double x = Math.abs(loc.getX());
		
		Double z = Math.abs(loc.getZ());
		
		if ( ( x <= 31 ) && ( z <= 31 ) ) return true;
		
		return false;
		
	}

}