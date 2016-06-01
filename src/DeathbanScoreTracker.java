package net.simpvp.PvPTeleport;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DeathbanScoreTracker implements Listener {

	private static HashMap<UUID, UUID> lastAttacker = new HashMap<UUID, UUID>();

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {

		if ( !event.getEntity().getWorld().getName().equals("deathban") )
			return;

		if ( event.getEntity().getType() != EntityType.PLAYER )
			return; // damaged is not player

		if ( event.getDamager().getType() != EntityType.PLAYER && event.getDamager().getType() != EntityType.ARROW )
			return; // damager cannot possibly be a player

		Player damager;

		/* if damager is arrow shot by player, damager = arrow's shooter */
		if ( event.getDamager().getType() == EntityType.ARROW ) {
			Arrow arrow = (Arrow) event.getDamager();

			if ( !(arrow.getShooter() instanceof Player) )
				return ;

			damager = (Player) arrow.getShooter();

		} else {
			damager = (Player) event.getDamager();
		}

		lastAttacker.put(event.getEntity().getUniqueId(), damager.getUniqueId());

	}
	
	public static void lastAttackerRemove(UUID uuid) {
		lastAttacker.remove(uuid);
	}
	
	public static UUID lastAttackerGet(UUID uuid) {
		return lastAttacker.get(uuid);
	}

}
