package net.simpvp.PvPTeleport;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffectType;

/**
 * This class handles the /world command. It runs the required checks that the player can run the command,
 * and then it passes it on to the relevant method in PvPTransportation.
 */
public class WorldCommand implements CommandExecutor {

	/** Teleports player to a random location in the pvp world
	 * Returns true if it was properly handled (teleport successful, or player was deteremined ineligible for teleport)
	 * Returns false if it failed
	 */
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Player player = null;
		if (sender instanceof Player){
			player = (Player) sender;
		}

		/* Checks that sender is a player and not console */
		if ( player == null ) {
			PvPTeleport.instance.getLogger().info("Only players can use this command.");
			return true;
		}

		String sWorld = player.getWorld().getName();

		/* Checks that they are either in the pvp world or overworld */
		if ( !sWorld.equals("world") && !sWorld.equals("pvp") ) {
			player.sendMessage(ChatColor.RED + "You must be in the overworld to use this command.");
			return true;
		}

		String tCheck = teleportCheck(player);

		/* Checks that they do not fail the teleportCheck (combatlog check) */
		if ( tCheck != null ) { 
			player.sendMessage(tCheck);
			return true;
		}

		if ( sWorld.equals("world") ) {

			PvPTransportation.teleportToPvP(player);

			return true;

		}

		/* If the person using the command is in the pvp world, then they shall be teleport back to the pvp world. */
		else if ( sWorld.equals("pvp") ) {

			TeleportBack.teleportBack(player);

		}

		return true;

	}

	/** Checks that player is not trying to combatlog/is allowed to teleport
	 * Returns an error message to be displayed if the player is not allowed to teleport
	 * Returns null if the player is allowed to teleport
	 */
	private static String teleportCheck(Player player) {

		Location pLoc = player.getLocation();

		World world = player.getWorld();

		/* Check if there are any players within 50 blocks */
		for ( Player p : world.getPlayers() ) {

			if ( !p.equals(player) && p.getLocation().distance(pLoc) < 50 && player.canSee(p) ) return ChatColor.RED + "You cannot use this command while within 50 blocks of any other players.";

		}

		/* Check if there are any hostile mobs within 5 blocks */
		for ( Entity entity : world.getEntitiesByClasses(Blaze.class, Creeper.class, Enderman.class, Ghast.class, PigZombie.class, Skeleton.class,
				Spider.class, Witch.class, CaveSpider.class, Slime.class, MagmaCube.class, Silverfish.class, Zombie.class) ) {

			if ( entity.getLocation().distance(pLoc) < 5 ) return ChatColor.RED + "You cannot use this command while within 5 blocks of any hostile mobs.";

		}
		
		/* Check if the player is falling */
		if ( player.getVelocity().getY() < - 0.079 || player.getVelocity().getY() > 0.08 )
			return ChatColor.RED + "You cannot use this command while falling.";
			
		/* Check if the player is burning */
		if ( player.getFireTicks() > 0 && !player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE) ) 
			return ChatColor.RED + "You cannot use this command while on fire.";

		/* Default to allow teleport */
		return null;

	}

}
