package de.fhpotsdam.unfolding.interactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.MouseEvent;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.events.EventDispatcher;
import de.fhpotsdam.unfolding.events.PanMapEvent;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.EmptyMapProvider;

public class MouseHandlerTest {

	private UnfoldingMap leftMap;
	private UnfoldingMap rightMap;
	private MouseHandler mouseHandler;

	@Before
	public void before() {
		PApplet p = new PApplet();
		// Two maps side by side, with a gap from x=400 to 420
		leftMap = new UnfoldingMap(p, "left", 0, 0, 400, 400, true, false, new EmptyMapProvider());
		rightMap = new UnfoldingMap(p, "right", 420, 0, 400, 400, true, false, new EmptyMapProvider());
		leftMap.zoomAndPanTo(5, new Location(50, 10));
		rightMap.zoomAndPanTo(5, new Location(50, 10));

		EventDispatcher eventDispatcher = new EventDispatcher();
		mouseHandler = new MouseHandler(p, leftMap, rightMap);
		eventDispatcher.addBroadcaster(mouseHandler);
		eventDispatcher.register(leftMap, PanMapEvent.TYPE_PAN, leftMap.getId());
		eventDispatcher.register(rightMap, PanMapEvent.TYPE_PAN, rightMap.getId());
	}

	private void mouse(int action, int x, int y) {
		mouseHandler.mouseEvent(new MouseEvent(null, 0, action, 0, x, y, PConstants.LEFT, 1));
	}

	/** Drags the mouse horizontally from x1 to x2. */
	private void drag(int x1, int x2) {
		mouse(MouseEvent.MOVE, x1, 200);
		mouse(MouseEvent.PRESS, x1, 200);
		for (int x = x1; x != x2; x += Integer.signum(x2 - x1) * 10) {
			mouse(MouseEvent.DRAG, x, 200);
		}
		mouse(MouseEvent.DRAG, x2, 200);
		mouse(MouseEvent.RELEASE, x2, 200);
	}

	@Test
	public void dragPansOnlyTheMapItStartedOn() {
		Location leftCenter = leftMap.getCenter();
		Location rightCenter = rightMap.getCenter();

		drag(200, 600);

		assertNotEquals(leftCenter, leftMap.getCenter());
		assertEquals(rightCenter, rightMap.getCenter());
	}

	@Test
	public void dragPansMapWithinItself() {
		Location rightCenter = rightMap.getCenter();

		drag(700, 500);

		assertNotEquals(rightCenter, rightMap.getCenter());
	}

	@Test
	public void dragStartingOutsideOfMapsPansNoMap() {
		Location leftCenter = leftMap.getCenter();
		Location rightCenter = rightMap.getCenter();

		// Starts in the gap between the maps
		drag(410, 700);

		assertEquals(leftCenter, leftMap.getCenter());
		assertEquals(rightCenter, rightMap.getCenter());
	}

}
