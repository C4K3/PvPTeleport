package net.simpvp.PvPTeleport;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/** Handles transportation from 'world' to 'pvp'.
 * 
 * Generally called by WorldCommand.
 */
public class PvPTransportation {

	/**
	 * Blindly teleports player to the PvP world. Assumes all checks have been completed already.
	 * @param player Player to teleport.
	 */
	public static void teleportToPvP(Player player) {

		Location loc = randomSpawn();

		/* If unable to get random spawn location */
		if ( loc == null ) {
			player.sendMessage(ChatColor.RED + "Unable to get a spawn location for you.\n"
					+ "Please try again.\n"
					+ "If this problem persists, please contact an admin for assistance.");
			return;
		}


		String sPlayer = player.getName();

		/* Teleporting will glitch if a player is inside a vehicle */
		if ( player.isInsideVehicle() ) {
			player.leaveVehicle();
		}

		SQLite.worldLocsInsert(player);

		player.teleport(loc);
		player.sendMessage(ChatColor.GOLD + "Teleporting you to a random location in the pvp world.");
		PvPTeleport.instance.getLogger().info("Teleporting " + sPlayer + " to the pvp world.");

	}

	/**
	 * Gives a random spawn location in the pvp world
	 * @return The random location.
	 */
	private static Location randomSpawn() {

		/* Array of materials the player is allowed to spawn inside of */
		Material[] nonsolids = new Material[11];

		nonsolids[0] = Material.AIR;
		nonsolids[1] = Material.SAPLING;
		nonsolids[2] = Material.GRASS;
		nonsolids[3] = Material.DEAD_BUSH;
		nonsolids[4] = Material.YELLOW_FLOWER;
		nonsolids[5] = Material.RED_ROSE;
		nonsolids[6] = Material.BROWN_MUSHROOM;
		nonsolids[7] = Material.RED_MUSHROOM;
		nonsolids[8] = Material.TORCH;
		nonsolids[9] = Material.SIGN;
		nonsolids[10] = Material.SIGN_POST;
		
		/* Array of materials the player is allowed to spawn on top of */
		Material[] solids = new Material[8];
		/*
		solids[0] = Material.STONE;
		solids[1] = Material.GRASS;
		solids[2] = Material.DIRT;
		solids[3] = Material.COBBLESTONE;
		solids[4] = Material.BEDROCK;
		solids[5] = Material.WATER;
		solids[6] = Material.STATIONARY_WATER;
		solids[7] = Material.SAND;
		solids[8] = Material.SANDSTONE;
		solids[9] = Material.GLASS;
		*/
		solids[0] = Material.STONE;
		solids[1] = Material.GRASS;
		solids[2] = Material.DIRT;
		solids[3] = Material.COBBLESTONE;
		solids[4] = Material.BEDROCK;
		solids[5] = Material.SAND;
		solids[6] = Material.SANDSTONE;
		solids[7] = Material.GLASS;

		Random randomGenerator = new Random();

		Material blocktype = null;
		Location spawnLoc = new Location(PvPTeleport.instance.getServer().getWorld("pvp"), 0, 0, 0);

		/* Only give it 1 000 tries */
		for (int counter = 0; counter < 1000; counter++) {

			Double y = 255.0;
			Double x = (double) randomGenerator.nextInt(450) - 225.5;
			Double z = (double) randomGenerator.nextInt(450) - 225.5;
			
			/* Temporary workaround for the December 2015 world */
			if (counter == 999) {
				x = 0.5;
				z = 0.5;
			}

			spawnLoc.setX(x);
			spawnLoc.setY(y);
			spawnLoc.setZ(z);

			blocktype = spawnLoc.getBlock().getType();

			/* Move downwards for as long as we have nonsolid blocks */
			while (Arrays.binarySearch(nonsolids,  blocktype) > -1 && y > 5 ) {
				y--;
				spawnLoc.setY(y);
				blocktype = spawnLoc.getBlock().getType();
			}
			
			if (Arrays.binarySearch(solids, blocktype) > -1) {
				PvPTeleport.instance.getLogger().info("Found valid pvp world spawn location on attempt " + counter + ".");
				return spawnLoc;
			}

		}

		PvPTeleport.instance.getLogger().info("Tried over 1000 times to find a suitable spawn, no result.");
		return null;

	}

}
