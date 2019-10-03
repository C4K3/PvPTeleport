package net.simpvp.PvPTeleport;

import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Glide implements Listener {

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
	public void onPlayerGlide(EntityToggleGlideEvent event) {
		Location location = event.getEntity().getLocation();

		if (event.isGliding() && location.getWorld().getName().equals("pvp")) {
			event.setCancelled(true);
		}
	}
}
