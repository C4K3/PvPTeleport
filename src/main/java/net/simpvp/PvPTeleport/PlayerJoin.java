package net.simpvp.PvPTeleport;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerJoin implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		JoinMessageAppend(event);

		UUID uuid = event.getPlayer().getUniqueId();
		if (SQLite.pvplistSubscribeGet(uuid)) {
			PvPListCommand.subscribed_players.add(uuid);
		}
	}

	/**
	 * Appends '(in pvp)' to the player login messages for any player
	 * logging in in the pvp world. Though it only shows the changed message
	 * to people who have used /pvplist subscribe
	 */
	private void JoinMessageAppend(PlayerJoinEvent event) {
		if (!event.getPlayer().getWorld().getName().equals("pvp")) {
			return;
		}

		String msg = event.getJoinMessage();
		event.setJoinMessage("");

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

