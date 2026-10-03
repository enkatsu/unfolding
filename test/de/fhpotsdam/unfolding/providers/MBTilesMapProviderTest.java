package de.fhpotsdam.unfolding.providers;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.Test;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;

public class MBTilesMapProviderTest {

	/** Creates an MBTiles file with the given metadata, and no tiles. */
	private String createMBTiles(String... metadata) throws Exception {
		File file = File.createTempFile("unfolding-test", ".mbtiles");
		file.deleteOnExit();
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
				Statement stat = conn.createStatement()) {
			stat.execute("CREATE TABLE metadata (name text, value text);");
			stat.execute("CREATE TABLE tiles (zoom_level integer, tile_column integer, tile_row integer, tile_data blob);");
			for (int i = 0; i < metadata.length; i += 2) {
				stat.execute("INSERT INTO metadata VALUES ('" + metadata[i] + "', '" + metadata[i + 1] + "');");
			}
		}
		return file.getAbsolutePath();
	}

	@Test
	public void readsZoomLevelsAndCenter() throws Exception {
		MBTilesMapProvider provider = new MBTilesMapProvider(createMBTiles("minzoom", "10", "maxzoom", "12", "center",
				"-0.1275,51.507,11"));

		assertEquals(10, provider.minZoomLevel());
		assertEquals(12, provider.maxZoomLevel());
		assertEquals(51.507f, provider.defaultLocation().getLat(), 0.001f);
		assertEquals(-0.1275f, provider.defaultLocation().getLon(), 0.001f);
		assertEquals(11, provider.defaultZoomLevel());
	}

	@Test
	public void usesBoundsIfThereIsNoCenter() throws Exception {
		MBTilesMapProvider provider = new MBTilesMapProvider(createMBTiles("minzoom", "1", "maxzoom", "3", "bounds",
				"-10,40,20,60"));

		assertEquals(new Location(50, 5), provider.defaultLocation());
		assertEquals(1, provider.defaultZoomLevel());
	}

	@Test
	public void keepsDefaultsWithoutMetadata() throws Exception {
		MBTilesMapProvider provider = new MBTilesMapProvider(createMBTiles());

		assertEquals(0, provider.minZoomLevel());
		assertEquals(18, provider.maxZoomLevel());
		assertEquals(null, provider.defaultLocation());
	}

	@Test
	public void mapStartsAtCenterAndStaysInZoomRange() throws Exception {
		MBTilesMapProvider provider = new MBTilesMapProvider(createMBTiles("minzoom", "10", "maxzoom", "12", "center",
				"-0.1275,51.507,11"));
		UnfoldingMap map = new UnfoldingMap(new PApplet(), provider);

		assertEquals(11, map.getZoomLevel());

		map.zoomToLevel(3);
		assertEquals(10, map.getZoomLevel());
		map.zoomToLevel(16);
		assertEquals(12, map.getZoomLevel());
	}

}
