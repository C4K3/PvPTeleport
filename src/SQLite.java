package net.simpvp.PvPTeleport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SQLite {

	private static Connection conn = null;

	/**
	 * Opens the SQLite connection.
	 */
	public static void connect() {

		/* Where the last part is the name of the database file */
		String database = "jdbc:sqlite:plugins/PvPTeleport/PvPTeleport.sqlite";

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(database);

			Statement st = conn.createStatement();

			/* Get database version */
			ResultSet rs = st.executeQuery("PRAGMA user_version;");

			rs.next();
			int user_version = rs.getInt("user_version");

			rs.close();

			switch (user_version) {

			/* Database is brand new. Create tables */
			case 0: {
				PvPTeleport.instance.getLogger().info("Database not yet created. Creating ...");
				String query = "CREATE TABLE worldlocs " // Player locations in the 'world' world. Used when players teleport to both deathban world and pvpworld
						+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ "uuid BLOB,"
						+ "x INT,"
						+ "y INT,"
						+ "z INT);"

						+ "CREATE TABLE deathbanlocs" // Player locations in the deathban world. Used when players teleport out of deathban world.
						+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ "uuid BLOB,"
						+ "x INT,"
						+ "y INT,"
						+ "z INT);"

						+ "CREATE TABLE deathbandata" // Data for each player in the current deathban tournament.
						+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ "uuid BLOB,"
						+ "points INT," // 1 point = 1 kill.
						+ "status INT);"; // 0 = not dead. 1 = dead.
				st.executeUpdate(query);
				query = "PRAGMA user_version = 1;";
				st.executeUpdate(query);
				break;
			}

			}

			st.close();

		} catch ( Exception e ) {
			PvPTeleport.instance.getLogger().info(e.getMessage() );
			return;
		}

	}

	/**
	 * Closes the database connection.
	 */
	public static void close() {
		try {
			conn.close();
		} catch(Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}

	}

	/**
	 * Inserts the player's location into the worldlocs table.
	 * @param player The player whose coordinates are to be inserted.
	 */
	public static void worldLocsInsert(Player player) {

		try {

			Statement st = conn.createStatement();

			Location pLoc = player.getLocation();
			int x = pLoc.getBlockX();
			int y = pLoc.getBlockY();
			int z = pLoc.getBlockZ();
			UUID uuid = player.getUniqueId();

			/* This is easier... */
			st.executeUpdate("DELETE FROM worldlocs WHERE uuid='" + uuid + "';");
			st.executeUpdate("INSERT INTO worldlocs(uuid, x, y, z) VALUES ('" + uuid + "', '" + x + "', '" + y + "', '" + z + "');");

			st.close();

		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}

	}

	/**
	 * Returns the saved Location object for the input UUID from the worldlocs table.
	 * ( For players returning from the pvp world.)
	 * @param uuid UUID of the player.
	 * @return Location of player's location in 'world'.
	 */
	public static Location worldLocsGet(UUID uuid) {

		Location loc = null;

		try {

			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery("SELECT * FROM worldlocs WHERE uuid = '" + uuid + "'");
			while (rs.next()) {

				double x = rs.getDouble("x") + 0.5;
				double y = rs.getDouble("y");
				double z = rs.getDouble("z") + 0.5;
				loc = new Location(PvPTeleport.instance.getServer().getWorld("world"), x, y, z);

			}

			rs.close();
			st.close();

		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}

		return loc;

	}

	/**
	 * Remove player's data from the worldlocs table.
	 * @param uuid UUID of the player.
	 */
	public static void worldLocsRemove(UUID uuid) {

		try {

			Statement st = conn.createStatement();

			st.executeUpdate("DELETE FROM worldlocs WHERE uuid='" + uuid + "';");

		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}

	}

	/**
	 * Inserts the player's location into the deathbanlocs table.
	 * @param player The player whose coordinates are to be inserted.
	 */
	public static void deathBanLocsInsert(Player player) {

		try {

			Statement st = conn.createStatement();

			Location pLoc = player.getLocation();
			int x = pLoc.getBlockX();
			int y = pLoc.getBlockY();
			int z = pLoc.getBlockZ();
			UUID uuid = player.getUniqueId();

			/* This is easier... */
			st.executeUpdate("DELETE FROM deathbanlocs WHERE uuid='" + uuid + "';");
			st.executeUpdate("INSERT INTO deathbanlocs(uuid, x, y, z) VALUES ('" + uuid + "', '" + x + "', '" + y + "', '" + z + "');");

			st.close();

		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}

	}

	/**
	 * Removes player from the deathbanlocs table.
	 * @param uuid UUID of the player to be removed.
	 */
	public static void deathBanLocsRemove(UUID uuid) {

		try {

			Statement st = conn.createStatement();

			String query = "DELETE FROM deathbanlocs WHERE uuid='" + uuid + "';";
			st.executeUpdate(query);

			st.close();

		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}

	}

	/**
	 * Retrieves the player's location from the deathbanlocs table.
	 * @param uuid The UUID of the player whose location is to be retrieved.
	 * @return Bukkit.Location of where to teleport the player. Null if not properly retrieved.
	 */
	public static Location deathBanLocsGet(UUID uuid) {

		Location loc = null;

		try {

			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery("SELECT * FROM deathbanlocs WHERE uuid = '" + uuid + "'");
			while (rs.next()) {

				double x = rs.getDouble("x") + 0.5;
				double y = rs.getDouble("y");
				double z = rs.getDouble("z") + 0.5;
				loc = new Location(PvPTeleport.instance.getServer().getWorld("deathban"), x, y, z);

			}

			rs.close();
			st.close();

		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}

		return loc;

	}

	/**
	 * Gets a player's points from the deathbandata table.
	 * @param uuid UUID of player.
	 * @return Amount of points (kills) player has.
	 */
	public static int deathBanGetPoints(UUID uuid) {

		int points = -1;

		try {

			Statement st = conn.createStatement();

			String query = "SELECT * FROM deathbandata WHERE uuid ='" + uuid + "';";
			ResultSet rs = st.executeQuery(query);

			while ( rs.next() ) {
				points = rs.getInt("points");
			}

			rs.close();
			st.close();

		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}

		return points;

	}

	/**
	 * Creates a new entry for player in the deathbandata table. Used when player is joining deathban world for first time (this round.)
	 * @param uuid UUID of player.
	 */
	public static void deathBanDataInsert(UUID uuid) {

		try {

			Statement st = conn.createStatement();

			String query = "INSERT INTO deathbandata(uuid, points, status) VALUES ('" + uuid + "', '0', '0');"; // Start as not dead with 0 points
			st.executeUpdate(query);

			st.close();

		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}

	}

	/**
	 * Adds 1 point to the player's points. Called when player kills somebody.
	 * @param uuid UUID of player who got a point.
	 */
	public static void deathBanIncrementPoints(UUID uuid) {

		try {

			Statement st = conn.createStatement();

			String query = "UPDATE deathbandata SET points = points + 1 WHERE uuid = '" + uuid + "';";
			st.executeUpdate(query);

			st.close();

		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}

	}

	/**
	 * Gets the status of player from the deathbandata table.
	 * @param uuid UUID of the player whose status is to be retrieved.
	 * @return The status of the player. -1 is haven't joined yet. 0 is active in game. 1 is dead.
	 */
	public static int deathBanGetStatus(UUID uuid) {

		/* Default value if player is not in table. */
		int status = -1;

		try {

			Statement st = conn.createStatement();

			String query = "SELECT * FROM deathbandata WHERE uuid='" + uuid + "';";
			ResultSet rs = st.executeQuery(query);

			while ( rs.next() ) {
				status = rs.getInt("status");
			}

			rs.close();
			st.close();

		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}

		return status;

	}

	/**
	 * Sets player's status to status in the deathbandata table. Called when player dies.
	 * @param uuid UUID of player who died.
	 * @param status Status to be set.
	 */
	public static void deathBanSetStatus(UUID uuid, int status) {

		try {

			Statement st = conn.createStatement();

			String query = "UPDATE deathbandata SET status = '" + status + "' WHERE uuid='" + uuid + "';";
			st.executeUpdate(query);

			st.close();

		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}

	}

}