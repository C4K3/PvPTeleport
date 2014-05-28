package org.c4k3.PvPTeleport;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class EnchantmentBan implements Listener {

	private static final String errorMsg = ChatColor.RED + "Enchanted items are forbidden from use in the pvp world.";

	/** Checks if input player has any items with enchantments on any illegal slots (hotbar and armor slots)
	 * 
	 * Return true if has illegal items. Returns false else.
	 */
	public static boolean TeleportCheck(Player player) {

		PlayerInventory inventory = player.getInventory();

		/* Checks the hotbar (item slots 0 through 8) */
		for ( int i = 0 ; i < 9 ; i++) {

			if ( inventory.getItem(i) != null )  { // Bukkit likes to throw an annoyingly ambiguous error if we try to call getItem on an empty slot
				if ( !inventory.getItem(i).getEnchantments().isEmpty() ) return true; // Double negative (If Not Is Empty == if has enchantments -> return true)
			}

		}

		/* Checks the armor slots (item slots 36 through 39) */
		for ( int i = 36 ; i < 40 ; i++) {

			if ( inventory.getItem(i) != null )  {
				if ( !inventory.getItem(i).getEnchantments().isEmpty() ) return true;
			}

		}

		return false;

	}

	/** This method goes through all EntityDamageByEntityEvents, and cancels all where
	 * the damage was inflicted using an enchanted item */
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {

		Entity damaged = event.getEntity();

		/* Enchantments are only banned in the pvp world */
		if ( !damaged.getWorld().getName().equals("pvp") ) {
			return;
		}

		/* If the damager is a player, we store the player */
		Player player = null;
		if ( event.getDamager().getType() == EntityType.PLAYER ) {
			player = (Player) event.getDamager();

			/* Check that the attacking person did not use an enchanted item, if so, cancel the event */
			if ( checkWeapon(player) ) {
				event.setCancelled(true);
				player.sendMessage(errorMsg);
			}

		} 

		/* Check that the damaged is indeed a player */
		if ( damaged.getType() == EntityType.PLAYER ) {

			/* Check if the damaged person was wearing enchanted armor */
			if ( checkArmor((Player) damaged) ) {

				/* Kill player
				 * 
				 * It would be worthwhile to simply make the armor drop instead. But Bukkit does not have any Inventory.drop method
				 * And if you make it delete the item in the inventory and then spawn it on the ground, people can dupe items when the server lags (actually happened) */
				event.setDamage(255.0);

			}

		}

	}

	/** This function takes a player (damager) from a EntityDamageByEntityEvent and checks
	 * if the player used an enchanted item to damage.
	 * 
	 *  Returns true if used enchanted item to damage.
	 *  Else returns false */
	public boolean checkWeapon(Player player) {

		ItemStack itemInHand = player.getItemInHand();

		if ( itemInHand != null ) {
			if ( !itemInHand.getEnchantments().isEmpty() ) { // Double negative again. If has enchantments

				return true;

			}

		}

		return false;

	}

	/** This function takes a damaged entity and checks if they're illegally wearing enchanted armor (in the pvp world.)
	 * If they are, drop their armor.
	 * 
	 * Returns true if player is wearing illegal armor.
	 * Else returns false */
	public boolean checkArmor(Player damaged) {

		for ( ItemStack item : damaged.getInventory().getArmorContents() ) {

			if ( item != null ) {
				if ( !item.getEnchantments().isEmpty() ) { // Item is enchanted

					return true;

				}

			}

		}

		return false;

	}

	/** This method goes through all EntityShootBowEvent and cancels
	 * all events in the pvp world where the bow was enchanted */
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onEntityShootBowEvent(EntityShootBowEvent event) {

		/* If not in the pvp world, return */
		if ( !event.getEntity().getWorld().getName().equals("pvp") ) {
			return;
		}

		Player player;

		/* We only care if players shoot enchanted bows */
		if ( event.getEntityType() == EntityType.PLAYER ) {
			player = (Player) event.getEntity();
		}

		/* If skeletons or other mobs do, we don't care */
		else return;

		ItemStack bow = event.getBow();

		if ( !bow.getEnchantments().isEmpty() ) { // Bow has enchantments

			event.setCancelled(true);
			player.sendMessage(errorMsg);

		}

	}

	/** This method checks all BlockBreakEvents for any players breaking blocks
	 * with enchanted items. */
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onBlockBreakEvent(BlockBreakEvent event) {

		Player player = event.getPlayer();

		/* Enchantments are only banned in the pvp world */
		if ( !player.getWorld().getName().equals("pvp") ) {
			return;
		}

		ItemStack itemInHand = player.getItemInHand();

		if ( itemInHand != null ) {

			if ( !itemInHand.getEnchantments().isEmpty() ) { // Double negative again. If has enchantments

				event.setCancelled(true);
				player.sendMessage(errorMsg);

			}

		}

	}

	/** Checks that players fishing in the pvp world aren't using enchanted fishing rods */
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onPlayerFishEvent(PlayerFishEvent event) {

		PvPTeleport.instance.getLogger().info("PlayerFishEvent");

		Player player = event.getPlayer();

		ItemStack itemInHand = player.getItemInHand();

		if ( itemInHand != null ) {

			if ( !itemInHand.getEnchantments().isEmpty() ) { // Double negative again. If has enchantments

				event.setCancelled(true);
				player.sendMessage(errorMsg);

			}

		}

	}

}
