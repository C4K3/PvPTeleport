package org.c4k3.PvPTeleport;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Teleports players logging out of the deathban world back to their location in the overworld.
 */
public class PlayerDisconnect implements Listener {

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {

		Player player = event.getPlayer();

		if ( player.getWorld().getName().equals("deathban" ) ) {
			TeleportBack.teleportBack(player);
		}

	}

}
