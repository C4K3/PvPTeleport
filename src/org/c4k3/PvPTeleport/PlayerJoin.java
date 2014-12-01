package org.c4k3.PvPTeleport;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		if ( !player.getWorld().getName().equals("deathban") )
			return;
		
		TeleportBack.teleportBack(player);
		
	}

}
