package org.c4k3.PvPTeleport;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvPListCommand implements CommandExecutor {

	/** Print a list of players in the pvp world to the sender. Simple as that! */
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Player player = null;
		if (sender instanceof Player){
			player = (Player) sender;
		}

		/* Hacky workaround for only showing colors to players and not to console */
		String gold = "";
		String green = "";
		String white = "";
		String darkAqua = "";

		if ( player != null ) {
			/* Only add colors if it's a player and not console */

			gold += ChatColor.GOLD;
			green += ChatColor.GREEN;
			white += ChatColor.WHITE;
			darkAqua += ChatColor.DARK_AQUA;

		} 

		int pvpCounter = 0;
		int deathbanCounter = 0;
		String pvpList = " ";
		String deathbanList = " ";

		for ( Player tPlayer : PvPTeleport.instance.getServer().getWorld("pvp").getPlayers() ) {

			if ( player == null || player.canSee(tPlayer) ) {

				pvpCounter++;
				pvpList += gold + tPlayer.getName() + ChatColor.WHITE + ", ";

			}

		}

		for ( Player tPlayer : PvPTeleport.instance.getServer().getWorld("deathban").getPlayers()) {

			if ( player == null || player.canSee(tPlayer) ) {

				deathbanCounter++;
				deathbanList += gold + tPlayer.getName() + white + ", ";

			}

		}

		if ( pvpCounter > 0 ) pvpList = pvpList.substring(0, pvpList.length() - 2); // Remove the two trailing characters ( , and the space )
		if ( deathbanCounter > 0 ) deathbanList = deathbanList.substring(0, deathbanList.length() - 2);

		String message = "";

		if ( pvpCounter == 1 ) message += green + " There is currently " + gold + "1" + green + " player in the PvP world."; // English grammar is stupid.
		else message += green + " There are currently " + gold + pvpCounter + green + " players in the PvP world.";
		if ( pvpCounter > 0 ) message += "\n " + pvpList;

		if ( deathbanCounter == 1 ) message += darkAqua + "\n There is currently " + gold + "1" + darkAqua + " player in the deathban world.";
		else message += darkAqua + "\n There are currently " + gold + deathbanCounter + darkAqua + " players in the deathban world.";
		if ( deathbanCounter > 0 ) message += "\n " + deathbanList;

		if ( player == null ) {
			PvPTeleport.instance.getLogger().info(message);
		} else {
			player.sendMessage(message);
		}

		return true;

	}

}
