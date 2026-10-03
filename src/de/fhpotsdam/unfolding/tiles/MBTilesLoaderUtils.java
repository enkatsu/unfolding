package de.fhpotsdam.unfolding.tiles;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import processing.core.PConstants;
import processing.core.PImage;

/**
 * Loads map tile images from a MBTiles SQLite database.
 * 
 * You need to provide the jdbcConnectionString to connect to the database file. e.g. "./data/my-map.mbtiles"
 * 
 * This class is part of the <a href="http://code.google.com/p/unfolding/">Unfolding</a> map library. See <a
 * href="http://tillnagel.com/2011/06/tilemill-for-processing/">TileMill for Processing</a> for more information.
 */
public class MBTilesLoaderUtils {

	public static final String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";

	private static boolean missingDriverReported = false;

	/**
	 * Loads the tile for given parameters as image.
	 * 
	 * @param column
	 *            The column of the tile.
	 * @param row
	 *            The row of the tile.
	 * @param zoomLevel
	 *            The zoom level of the tile.
	 * @param jdbcConnectionString
	 *            The path to the MBTiles database.
	 * @return The tile as PImage, or null if not found.
	 */
	public static PImage getMBTile(int column, int row, int zoomLevel, String jdbcConnectionString) {
		PImage img = null;
		try {
			byte[] tileData = getMBTileData(column, row, zoomLevel, jdbcConnectionString);
			if (tileData != null) {
				img = getAsImage(tileData);
			} else {
				// System.err.println("No tile found for " + column + "," + row + " " + zoomLevel);
			}
		} catch (ClassNotFoundException e) {
			reportMissingDriver();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return img;
	}

	private static synchronized void reportMissingDriver() {
		if (!missingDriverReported) {
			System.err.println("Could not load MBTiles: The SQLite JDBC driver (" + SQLITE_JDBC_DRIVER
					+ ") was not found. Download sqlite-jdbc from https://github.com/xerial/sqlite-jdbc/releases"
					+ " and put the jar file into the 'code' folder of your sketch.");
			missingDriverReported = true;
		}
	}

	/**
	 * Loads the metadata of the MBTiles database, e.g. minzoom, maxzoom, center, and bounds.
	 * 
	 * @param jdbcConnectionString
	 *            The path to the MBTiles database.
	 * @return The metadata as name-value pairs, or an empty map if it could not be loaded.
	 */
	public static Map<String, String> getMetadata(String jdbcConnectionString) {
		Map<String, String> metadata = new HashMap<String, String>();
		try {
			Connection conn = getConnection(jdbcConnectionString);
			try (Statement stat = conn.createStatement();
					ResultSet rs = stat.executeQuery("SELECT name, value FROM metadata;")) {
				while (rs.next()) {
					metadata.put(rs.getString("name"), rs.getString("value"));
				}
			}
		} catch (ClassNotFoundException e) {
			reportMissingDriver();
		} catch (SQLException e) {
			System.err.println("Could not read metadata of MBTiles " + jdbcConnectionString + ": " + e.getMessage());
		}
		return metadata;
	}

	private static synchronized Connection getConnection(String jdbcConnectionString) throws ClassNotFoundException,
			SQLException {
		Class.forName(SQLITE_JDBC_DRIVER);
		Connection conn = connectionsMap.get(jdbcConnectionString);
		if (conn == null) {
			conn = DriverManager.getConnection(jdbcConnectionString);
			connectionsMap.put(jdbcConnectionString, conn);
		}
		return conn;
	}

	protected static Map<String, Connection> connectionsMap = new HashMap<String, Connection>();

	/**
	 * Loads the MBTile data from the database as blob, and returns it as byte array.
	 * 
	 * @param column
	 *            The column of the tile.
	 * @param row
	 *            The row of the tile.
	 * @param zoomLevel
	 *            The zoom level of the tile.
	 * @return The tile as byte array with image information, or an empty array if not found.
	 */
	protected static byte[] getMBTileData(int column, int row, int zoomLevel, String jdbcConnectionString)
			throws Exception {
		Connection conn = getConnection(jdbcConnectionString);
		byte[] tileData = null;

		try (PreparedStatement prep = conn
				.prepareStatement("SELECT tile_data FROM tiles WHERE tile_column = ? AND tile_row = ? AND zoom_level = ?;")) {
			prep.setInt(1, column);
			prep.setInt(2, row);
			prep.setInt(3, zoomLevel);

			try (ResultSet rs = prep.executeQuery()) {
				while (rs.next()) {
					tileData = rs.getBytes("tile_data");
				}
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return tileData;
	}

	/**
	 * Converts the byte array into a PImage. Expects the byte array to be in ARGB (RGB with alpha channel).
	 * 
	 * Adapted from toxi
	 * 
	 * @param bytes
	 *            The image information as byte array.
	 * @return A PImage.
	 */
	protected static PImage getAsImage(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			BufferedImage bimg = ImageIO.read(bis);
			PImage img = new PImage(bimg.getWidth(), bimg.getHeight(), PConstants.ARGB);
			bimg.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
			img.updatePixels();
			return img;
		} catch (Exception e) {
			System.err.println("Can't create image from buffer");
			e.printStackTrace();
			return null;
		}
	}

}
