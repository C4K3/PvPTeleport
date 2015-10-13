package org.c4k3.PvPTeleport;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Checks all damage, and if it's damage to a player in the deathban world that would have killed said player
 * cancel the event and teleport player out of the deathban world.
 */
public class EntityDamage implements Listener {

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onEntityDamageEvent(EntityDamageEvent event) {

		if (event.getEntity().getType() != EntityType.PLAYER) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (!player.getLocation().getWorld().getName().equals("deathban")) {
			return;
		}

		if ((player.getHealth() - event.getDamage()) > 0) {
			return;
		}

		event.setCancelled(true);

		UUID uuid = player.getUniqueId();

		SQLite.deathBanSetStatus(uuid, 1);

		UUID killer = DeathbanScoreTracker.lastAttackerGet(uuid);
		if (killer != null) {
			SQLite.deathBanIncrementPoints(killer);
		}

		for (Player p : PvPTeleport.instance.getServer().getWorld("deathban").getPlayers()) {
			p.sendMessage(ChatColor.DARK_GREEN + player.getName() + " has been slain.");
		}

		player.sendMessage(ChatColor.RED + "You have died, and so will not be able to play in the deathban world until the end of this round.");

		TeleportBack.teleportBack(player);

	}

}
