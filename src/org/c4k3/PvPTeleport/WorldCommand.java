package org.c4k3.PvPTeleport;

import java.util.UUID;

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

	/** Teleports player to a random location in the pvp world
	 * Returns true if it was properly handled (teleport successful, or player was deteremined ineligible for teleport)
	 * Returns false if it failed
	 */
	public static boolean main(Player player) {

		/* Checks that sender is a player and not console */
		if ( player == null ) {
			PvPTeleport.instance.getLogger().info("Only players can use this command.");
			return true;
		}

		String sWorld = player.getWorld().getName();

		/* Checks that they are either in the pvp world or overworld */
		if ( !sWorld.equals("world") && !sWorld.equals("pvp") ) {
			player.sendMessage(ChatColor.RED + "You must be in the overworld to use this command.");
			return true;
		}

		String tCheck = teleportCheck(player);

		/* Checks that they do not fail the teleportCheck (combatlog check) */
		if ( tCheck != null ) { 
			player.sendMessage(tCheck);
			return true;
		}

		/* Teleporting will glitch if a player is inside a vehicle */
		if ( player.isInsideVehicle() ) {
			player.leaveVehicle();
		}

		String sPlayer = player.getName();
		UUID uuid = player.getUniqueId();

		/* If the player using the command is in the overworld ("world"), then they should be sent to the spawn of the
		 * pvp world, and their overworld coordinates shall be saved in the database */
		if ( sWorld.equals("world") ) {

			SQL.putPlayer(player);

			player.teleport(PvPTeleport.instance.getServer().getWorld("pvp").getSpawnLocation());
			player.sendMessage(ChatColor.GOLD + "Teleporting you to the pvp world.");
			PvPTeleport.instance.getLogger().info("Teleporting " + sPlayer + " to the pvp world.");

			/* Warn players if they've got any enchanted items on their hotspot or armor slots */
			if ( EnchantmentBan.TeleportCheck(player) ) {
				player.sendMessage(" " + ChatColor.RED + "" + ChatColor.ITALIC  + ChatColor.UNDERLINE + ChatColor.BOLD + "Attention!"
						+ ChatColor.RESET + "\n "
						+ ChatColor.RED + "\nYou are entering the pvp world with enchanted items. Please beware that enchanted items are "
						+ "forbidden from use in the pvp world. You might lose them if you try to use them.");
			}

			return true;

		}

		/* If the person using the command is in the pvp world, then their location shall be retrieved from the database
		 * and they shall be teleoprted to that location in the overworld */
		else if ( sWorld.equals("pvp") ) {

			Location tLoc = SQL.getPlayer(uuid);

			/* This will generally happen in the event an admin has teleported to the pvp world with a special admin command,
			 * and is now trying to get back. */
			if ( tLoc == null ) {
				return false;
			}

			player.teleport(tLoc);
			player.sendMessage(ChatColor.GOLD + "Teleporting you back to your saved location in the overworld.");
			PvPTeleport.instance.getLogger().info("Teleporting " + sPlayer + " back to " + tLoc.getBlockX() + " " + tLoc.getBlockY() + " " + tLoc.getBlockZ());

			return true;

		}

		return false;

	}

	/** Checks that player is not trying to combatlog/is allowed to teleport
	 * Returns an error message to be displayed if the player is not allowed to teleport
	 * Returns null if the player is allowed to teleport
	 */
	private static String teleportCheck(Player player) {

		Location pLoc = player.getLocation();

		World world = player.getWorld();

		/* In the pvp world, a player just has to be inside spawn to be allowed to teleport */
		if ( world.getName().equals("pvp") ) {

			Double x = Math.abs(pLoc.getX());

			Double z = Math.abs(pLoc.getZ());

			/* Since the spawn is square, if either the x or the z is higher than 31, the player must logically be outside the spawn */
			if ( ( x >= 31 ) || ( z >= 31 ) ) {
				return ChatColor.RED + "You must be inside the protected area to teleport back.\n"
						+ "(x = 0, z = 0.)";
			}

			else return null;

		}
		/* In all other worlds, players are considered safe if they are more than 50 blocks from any players, or 5 blocks from any mobs */

		/* Check if there are any players within 50 blocks */
		for ( Player p : world.getPlayers() ) {

			if ( !p.equals(player) && p.getLocation().distance(pLoc) < 50 && player.canSee(p) ) return ChatColor.RED + "You cannot use this command while within 50 blocks of any other players.";

		}

		/* Check if there are any hostile mobs within 5 blocks */
		for ( Entity entity : world.getEntitiesByClasses(Blaze.class, Creeper.class, Enderman.class, Ghast.class, PigZombie.class, Skeleton.class,
				Spider.class, Witch.class, CaveSpider.class, Slime.class, MagmaCube.class, Silverfish.class, Zombie.class) ) {

			if ( entity.getLocation().distance(pLoc) < 5 ) return ChatColor.RED + "You cannot use this command while within 5 blocks of any hostile mobs.";

		}

		/* Default to allow teleport */
		return null;

	}

}