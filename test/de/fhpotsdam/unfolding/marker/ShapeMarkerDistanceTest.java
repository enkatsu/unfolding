package de.fhpotsdam.unfolding.marker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.EmptyMapProvider;
import de.fhpotsdam.unfolding.utils.GeoUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

public class ShapeMarkerDistanceTest {

	private static final double TOLERANCE_KM = 1;

	private SimplePolygonMarker createSquare() {
		List<Location> square = Arrays.asList(new Location(50, 10), new Location(50, 12), new Location(52, 12),
				new Location(52, 10));
		return new SimplePolygonMarker(square);
	}

	@Test
	public void distanceToLineIsMeasuredToNearestEdge() {
		SimpleLinesMarker line = new SimpleLinesMarker(new Location(50, 0), new Location(50, 40));

		// On the line, far from its centroid
		assertEquals(0, line.getDistanceTo(new Location(50, 1)), TOLERANCE_KM);
		// Next to the line
		assertEquals(GeoUtils.getDistance(new Location(51, 5), new Location(50, 5)),
				line.getDistanceTo(new Location(51, 5)), TOLERANCE_KM);
		// Beyond the end of the line
		assertEquals(GeoUtils.getDistance(new Location(50, -2), new Location(50, 0)),
				line.getDistanceTo(new Location(50, -2)), TOLERANCE_KM);
	}

	@Test
	public void distanceToLongLineIsZeroOnTheDrawnLine() {
		// Lines are drawn straight in the map's (Mercator) projection, not in latitude and longitude
		UnfoldingMap map = new UnfoldingMap(new PApplet(), 0, 0, 800, 600, new EmptyMapProvider());
		map.zoomAndPanTo(3, new Location(20, 0));
		Location start = new Location(-30, -60);
		Location end = new Location(60, 60);
		SimpleLinesMarker line = new SimpleLinesMarker(start, end);

		ScreenPosition startPos = map.mapDisplay.getScreenPositionFloat(start);
		ScreenPosition endPos = map.mapDisplay.getScreenPositionFloat(end);
		for (float t = 0.1f; t < 1; t += 0.1f) {
			Location onDrawnLine = map.getLocation(startPos.x + t * (endPos.x - startPos.x), startPos.y + t
					* (endPos.y - startPos.y));
			assertEquals(0, line.getDistanceTo(onDrawnLine), TOLERANCE_KM);
		}
	}

	@Test
	public void distanceToPolygonIsZeroInside() {
		assertEquals(0, createSquare().getDistanceTo(new Location(51, 11)), 0);
	}

	@Test
	public void distanceToPolygonIsMeasuredToNearestEdgeOutside() {
		assertEquals(GeoUtils.getDistance(new Location(51, 13), new Location(51, 12)),
				createSquare().getDistanceTo(new Location(51, 13)), TOLERANCE_KM);
	}

	@Test
	public void distanceToPolygonIsMeasuredToHoleInsideHole() {
		SimplePolygonMarker polygon = createSquare();
		List<Location> hole = Arrays.asList(new Location(50.5, 10.5), new Location(50.5, 11.5),
				new Location(51.5, 11.5), new Location(51.5, 10.5));
		polygon.setInteriorRings(Arrays.asList(hole));

		double distance = polygon.getDistanceTo(new Location(51, 11));
		assertTrue(distance > 0);
		assertEquals(GeoUtils.getDistance(new Location(51, 11), new Location(51, 10.5)), distance, TOLERANCE_KM);
	}

	@Test
	public void nearestMarkerIsLineWhenCheckingOnTheLine() {
		UnfoldingMap map = new UnfoldingMap(new PApplet(), new EmptyMapProvider());
		map.zoomAndPanTo(5, new Location(50, 10));
		SimpleLinesMarker line = new SimpleLinesMarker(new Location(50, 0), new Location(50, 40));
		SimplePointMarker point = new SimplePointMarker(new Location(52, 3));
		map.addMarker(line);
		map.addMarker(point);

		ScreenPosition onLine = map.getScreenPosition(new Location(50, 1));
		assertSame(line, map.getDefaultMarkerManager().getNearestMarker(onLine.x, onLine.y));

		ScreenPosition onPoint = map.getScreenPosition(new Location(52, 3));
		assertSame(point, map.getDefaultMarkerManager().getNearestMarker(onPoint.x, onPoint.y));
	}

}
