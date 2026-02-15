package net.simpvp.PvPTeleport;

import org.bukkit.Bukkit;
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

		Player player = event.getPlayer();
		removePlayerFromOldWorld(player);

		UUID uuid = player.getUniqueId();
		if (SQLite.pvplistSubscribeGet(uuid)) {
			PvPListCommand.subscribed_players.add(uuid);
		}
	}

	private void removePlayerFromOldWorld(Player player) {
		if (!player.getWorld().getName().equals("pvp")) return;

		long lastReset = PvPTeleport.pvpLastReset;
		if (lastReset <= 0L) return;

		if (!player.hasPlayedBefore()) return;

		long lastPlayed = player.getLastPlayed();
		if (lastPlayed <= 0L || lastPlayed >= lastReset) return;

		/* Delay to ensure the player is fully joined */
		Bukkit.getScheduler().runTask(PvPTeleport.instance, () -> {

			if (!player.isOnline()) return;
			if (!player.getWorld().getName().equals("pvp")) return;

			try {
				TeleportBack.teleportBack(player);
			} catch (Exception e) {
				PvPTeleport.instance.getLogger().warning(
						"Failed to remove " + player.getName() + " from pvp on join"
				);
				e.printStackTrace();
			}
		});
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

