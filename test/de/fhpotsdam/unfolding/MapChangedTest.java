package de.fhpotsdam.unfolding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.events.MapEvent;
import de.fhpotsdam.unfolding.events.PanMapEvent;
import de.fhpotsdam.unfolding.events.ZoomMapEvent;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.EmptyMapProvider;

public class MapChangedTest {

	/** A sketch recording the map events it is notified of. */
	public static class Sketch extends PApplet {
		List<MapEvent> events = new ArrayList<MapEvent>();
		UnfoldingMap map;
		boolean panInMapChanged = false;

		public void mapChanged(MapEvent mapEvent) {
			events.add(mapEvent);
			if (panInMapChanged) {
				map.panBy(10, 0);
			}
		}
	}

	private Sketch sketch;
	private UnfoldingMap map;

	@Before
	public void before() {
		sketch = new Sketch();
		map = new UnfoldingMap(sketch, 0, 0, 600, 400, new EmptyMapProvider());
		sketch.map = map;
		map.zoomAndPanTo(5, new Location(50, 10));
		sketch.events.clear();
	}

	private MapEvent singleEvent() {
		assertEquals(1, sketch.events.size());
		MapEvent mapEvent = sketch.events.get(0);
		sketch.events.clear();
		return mapEvent;
	}

	@Test
	public void notifiesPanMethods() {
		Location location = new Location(48, 2);
		map.panTo(location);
		PanMapEvent panTo = (PanMapEvent) singleEvent();
		assertEquals(PanMapEvent.PAN_TO, panTo.getSubType());
		assertEquals(location, panTo.getToLocation());

		map.panBy(10, 20);
		assertEquals(PanMapEvent.PAN_BY, singleEvent().getSubType());

		map.pan(100, 100, 150, 120);
		assertEquals(PanMapEvent.PAN_BY, singleEvent().getSubType());

		map.panLeft();
		assertEquals(PanMapEvent.PAN_LEFT, singleEvent().getSubType());
	}

	@Test
	public void notifiesZoomMethods() {
		map.zoomToLevel(7);
		ZoomMapEvent zoomToLevel = (ZoomMapEvent) singleEvent();
		assertEquals(ZoomMapEvent.ZOOM_TO_LEVEL, zoomToLevel.getSubType());
		assertEquals(7, zoomToLevel.getZoomLevel());

		map.zoomLevelIn();
		ZoomMapEvent zoomByLevel = (ZoomMapEvent) singleEvent();
		assertEquals(ZoomMapEvent.ZOOM_BY_LEVEL, zoomByLevel.getSubType());
		assertEquals(1, zoomByLevel.getZoomLevelDelta());

		map.zoomIn();
		assertEquals(ZoomMapEvent.ZOOM_BY, singleEvent().getSubType());
	}

	@Test
	public void notifiesZoomAndPanToOnce() {
		Location location = new Location(41.9, 12.5);
		map.zoomAndPanTo(8, location);

		ZoomMapEvent zoomMapEvent = (ZoomMapEvent) singleEvent();
		assertEquals(ZoomMapEvent.ZOOM_TO_LEVEL, zoomMapEvent.getSubType());
		assertEquals(8, zoomMapEvent.getZoomLevel());
		assertEquals(location, zoomMapEvent.getCenter());

		map.zoomAndPanToFit(Arrays.asList(new Location(52.5, 13.4), new Location(48.85, 2.35)));
		assertEquals(ZoomMapEvent.ZOOM_TO, singleEvent().getSubType());
	}

	@Test
	public void notifiesMapEventsOnce() {
		// As sent by the event dispatcher on mouse and keyboard interactions
		PanMapEvent panMapEvent = new PanMapEvent(this, map.getId(), PanMapEvent.PAN_TO);
		panMapEvent.setToLocation(new Location(48, 2));
		map.onManipulation(panMapEvent);

		assertTrue(singleEvent() == panMapEvent);
	}

	@Test
	public void doesNotNotifyChangesInMapChanged() {
		sketch.panInMapChanged = true;
		map.panBy(10, 0);

		// Not called again for the panBy() in mapChanged()
		singleEvent();
	}

}
