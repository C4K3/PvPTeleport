package org.c4k3.PvPTeleport;

import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import lib.PatPeter.SQLibrary.SQLite;

/* This class is reponsible for opening and
 * closing the SQL connection, and creating
 * the database + table(s) if it does not exist
 * and writing and reading from the SQLite database */
public class SQL {
	
	private static SQLite sqlite;
	
	/* Opens the sql connection
	 * Checks if the database exists, if it does not, creates it */
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
	
	/* Inserts the players coordinates into the sqlite database */
	public static void putPlayer(String splayer, int x, int y, int z) {
		sqlite.query("DELETE FROM playerlocs WHERE playername='" + splayer + "'");
		sqlite.query("INSERT INTO playerlocs(playername, x, y, z) VALUES ('" + splayer + "', '" + x + "', '" + y + "', '" + z + "')");
	}
	
	/* Retrieves the players location from the playerlocs table, and turns it into a Bukkit Location
	 * Returns null if it was unable to properly retrieve the location */
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