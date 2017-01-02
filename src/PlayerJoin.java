package net.simpvp.PvPTeleport;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class PlayerJoin implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!event.getPlayer().getWorld().getName().equals("pvp")) {
			return;
		}

		TeleportBack.teleportBack(event.getPlayer());
	}

}

