package org.c4k3.PvPTeleport;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
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

public class EnchantmentBan implements Listener {

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

}
