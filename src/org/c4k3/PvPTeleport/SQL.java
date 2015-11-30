package org.c4k3.PvPTeleport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SQL {

	private static Connection conn = null;

	public static void connect() {

		/* Where the last part is the name of the database file */
		String database = "jdbc:sqlite:plugins/PvPTeleport/PvPTeleport.sqlite";

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(database);

			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery("PRAGMA user_version;");

			rs.next();
			int user_version = rs.getInt("user_version");
			rs.close();

			switch (user_version) {

			case 0: {
				PvPTeleport.instance.getLogger().info("Database not yet created. Creating ...");

				/* Player locations in the overworld,
				 * used both when players are in the pvp world and deathban world */
				String query = "CREATE TABLE worldlocs "
						+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ "uuid BLOB,"
						+ "x INT,"
						+ "y INT,"
						+ "z INT);"

						/* Player locations in the deathban world */
						+ "CREATE TABLE deathbanlocs"
						+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ "uuid BLOB,"
						+ "x INT,"
						+ "y INT,"
						+ "z INT,"
						+ "time DATETIME DEFAULT CURRENT_TIMESTAMP));"

						+ "CREATE TABLE deathbandeaths"
						+ "dead_uuid BLOB," // uuid of the player who died
						+ "killer_uuid BLOB," // uuid of the player who killed the person
						+ "death_message TEXT);"

						+ "PRAGMA user_version = 2;";
				st.executeUpdate(query);
				break;
			}

			case 1: {
				PvPTeleport.instance.getLogger().info("Upgrading database to version 2");
				// FIXME: Do the upgrade
				String query = "";

				st.executeUpdate(query);
				break;
			}
			}

			st.close();
		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
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
	 * Inserts the coords of the given player into the worldlocs table
	 * @param player Player who is teleporting
	 */
	public static void worldLocsInsert(Player player) {
		try {
			String query = "INSERT INTO worldlocs (uuid, x, y, z) VALUES (?, ?, ?, ?);";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, player.getUniqueId().toString());
			st.setInt(2, player.getLocation().getBlockX());
			st.setInt(3, player.getLocation().getBlockY());
			st.setInt(4, player.getLocation().getBlockZ());
			st.executeUpdate();
			st.close();
		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}
	}

	public static Location worldLocsGet(UUID uuid) {
		Location ret = null;
		try {
			String query = "SELECT * FROM worldlocs WHERE uuid = ?;";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1,  uuid.toString());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				ret = new Location(
						PvPTeleport.instance.getServer().getWorld("world"),
						(double) rs.getInt("x") + 0.5,
						(double) rs.getInt("y") + 0.5,
						(double) rs.getInt("z") + 0.5);
			}
			rs.close();
			st.close();

		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}
		return ret;
	}

	public static void worldLocsRemove(UUID uuid) {
		try {
			String query = "DELETE FROM worldlocs WHERE uuid = ?;";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, uuid.toString());
			st.executeUpdate();
			st.close();
		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}
	}

	public static void deathbanLocsInsert(Player player) {
		try {
			String query = "INSERT INTO deathbanlocs (uuid, x, y, z) VALUES (?, ?, ?, ?);";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, player.getUniqueId().toString());
			st.setInt(2, player.getLocation().getBlockX());
			st.setInt(3, player.getLocation().getBlockY());
			st.setInt(4, player.getLocation().getBlockZ());
			st.executeUpdate();
			st.close();
		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}
	}

	// FIXME to return a pair of a location and a time
	public static Location deathbanLocsGet(UUID uuid) {
		Location ret = null;
		try {
			String query = "SELECT * FROM deathbanlocs WHERE uuid = ?;";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1,  uuid.toString());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				ret = new Location(
						PvPTeleport.instance.getServer().getWorld("deathban"),
						(double) rs.getInt("x") + 0.5,
						(double) rs.getInt("y") + 0.5,
						(double) rs.getInt("z") + 0.5);
			}
			rs.close();
			st.close();

		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}
		return ret;
	}

	public static void deathbanLocsRemove(UUID uuid) {
		try {
			String query = "DELETE FROM deathbanlocs WHERE uuid = ?;";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, uuid.toString());
			st.executeUpdate();
			st.close();
		} catch (Exception e) {
			PvPTeleport.instance.getLogger().info(e.getMessage());
		}
	}


}
