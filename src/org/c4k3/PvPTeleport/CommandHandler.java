package org.c4k3.PvPTeleport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = null;
		if (sender instanceof Player){
			player = (Player) sender;
		}
		
		String scmd = command.getName();
		
		if ( scmd.equals("world") ) return WorldCommand.main(player);
		
		if ( scmd.equals("pvplist") ) return PvPListCommand.main(player);
		
		return true;
	}

}
