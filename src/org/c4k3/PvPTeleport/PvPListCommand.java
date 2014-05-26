package org.c4k3.PvPTeleport;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvPListCommand {

	/** Print a list of players in the pvp world to the sender. Simple as that! */
	public static boolean main(Player player) {
		int counter = 0;
		String list = " ";

		if ( player == null ) {
			/* Sender is not an ingame player, but they still might want to see a list of players */

			for ( Player tPlayer : PvPTeleport.instance.getServer().getWorld("pvp").getPlayers()) {

				counter++;

				list = list + tPlayer.getName() + ", ";

			}

			if ( counter > 0 ) list = list.substring(0, list.length() - 2) + "";

			PvPTeleport.instance.getLogger().info(counter + " players: " + list);

		} else {
			/* Sender is a player */
			
			for ( Player tPlayer : PvPTeleport.instance.getServer().getWorld("pvp").getPlayers()) {

				if ( player.canSee(tPlayer)) { // Invisible admins shouldn't show up

					counter++;

					list = list + ChatColor.GOLD + tPlayer.getName() + ChatColor.WHITE + ", ";

				}

			}

			if ( counter > 0 ) list = list.substring(0, list.length() - 2) + ""; // Remove the two trailing characters ( , and the space )

			/* English grammar can be annoying (player versus players) */
			if ( counter == 1 ) player.sendMessage(ChatColor.GREEN + " There is currently " + ChatColor.GOLD + '1' + ChatColor.GREEN + " player in the PvP world"); // English grammar is stupid
			else player.sendMessage(ChatColor.GREEN + " There are currently " + ChatColor.GOLD + counter + ChatColor.GREEN + " players in the PvP world");

			/* If 1 or more players are in the pvp world, send the list of these players. Obv. don't send if there are 0 players */
			if ( counter > 0 ) player.sendMessage(list);

		}

		return true;


	}

}
