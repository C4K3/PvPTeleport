package org.c4k3.PvPTeleport;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.World;

import com.google.common.collect.Sets;

/** Handles transportation from 'world' to 'pvp' and 'deathban'
 * 
 * Generally called by WorldCommand and DeathbanCommand.
 */
public class TeleportFromWorld {

	/**
	 * Blindly teleports player to a random location in the given world.
	 * Assumes all checks have been completed already.
	 * @param player Player to teleport.
	 */
	public static void teleport(Player player, World world) {

		Location loc = null;
		if (world.getName() == "deathban")
			loc = SQLite.deathbanLocsGet(player.getUniqueId());

		if (loc == null)
			loc = randomSpawn(world);

		/* If unable to get random spawn location */
		if (loc == null) {
			player.sendMessage(ChatColor.RED + "Unable to get a spawn location for you.\n"
					+ "Please try again.\n"
					+ "If this problem persists, please contact an admin for assistance.");
			return;
		}

		/* Teleporting will glitch if a player is inside a vehicle */
		if (player.isInsideVehicle())
			player.leaveVehicle();

		SQLite.worldLocsInsert(player);

		player.teleport(loc);
		player.sendMessage(ChatColor.GOLD + "Teleporting you to the " + world.getName() + " world.");
		PvPTeleport.instance.getLogger().info("Teleporting " + player.getName() + " to the " + world.getName() + " world.");

	}

	final private static HashSet<Material> nonsolids = new HashSet<Material>() {
		private static final long serialVersionUID = 6875191268840511805L;
		{
			add(Material.AIR);
			add(Material.SAPLING);
			add(Material.GRASS);
			add(Material.DEAD_BUSH);
			add(Material.YELLOW_FLOWER);
			add(Material.RED_ROSE);
			add(Material.BROWN_MUSHROOM);
			add(Material.RED_MUSHROOM);
			add(Material.TORCH);
			add(Material.SIGN);
			add(Material.SIGN_POST);
		}};

		final private static HashSet<Material> solids = new HashSet<Material>() {
			private static final long serialVersionUID = -5587257811894691550L;
			{
				add(Material.GRASS);
				add(Material.DIRT);
				add(Material.COBBLESTONE);
				add(Material.BEDROCK);
				add(Material.WATER);
				add(Material.STATIONARY_WATER);
				add(Material.SAND);
				add(Material.SANDSTONE);
			}};

			/**
			 * Gives a random spawn location in the pvp world
			 * @return The random location.
			 */
			private static Location randomSpawn(World world) {
				Random randomGenerator = new Random();

				Material blocktype = null;
				Material blocktype1 = null;
				Material blocktype2 = null;

				Location spawnLoc = new Location(world, 0, 0, 0);

				int counter = 0;

				while (counter < 1000000) {

					Double y = 60.0;
					Double x = (double) randomGenerator.nextInt(450) - 225.5;
					Double z = (double) randomGenerator.nextInt(450) - 225.5;

					spawnLoc.setX(x);
					spawnLoc.setY(y);
					spawnLoc.setZ(z);

					blocktype = spawnLoc.getBlock().getType();

					/* While the block at the given x and z coords is of one of the below types, we check if the two blocks above it are
					 * empty blocks that would allow the player to teleport. */
					while (solids.contains(blocktype) && y <= 250) {
						spawnLoc.setY(y + 1);
						blocktype1 = spawnLoc.getBlock().getType();

						spawnLoc.setY(y + 2);
						blocktype2 = spawnLoc.getBlock().getType();

						/* If the two blocks above the y position are valid nonsolids, then we got a good location */
						if (nonsolids.contains(blocktype1) && nonsolids.contains(blocktype2)) {

							PvPTeleport.instance.getLogger().info("Found valid pvp world spawn location on attempt " + counter + ".");
							return spawnLoc;
						}

						/* Increment y by one, to test that location */
						y++;
						spawnLoc.setY(y);
						blocktype = spawnLoc.getBlock().getType();

					}

					counter++;

				}

				PvPTeleport.instance.getLogger().info("Tried over 1000 times to find a suitable spawn, no result.");
				return null;

			}

}
