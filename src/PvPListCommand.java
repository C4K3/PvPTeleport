package net.simpvp.PvPTeleport;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

public class PvPListCommand implements CommandExecutor {

	/* Stores all the players who have /pvplist subscribed */
	public static HashSet<UUID> subscribed_players = new HashSet<UUID>();

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

		if (args.length == 0) {
			show_list(player);
		} else {
			subscribe(player, args);
		}

		return true;
	}

	/**
	 * To be called when a player teleports, so that this information
	 * may be sent to all /pvplist subscribed players.
	 *
	 * MUST be called AFTER the player has been teleported.
	 */
	public static void on_player_teleport(Player player) {
		String msg;
		if (player.getWorld().getName().equals("pvp")) {
			msg = ChatColor.AQUA + player.getName()
				+ " is now entering the pvp world.";
		} else {
			msg = ChatColor.BLUE + player.getName()
				+ " is now leaving the pvp world.";
		}

		for (Player p : PvPTeleport.instance.getServer()
				.getOnlinePlayers()) {
			UUID uuid = p.getUniqueId();
			if (subscribed_players.contains(uuid)
					&& uuid != player.getUniqueId()) {
				p.sendMessage(msg);
			}
		}
	}

	/**
	 * Handles /pvplist subscribe
	 *
	 * Setting whether to show messages when somebody enters/leaves the
	 * pvp world to the given player.
	 */
	private void subscribe(Player player, String[] args) {
		if (args.length > 1 || !args[0].equalsIgnoreCase("subscribe")) {
			String msg = "Invalid usage. Correct usage is "
				+ "/pvplist [subscribe]";
			
			if (player == null) {
				PvPTeleport.instance.getLogger().info(msg);
			} else {
				player.sendMessage(ChatColor.RED + msg);
			}

			return;
		}

		if (player == null) {
			PvPTeleport.instance.getLogger().info(
					"Only players can use this command.");
			return;
		}

		UUID uuid = player.getUniqueId();

		if (subscribed_players.contains(uuid)) {
			player.sendMessage(ChatColor.GREEN
					+ "You are no longer subscribed to pvplist.");
			subscribed_players.remove(uuid);
			SQLite.pvplistSubscribeRemove(uuid);
		} else {
			player.sendMessage(ChatColor.GREEN
					+ "You are now subscribed to pvplist.");
			subscribed_players.add(uuid);
			SQLite.pvplistSubscribeSet(uuid);
		}

	}

	/**
	 * Show the list of who's in the pvp world to 'player'
	 */
	private void show_list(Player player) {

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

		World world = PvPTeleport.instance.getServer().getWorld("pvp");
		if (world == null || DisableWorldCommand.is_disabled) {
			player.sendMessage(ChatColor.RED + "The pvp world is currently disabled.");
			return;
		}

		for (Player tPlayer : world.getPlayers()) {

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

		return;

	}

}

