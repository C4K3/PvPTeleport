package org.c4k3.PvPTeleport;

import org.bukkit.plugin.java.JavaPlugin;

public class PvPTeleport extends JavaPlugin {

	public static JavaPlugin instance;

	public void onEnable() {
		instance = this;
        getServer().getPluginManager().registerEvents(new EnchantmentBan(), this);
		getCommand("world").setExecutor(new CommandHandler());
		getCommand("pvplist").setExecutor(new CommandHandler());
		SQL.sqlConnection();
	}

	public void onDisable() {

	}

}
