package org.c4k3.PvPTeleport;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

public class PvPTeleport extends JavaPlugin {

	public static JavaPlugin instance;

	public void onEnable() {
		instance = this;

		/* Check if this plugin's directory exists, if not create it */
		File dir = new File("plugins/PvPTeleport");
		if ( !dir.exists() ) {
			dir.mkdir();
		}

		getServer().getPluginManager().registerEvents(new EnchantmentBan(), this);
		getServer().getPluginManager().registerEvents(new PlayerDisconnect(), this);
		getServer().getPluginManager().registerEvents(new EntityDamage(), this);
		getCommand("world").setExecutor(new WorldCommand());
		getCommand("pvplist").setExecutor(new PvPListCommand());
		getCommand("deathban").setExecutor(new DeathbanCommand());

		SQLite.connect();
	}

	public void onDisable() {
		SQLite.close();
	}

}
