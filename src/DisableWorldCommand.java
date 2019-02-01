package net.simpvp.PvPTeleport;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.World;

public class DisableWorldCommand implements CommandExecutor {

	public static boolean is_disabled = false;

	/**
	 * Disable the /world command and return everybody in /world back to
	 * the overworld.
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

		if (player != null && !player.isOp()) {
			player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
			return true;
		}


		if (is_disabled) {
			is_disabled = false;
			sender.sendMessage("Enabled /world");
			for (Player p : PvPTeleport.instance.getServer().getOnlinePlayers()) {
				if (p.isOp()) {
					p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC
							+ "[" + sender.getName() + ": Enabled /world]");
				}
			}
		} else {
			sender.sendMessage("Disabling /world . . .");
			is_disabled = true;

			World world = PvPTeleport.instance.getServer().getWorld("pvp");
			for (Player p : world.getPlayers()) {
				TeleportBack.teleportBack(p);
			}
			sender.sendMessage("Disabled /world");
			for (Player p : PvPTeleport.instance.getServer().getOnlinePlayers()) {
				if (p.isOp()) {
					p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC
							+ "[" + sender.getName() + ": Disabled /world]");
				}
			}
		}

		return true;
	}

}

