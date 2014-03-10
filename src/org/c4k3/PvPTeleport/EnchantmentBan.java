package org.c4k3.PvPTeleport;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/* This class is responsible for handling the ban on enchantments in the pvp world */
public class EnchantmentBan implements Listener {

	/* Checks if player has contents not permitted in teleports world -> pvp, returns true if player has forbidden items */
	public static boolean TeleportCheck(Player player) {

		PlayerInventory inventory = player.getInventory();

		/* Checks the hotbar (item slots 0 through 8) */
		for ( int i = 0 ; i < 9 ; i++) {

			if ( inventory.getItem(i) != null )  { // Bukkit likes to throw a weird error if we try to call getItem on an empty slot
				if ( !inventory.getItem(i).getEnchantments().isEmpty() ) return true;
			}

		}

		/* Checks the armor slots (item slots 36 through 39) */
		for ( int i = 36 ; i < 40 ; i++) {

			if ( inventory.getItem(i) != null )  { // Bukkit likes to throw a weird error if we try to call getItem on an empty slot
				if ( !inventory.getItem(i).getEnchantments().isEmpty() ) return true;
			}

		}

		return false;

	}

	/* This method ensures that players do not move any enchanted items onto their hotbar whilst in the pvp world
	 * In this method, armor slots are 5 through 8, while hotbar slots are 9 through 17 */
	@EventHandler(priority = EventPriority.NORMAL,ignoreCancelled=true)
	public void onInventoryClickEvent(InventoryClickEvent event) {
				
		if ( !event.getWhoClicked().getWorld().getName().equals("pvp") ) return; // Return if this happens anywhere but the pvp world
		
		Bukkit.getLogger().info("Click event");

		ItemStack itemstack = event.getCursor();

		Bukkit.getLogger().info(event.getCursor().getEnchantments().toString());

		Bukkit.getLogger().info(String.valueOf(event.getSlot()));
		
		if ( event.getSlotType() == InventoryType.CONTAINER ) { 
			event.setCancelled(true);

		}

	}
	
	private List<Integer> tempStones = new ArrayList<Integer>(); // Lists the hotbar slots in which stone was temporarily put, used to remove them again after 1 ticks in the scheduler
	
	private ItemStack stone = new ItemStack(Material.STONE);

	
	/* This method ensures that enchanted items that are picked up are not put into the player's hotbar, but rather their inventory */
	@EventHandler(priority = EventPriority.NORMAL,ignoreCancelled=true)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		Bukkit.getLogger().info("Pickup event");
		
		if ( event.getItem().getItemStack().getEnchantments().isEmpty() ) return; // Ensures that this only acts upon enchanted items

		final PlayerInventory inventory = event.getPlayer().getInventory();
		
		for ( int i = 0 ; i < 9 ; i++ ) { // Hotbar slots are 0 through 8
			if ( inventory.getItem(i) == null ) {
				inventory.setItem(i, stone);
				tempStones.add(i);
			}
		}
		
		/* Schedule the removal of the stone blocks again */
		PvPTeleport.instance.getServer().getScheduler().scheduleSyncDelayedTask(PvPTeleport.instance, new Runnable() {
			public void run() {
				for ( int i : tempStones ) {
					inventory.clear(i);
				}
				tempStones.clear();
			}
		}, 1);

	}

}
