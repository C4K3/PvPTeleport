package net.simpvp.PvPTeleport;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.WorldBorder;

/** Handles transportation from 'world' to 'pvp'.
 * 
 * Generally called by WorldCommand.
 */
public class PvPTransportation {

	/**
	 * Blindly teleports player to the PvP world.
	 *
	 * Assumes all checks have been completed already.
	 * @param player Player to teleport.
	 */
	public static void teleportToPvP(Player player) {

		Location loc = randomSpawn();

		/* If unable to get random spawn location */
		if (loc == null) {
			player.sendMessage(ChatColor.RED + "Unable to get a spawn location for you.\n"
					+ "Please try again.\n"
					+ "If this problem persists, please contact an admin for assistance.");
			return;
		}

		String sPlayer = player.getName();

		/* Teleporting will glitch if a player is inside a vehicle */
		if (player.isInsideVehicle()) {
			player.leaveVehicle();
		}

		SQLite.worldLocsInsert(player);

		player.teleport(loc);
		player.sendMessage(ChatColor.GOLD + "Teleporting you to a random location in the pvp world.");
		PvPTeleport.instance.getLogger().info("Teleporting "
				+ sPlayer + " to the pvp world.");
		PvPListCommand.on_player_teleport(player);
	}

	/**
	 * Gives a random spawn location in the pvp world
	 * @return The random location.
	 */
	private static Location randomSpawn() {

		Random RNG = new Random();

		Material blocktype = null;
		World pvpworld = PvPTeleport.instance.getServer().getWorld("pvp");
		WorldBorder border = pvpworld.getWorldBorder();
		Double x_center = border.getCenter().getX();
		Double z_center = border.getCenter().getZ();
		/* Make the area within which players can spawn 50 less than the
		 * border, so people don't spawn within 25 blocks of the border */
		Double border_size = border.getSize() - 50;
		if (border_size <= 0) {
			border_size = border.getSize();
		}

		Location spawnLoc = new Location(pvpworld, 0, 0, 0);

		/* Only give it 1 000 tries */
		for (int counter = 0; counter < 1000; counter++) {

			Double y = 255.0;
			Double x = (double) RNG.nextInt(border_size.intValue()) - (border_size/2);
			x += x_center;
			Double z = (double) RNG.nextInt(border_size.intValue()) - (border_size/2);
			z += z_center;
			
			/* If all randomly tried positions have failed, ensure we try 0,0 at least once */
			if (counter == 999) {
				x = 0.5;
				z = 0.5;
			}

			spawnLoc.setX(x);
			spawnLoc.setY(y);
			spawnLoc.setZ(z);

			blocktype = spawnLoc.getBlock().getType();

			/* Move downwards for as long as we have
			 * nonsolid blocks until we reach a solid block */
			while (is_nonsolid(blocktype) && y > 5) {
				y--;
				spawnLoc.setY(y);
				blocktype = spawnLoc.getBlock().getType();
			}
			
			if (blocktype.isSolid()) {
				String tmp = String.format("Found valid pvp world spawn location on attempt %d. (%f, %f, %f)", counter, x, y, z);
				PvPTeleport.instance.getLogger().info(tmp);
				return spawnLoc;
			}

		}

		PvPTeleport.instance.getLogger().info("Tried over 1000 times to find a suitable spawn, no result.");
		return null;

	}

	/* Materials the player is allowed to spawn inside of */
	private static boolean is_nonsolid(Material block) {
		switch (block) {
			case AIR:
			case ACACIA_SAPLING:
			case BIRCH_SAPLING:
			case DARK_OAK_SAPLING:
			case JUNGLE_SAPLING:
			case OAK_SAPLING:
			case SPRUCE_SAPLING:
			case GRASS:
			case DEAD_BUSH:
			case DANDELION:
			case ROSE_RED:
			case BROWN_MUSHROOM:
			case RED_MUSHROOM:
			case TORCH:
			case SIGN:
			case WALL_SIGN:
			      return true;
			default:
			      return false;
		}
	}

}

