package org.c4k3.PvPTeleport;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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

						Location rloc = pvpspawn();

						if ( rloc == null ) return false; // If a valid spawn location could not be found

						if ( player.isInsideVehicle() ) player.leaveVehicle(); // Forces players out of vehicles

						Location pLoc = player.getLocation();
						int x = pLoc.getBlockX();
						int y = pLoc.getBlockY();
						int z = pLoc.getBlockZ();

						SQL.putPlayer(splayer, x, y, z);

						player.teleport(rloc);
						player.sendMessage(ChatColor.GOLD + "Teleporting to random location in pvp world");

					} else {
						/* combat log check failed */
						player.sendMessage(ChatColor.RED + "You cannot use this command while within 50 blocks of any other players, or 5 blocks of any hostile mobs");
						return true;
					}

				}

				else {
					/* Inventory check failed */
					player.sendMessage(ChatColor.RED + "Enchanted items are forbidden from use in the pvp world, therefore you are not permitted to enter the pvp world with any enchanted items in your armor slots or on your hotbar");
					return true;
				}

			}

			if ( sworld.equals("pvp") ) {

				if ( !combatLogCheck(player)) {

					Location tloc = SQL.getPlayer(splayer);

					if ( tloc == null ) return false;

					if ( player.isInsideVehicle() ) player.leaveVehicle();

					PvPTeleport.instance.getLogger().info("Teleporting " + splayer + " back to " + tloc.getBlockX() + " " + tloc.getBlockY() + " " + tloc.getBlockZ());

					player.teleport(tloc);

					player.sendMessage(ChatColor.GOLD + "Teleporting you back to your saved location in the overworld");

				} else {
					/* combat log check failed */
					player.sendMessage(ChatColor.RED + "You cannot use this command while within 50 blocks of any other players, or 5 blocks of any hostile mobs");
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

	private static Location pvpspawn() {
		/** Returns a randomly chosen safe (from the environment) spawn location in the pvp world */

		Material[] nonsolids; // This array determines which blocks it is okay to spawn IN

		nonsolids = new Material[12]; // IMPORTANT: Make sure the size of the array is the same as the amount of blocks put into the array. Apparently java won't let you do arrays of arbitrary size (why aren't I using an ArrayList?)

		nonsolids[0] = Material.AIR;
		nonsolids[1] = Material.SAPLING;
		nonsolids[2] = Material.GRASS;
		nonsolids[3] = Material.DEAD_BUSH;
		nonsolids[4] = Material.YELLOW_FLOWER;
		nonsolids[5] = Material.RED_ROSE;
		nonsolids[6] = Material.BROWN_MUSHROOM;;
		nonsolids[7] = Material.RED_MUSHROOM;
		nonsolids[8] = Material.TORCH;
		nonsolids[9] = Material.SIGN;
		nonsolids[10] = Material.SIGN_POST;
		nonsolids[11] = Material.VINE;

		Random randomGenerator = new Random();

		Material blocktype = null;
		Material blocktype1 = null;
		Material blocktype2 = null;
		Location spawnloc = new Location(PvPTeleport.instance.getServer().getWorld("pvp"), 0, 0, 0);
		int counter = 1;

		while ( counter < 1000 ) {
			/* We try 1000 random locations. Give up otherwise */

			Double y = 60.0;
			Double x = (double) randomGenerator.nextInt(299) - 149.5;
			Double z = (double) randomGenerator.nextInt(299) - 149.5;

			spawnloc.setX(x);
			spawnloc.setY(y);
			spawnloc.setZ(z);

			blocktype = spawnloc.getBlock().getType();

			while ( blocktype == Material.STONE || blocktype == Material.GRASS || blocktype == Material.DIRT || blocktype == Material.COBBLESTONE || blocktype == Material.WATER || blocktype == Material.STATIONARY_WATER ||  blocktype == Material.SAND || blocktype == Material.SANDSTONE && y <= 250 ) {
				/* While the block at the random location (y = 60) is one of the above: check if the 3 blocks above are valid nonsolids. If they all are, we've got a valid location. If not, we try to increment y by one and check again. */
				
				Double y1 = y + 1;
				Double y2 = y + 2;
				Double y3 = y + 3;

				spawnloc.setY(y1);

				blocktype1 = spawnloc.getBlock().getType();

				spawnloc.setY(y2);

				blocktype2 = spawnloc.getBlock().getType();

				if ( Arrays.binarySearch(nonsolids, blocktype1) > -1 && Arrays.binarySearch(nonsolids, blocktype2) > -1) {

					PvPTeleport.instance.getLogger().info("Found valid pvpworld spawn location on attempt " + counter);
					spawnloc.setY(y3); // Put the player's head 3 blocks above the surface. This way they'll fall 1 block, good for lag other instabilities
					return spawnloc;

				}

				y++;

				spawnloc.setY(y);
				blocktype = spawnloc.getBlock().getType();

			}

			counter++;

		}

		PvPTeleport.instance.getLogger().info("Tried over 1,000 times to find a suitable spawn, no result");
		return null;

	}

}