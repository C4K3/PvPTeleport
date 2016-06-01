package net.simpvp.PvPTeleport;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeathbanCommand implements CommandExecutor {

	/**
	 * Teleports players between the overworld and the deathbanworld
	 */
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Player player = null;
		if (sender instanceof Player){
			player = (Player) sender;
		}

		if ( player == null ) {
			PvPTeleport.instance.getLogger().info("Only players can use this command.");
			return true;
		}

		String sWorld = player.getWorld().getName();

		/* This command can only be used to teleport between the overworld ("world") and the deathban world */
		if ( !sWorld.equals("world") && !sWorld.equals("deathban") ) {
			player.sendMessage(ChatColor.RED + "You must be in the overworld to use this command.");
			return true;
		}

		/* If player is in the overworld and is teleporting to the deathban world */
		if ( sWorld.equals("world") ) {

			DeathbanTransportation.teleportToDeathban(player);

		} else if ( sWorld.equals("deathban") ) {

			TeleportBack.teleportBack(player);

		}

		return true;

	}

}
