package org.c4k3.PvPTeleport;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Handles teleporting the player back to the overworld from either 'pvp' or 'deathban'.
 */
public class TeleportToWorld {

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

		if (player.getWorld().getName().equals("deathban")) {
			SQLite.deathBanLocsInsert(player);
			DeathbanScoreTracker.lastAttackerRemove(uuid);
		}

		/* Teleporting will glitch if a player is inside a vehicle */
		if (player.isInsideVehicle()) {
			player.leaveVehicle();
		}

		String sPlayer = player.getName();

		player.teleport(loc);
		player.sendMessage(ChatColor.GOLD + "Teleporting you back to your saved location in the overworld.");
		PvPTeleport.instance.getLogger().info("Teleporting " + sPlayer + " back to " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());

		if (player.getWorld().getName().equals("world"))
			SQLite.worldLocsRemove(uuid);

	}

}
