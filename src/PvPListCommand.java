package net.simpvp.PvPTeleport;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvPListCommand implements CommandExecutor {

	/**
	 * Print a list of players in the pvp world to the sender.
	 */
	public boolean onCommand(
			CommandSender sender,
			Command command,
			String label,
			String[] args) {

		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}

		/* Hacky workaround for only showing colors to players
		 * and not to console */
		String gold = "";
		String green = "";
		String white = "";
		String darkAqua = "";

		if (player != null) {
			gold += ChatColor.GOLD;
			green += ChatColor.GREEN;
			white += ChatColor.WHITE;
			darkAqua += ChatColor.DARK_AQUA;
		}

		int pvpCounter = 0;
		String pvpList = " ";

		for (Player tPlayer : PvPTeleport.instance.getServer()
				.getWorld("pvp").getPlayers()) {

			if (player == null || player.canSee(tPlayer)) {
				pvpCounter++;
				pvpList += gold + tPlayer.getName()
					+ ChatColor.WHITE + ", ";
			}

		}

		if (pvpCounter > 0) {
			/* Remove the trailing comma and space */
			pvpList = pvpList.substring(0, pvpList.length() - 2);
		}

		String message = "";

		/* English grammar is stupid */
		if (pvpCounter == 1) {
			message += green + " There is currently "
				+ gold + "1"
				+ green + " player in the PvP world.";
		} else {
			message += green + " There are currently "
				+ gold + pvpCounter
				+ green + " players in the PvP world.";
		}

		if (pvpCounter > 0) {
			message += "\n " + pvpList;
		}

		if (player == null) {
			PvPTeleport.instance.getLogger().info(message);
		} else {
			player.sendMessage(message);
		}

		return true;

			}

}

