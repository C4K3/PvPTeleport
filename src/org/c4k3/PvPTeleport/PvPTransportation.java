package org.c4k3.PvPTeleport;

import org.bukkit.ChatColor;
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

		String sPlayer = player.getName();

		/* Teleporting will glitch if a player is inside a vehicle */
		if ( player.isInsideVehicle() ) {
			player.leaveVehicle();
		}

		SQLite.worldLocsInsert(player);

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

	}

}
