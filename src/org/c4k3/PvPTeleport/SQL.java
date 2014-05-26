package org.c4k3.PvPTeleport;

import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import lib.PatPeter.SQLibrary.SQLite;

public class SQL {
	
	private static SQLite sqlite;

	/** Opens the SQL connection */
	public static void sqlConnection() {
		
		sqlite = new SQLite(PvPTeleport.instance.getLogger(),
		"PvPWorld", // Database name
		"playerlocs", // Table name
		PvPTeleport.instance.getDataFolder().getAbsolutePath());
		try {
			sqlite.open();
			
			/* Checks if the playerlocs table already exists, if it does not, create it */
			if ( sqlite.checkTable("playerlocs")) {
				return;
			} else {
			sqlite.query("CREATE TABLE playerlocs (id INT PRIMARY KEY, playername VARCHAR(20), x DOUBLE, y DOUBLE, z DOUBLE);");
			sqlite.query("INSERT INTO playerlocs(playername, x, y, z) VALUES ('0', '0', '0', '0')");
			}
			
		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}
	}

	/** Inserts the player's data into the database */
	public static void putPlayer(Player player) {
		
		Location pLoc = player.getLocation();
		int x = pLoc.getBlockX();
		int y = pLoc.getBlockY();
		int z = pLoc.getBlockZ();
		String sPlayer = player.getName();
		
		/* This is easier... */
		sqlite.query("DELETE FROM playerlocs WHERE playername='" + sPlayer + "'");
		sqlite.query("INSERT INTO playerlocs(playername, x, y, z) VALUES ('" + sPlayer + "', '" + x + "', '" + y + "', '" + z + "')");
	}
	
	/** Retrieves the player's location from the playerlocs table, and turns it into a Bukkit.Location
	 * Returns null if it was unable to properly retrieve the location
	 */
	public static Location getPlayer(String splayer) {
		
		ResultSet rs = null;
		
		try {
			rs = sqlite.query("SELECT * FROM playerlocs WHERE playername = '" + splayer + "'");
			while (rs.next()) {
				
				double x = rs.getDouble("x");
				double y = rs.getDouble("y");
				double z = rs.getDouble("z");
				Location loc = new Location(Bukkit.getWorld("world"), x, y, z);
				return loc;
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		finally {
			try {
				rs.close();
				sqlite.query("DELETE FROM playerlocs WHERE playername='" + splayer + "'");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return null;
		
	}
	
}