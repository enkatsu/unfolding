package de.fhpotsdam.unfolding;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.EmptyMapProvider;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

public class TweeningTest {

	private static final Location LOCATION = new Location(52.5162, 13.3777);

	private UnfoldingMap createTweeningMap() {
		UnfoldingMap map = new UnfoldingMap(new PApplet(), 0, 0, 600, 400, new EmptyMapProvider());
		map.setTweening(true);
		return map;
	}

	private void finishTweening(UnfoldingMap map) {
		for (int i = 0; i < 300; i++) {
			map.updateMap();
		}
	}

	private void assertCentered(UnfoldingMap map, Location location) {
		ScreenPosition pos = map.mapDisplay.getScreenPositionFloat(location);
		assertEquals(300, pos.x, 0.5f);
		assertEquals(200, pos.y, 0.5f);
	}

	@Test
	public void zoomAndPanToEndsAtLocation() {
		for (int zoomLevel : new int[] { 5, 10, 14, 18 }) {
			UnfoldingMap map = createTweeningMap();
			map.zoomAndPanTo(zoomLevel, LOCATION);
			finishTweening(map);

			assertEquals(zoomLevel, map.getZoomLevel());
			assertCentered(map, LOCATION);
		}
	}

	@Test
	public void zoomAndPanToFitEndsAtCenter() {
		UnfoldingMap map = createTweeningMap();
		map.zoomAndPanToFit(Arrays.asList(new Location(52.51, 13.37), new Location(52.52, 13.39)));
		finishTweening(map);

		assertCentered(map, new Location(52.515, 13.38));
	}

}
