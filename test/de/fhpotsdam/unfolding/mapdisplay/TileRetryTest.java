package de.fhpotsdam.unfolding.mapdisplay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import processing.core.PApplet;
import processing.core.PImage;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.core.Coordinate;
import de.fhpotsdam.unfolding.providers.EmptyMapProvider;
import de.fhpotsdam.unfolding.tiles.TileLoader;

public class TileRetryTest {

	private PApplet p;
	private AbstractMapDisplay mapDisplay;
	private Coordinate coord;

	@Before
	public void before() {
		p = new PApplet();
		mapDisplay = new UnfoldingMap(p, new EmptyMapProvider()).mapDisplay;
		mapDisplay.queue.clear();
		coord = new Coordinate(2, 4, 3);
	}

	/** Simulates loading the tile, with the given result (null if it could not be loaded). */
	private void loadTile(Object image) {
		mapDisplay.pending.put(coord, new TileLoader(p, mapDisplay.provider, mapDisplay, coord));
		mapDisplay.tileLoaded(coord, image);
	}

	@Test
	public void failedTileIsNotStoredAndLoadedAgainAfterDelay() {
		loadTile(null);

		assertFalse(mapDisplay.images.containsKey(coord));
		assertFalse(mapDisplay.pending.containsKey(coord));

		// Waits before loading again
		mapDisplay.grabTile(coord);
		assertFalse(mapDisplay.queue.contains(coord));

		// Loads again after the delay
		mapDisplay.tileRetryTimes.put(coord, System.currentTimeMillis() - 1);
		mapDisplay.grabTile(coord);
		assertTrue(mapDisplay.queue.contains(coord));
	}

	@Test
	public void tileIsStoredWhenLoadedAfterFailure() {
		loadTile(null);
		PImage tile = new PImage(256, 256);
		loadTile(tile);

		assertEquals(tile, mapDisplay.images.get(coord));
		assertFalse(mapDisplay.failedTileLoads.containsKey(coord));
		assertFalse(mapDisplay.tileRetryTimes.containsKey(coord));
	}

	@Test
	public void transparentTileIsStoredAfterMaxAttempts() {
		for (int i = 0; i < mapDisplay.maxTileLoadAttempts - 1; i++) {
			loadTile(null);
			assertFalse(mapDisplay.images.containsKey(coord));
		}
		loadTile(null);

		PImage tile = (PImage) mapDisplay.images.get(coord);
		assertEquals(256, tile.width);
		assertEquals(0, tile.get(128, 128) >>> 24);
		assertFalse(mapDisplay.failedTileLoads.containsKey(coord));
	}

}
