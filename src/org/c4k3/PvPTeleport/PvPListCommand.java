package org.c4k3.PvPTeleport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvPListCommand {

	public static boolean main(Player sender) {
		/** Print a list of players in the pvp world to the sender. Simple as that! */
		int counter = 0;
		String list = " ";

		if ( sender == null ) {
			/* Sender is not an ingame player, but they still might want to see a list of players */

			for ( Player player : Bukkit.getWorld("pvp").getPlayers()) {

				counter++;

				list = list + player.getName() + ", ";

			}

			if ( counter > 0 ) list = list.substring(0, list.length() - 2) + "";

			PvPTeleport.instance.getLogger().info(counter + " players: " + list);

		} else {
			/* Sender is a player */
			
			for ( Player player : Bukkit.getWorld("pvp").getPlayers()) {

				if ( sender.canSee(player)) { // Invisible admins shouldn't show up

					counter++;

					list = list + ChatColor.GOLD + player.getName() + ChatColor.WHITE + ", ";

				}

			}

			if ( counter > 0 ) list = list.substring(0, list.length() - 2) + ""; // Remove the two trailing characters ( , and the space )

			if ( counter == 1 ) sender.sendMessage(ChatColor.GREEN + " There is currently " + ChatColor.GOLD + '1' + ChatColor.GREEN + " player in the PvP world"); // English grammar is stupid
			else sender.sendMessage(ChatColor.GREEN + " There are currently " + ChatColor.GOLD + counter + ChatColor.GREEN + " players in the PvP world");

			if ( counter > 0 ) sender.sendMessage(list); // Because if counter == 0 then the list is empty

		}

		return true;


	}

}
