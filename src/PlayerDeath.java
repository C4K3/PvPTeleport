package net.simpvp.PvPTeleport;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;

/**
 * Appends '(in pvp)' to the death messages of anybody who died in the
 * the pvp world. Though it only shows the changed message to people who have
 * used /pvplist subscribe
 */
public class PlayerDeath implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!event.getEntity().getWorld().getName().equals("pvp")) {
			return;
		}

		String msg = event.getDeathMessage();
		event.setDeathMessage("");

		for (Player p : PvPTeleport.instance.getServer()
				.getOnlinePlayers()) {
			if (PvPListCommand.subscribed_players.contains(
						p.getUniqueId())) {
				p.sendMessage(msg + " (in pvp)");
			} else {
				p.sendMessage(msg);
			}
		}
	}

}

