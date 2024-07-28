package net.simpvp.PvPTeleport;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Handles teleporting the player back to the overworld from the pvp world
 */
public class TeleportBack {

	/**
	 * Teleports the given player back to their location in the overworld.
	 *
	 * @param player Player who is to be teleported back.
	 */
	public static void teleportBack(Player player) {

		UUID uuid = player.getUniqueId();

		Location loc = SQLite.worldLocsGet(uuid);

		/* If unable to get location. */
		if (loc == null) {
			player.sendMessage(ChatColor.RED + "Database error."
					+ "\nPlease contact an admin for assistance.");
			return;
		}

		/* Teleporting will glitch if a player is inside a vehicle */
		if (player.isInsideVehicle()) {
			player.leaveVehicle();
		}

		String sPlayer = player.getName();
		Location safeLocation = getSafeLocation(loc);
		if (safeLocation == null) {
			player.sendMessage(ChatColor.RED + "No safe location found near your saved location in the overworld!");
			return;
		}

		player.teleport(safeLocation);
		player.sendMessage(ChatColor.GOLD
				+ "Teleporting you back to your saved location in the overworld.");
		PvPTeleport.instance.getLogger().info("Teleporting " + sPlayer
				+ " back to "
				+ safeLocation.getBlockX() + " "
				+ safeLocation.getBlockY() + " "
				+ safeLocation.getBlockZ());
		PvPListCommand.on_player_teleport(player);

		if (player.getWorld().getName().equals("world")) {
			SQLite.worldLocsRemove(uuid);
		}

	}

	/**
	 * Checks if the location and the blocks above it are safe.
	 * @param location The location to check.
	 * @return True if the location is safe, false otherwise.
	 */
	private static boolean isSafeLocation(Location location) {
		World world = location.getWorld();
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();

		Block block1 = world.getBlockAt(x, y, z);        // Block where the player stands
		Block block2 = world.getBlockAt(x, y + 1, z);    // Block where the player's head will be
		Block blockAbove = world.getBlockAt(x, y + 2, z); // Block directly above the player

		return block1.isPassable() && block2.isPassable() && blockAbove.isPassable();
	}

	/**
	 * Finds a safe location near the given location.
	 * @param destination The initial destination location.
	 * @return A safe location near the destination or null if no safe location is found.
	 */
	private static Location getSafeLocation(Location destination) {
		if (isSafeLocation(destination)) {
			return destination;
		}

		World world = destination.getWorld();
		int startX = destination.getBlockX();
		int startY = destination.getBlockY();
		int startZ = destination.getBlockZ();

		// Check for a safe location upwards from the starting location
		for (int y = startY + 1; y < world.getMaxHeight() - 1; y++) {
			Location checkLocation = new Location(world, startX, y, startZ);
			Block block1 = world.getBlockAt(checkLocation);
			Block block2 = world.getBlockAt(checkLocation.add(0, 1, 0));

			if (block1.isPassable() && block2.isPassable()) {
				return new Location(world, startX + 0.5, y, startZ + 0.5);
			}
		}
		return null;
	}
}
