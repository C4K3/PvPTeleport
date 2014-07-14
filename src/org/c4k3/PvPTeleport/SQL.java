package org.c4k3.PvPTeleport;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import lib.PatPeter.SQLibrary.SQLite;
import lib.com.evilmidget38.UUIDFetcher;

public class SQL {

	private static SQLite sqlite;

	/** Opens the SQL connection */
	public static void sqlConnection() {

		sqlite = new SQLite(PvPTeleport.instance.getLogger(),
				"PvPWorld", // Database name
				"playerlocs", // Database filename
				PvPTeleport.instance.getDataFolder().getAbsolutePath());
		try {
			sqlite.open();

			/* Checks if the playerlocs table already exists, if it does not, create it */
			if ( sqlite.checkTable("playerlocs")) {
				ResultSet rs = null;
				int userVersion = 0;

				rs = sqlite.query("PRAGMA user_version;");

				while ( rs.next() ) {
					userVersion = rs.getInt("user_version");
				}

				if ( userVersion == 0 ) {

					PvPTeleport.instance.getLogger().info("Detected that this database does not use UUIDs yet, beginning conversion ...");

					/* Update database */
					sqlite.query("ALTER TABLE playerlocs ADD uuid BLOB;");
					sqlite.query("PRAGMA user_version = 1;");

					sqlite.query("DELETE FROM playerlocs WHERE playername='0';");

					rs = sqlite.query("SELECT * FROM playerlocs");

					/* Create a list of players to get the UUIDs of */
					List<String> players = new ArrayList<String>();

					while ( rs.next() ) {
						players.add(rs.getString("playername"));
					}

					/* then we get the UUIDs */
					UUIDFetcher fetcher = new UUIDFetcher(players);
					Map<String, UUID> uuids = null;
					uuids = fetcher.call();

					/* Then we add the playernames */
					for ( String player : players ) {
						UUID uuid = uuids.get(player);

						sqlite.query("UPDATE playerlocs SET uuid='" + uuid + "' WHERE playername='" + player + "';");

					}

					/* Drop column playername. SQLite doesn't have ALTER TABLE DROP COLUMN so this workaround must be used */
					sqlite.query("CREATE TEMPORARY TABLE backup(uuid, x, y, z);"
							+ "INSERT INTO backup SELECT uuid,x,y,z FROM playerlocs;"
							+ "DROP TABLE playerlocs;"
							+ "CREATE TABLE playerlocs(uuid BLOB, x DOUBLE, y DOUBLE, z DOUBLE);"
							+ "INSERT INTO playerlocs(uuid, x, y, z) SELECT uuid,x,y,z FROM backup;"
							+ "DROP TABLE backup;");

					PvPTeleport.instance.getLogger().info("All done with database name -> UUID conversion. Enjoy!");

				}

				return;
			} else {
				sqlite.query("CREATE TABLE playerlocs (id INT PRIMARY KEY, uuid BLOB, x DOUBLE, y DOUBLE, z DOUBLE);");
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
		UUID uuid = player.getUniqueId();

		/* This is easier... */
		sqlite.query("DELETE FROM playerlocs WHERE uuid='" + uuid + "'");
		sqlite.query("INSERT INTO playerlocs(uuid, x, y, z) VALUES ('" + uuid + "', '" + x + "', '" + y + "', '" + z + "')");
	}

	/** Retrieves the player's location from the playerlocs table, and turns it into a Bukkit.Location
	 * Returns null if it was unable to properly retrieve the location
	 */
	public static Location getPlayer(UUID uuid) {

		ResultSet rs = null;

		try {
			rs = sqlite.query("SELECT * FROM playerlocs WHERE uuid = '" + uuid + "'");
			while (rs.next()) {

				double x = rs.getDouble("x") + 0.5;
				double y = rs.getDouble("y");
				double z = rs.getDouble("z") + 0.5;
				Location loc = new Location(PvPTeleport.instance.getServer().getWorld("world"), x, y, z);
				return loc;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			try {
				rs.close();
				sqlite.query("DELETE FROM playerlocs WHERE playername='" + uuid + "'");
			}
			catch (Exception e) {
				e.printStackTrace();
			}

		}

		return null;

	}

}