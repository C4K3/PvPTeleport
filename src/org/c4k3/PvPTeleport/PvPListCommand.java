package org.c4k3.PvPTeleport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvPListCommand {

	public static boolean main(Player sender) {
		//TODO
		int counter = 0;
		String list = " ";

		if ( sender == null ) {
			/* Sender is not an ingame player */

			for ( Player player : Bukkit.getWorld("pvp").getPlayers()) {

				counter++;

				list = list + player.getName() + ", ";

			}

			if ( counter > 0 ) list = list.substring(0, list.length() - 2) + "";

			PvPTeleport.instance.getLogger().info(counter + " players: " + list);

		} else {



			for ( Player player : Bukkit.getWorld("pvp").getPlayers()) {
				
				if ( sender.canSee(player)) {
				
				counter++;

				list = list + ChatColor.GOLD + player.getName() + ChatColor.WHITE + ", ";
				
				}

			}

			if ( counter > 0 ) list = list.substring(0, list.length() - 2) + "";

			if ( counter == 1 ) sender.sendMessage(ChatColor.GREEN + " There is currently " + ChatColor.GOLD + '1' + ChatColor.GREEN + " player in the PvP world");
			else sender.sendMessage(ChatColor.GREEN + " There are currently " + ChatColor.GOLD + counter + ChatColor.GREEN + " players in the PvP world");

			if ( counter > 0 ) sender.sendMessage(list);

		}

		return true;


	}

}
