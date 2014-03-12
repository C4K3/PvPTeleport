package org.c4k3.PvPTeleport;

import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import lib.PatPeter.SQLibrary.SQLite;

public class SQL {
	
	private static SQLite sqlite;
	
	public static void sqlConnection() {
		/** Opens the SQL connection */
		
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
	
	public static void putPlayer(String splayer, int x, int y, int z) {
		/** Inserts the player's coordinates into the database */
		sqlite.query("DELETE FROM playerlocs WHERE playername='" + splayer + "'");
		sqlite.query("INSERT INTO playerlocs(playername, x, y, z) VALUES ('" + splayer + "', '" + x + "', '" + y + "', '" + z + "')");
	}
	
	public static Location getPlayer(String splayer) {
		/** Retrieves the player's location from the playerlocs table, and turns it into a Bukkit.Location
		 * Returns null if it was unable to properly retrieve the location
		 */
		
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