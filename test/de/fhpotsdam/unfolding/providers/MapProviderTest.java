package de.fhpotsdam.unfolding.providers;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.core.Coordinate;

public class MapProviderTest {

	private PApplet p;

	@Before
	public void before() {
		p = new PApplet();
	}

	@Test
	public void mapRestrictsZoomToProviderMaxZoomLevel() {
		UnfoldingMap map = new UnfoldingMap(p, new EsriProvider.WorldGrayCanvas());
		assertEquals(UnfoldingMap.getScaleFromZoom(16), map.maxScale, 0);
	}

	@Test
	public void mapKeepsDefaultMaxZoomLevel() {
		UnfoldingMap map = new UnfoldingMap(p, new OpenStreetMap.OpenStreetMapProvider());
		assertEquals(UnfoldingMap.getScaleFromZoom(18), map.maxScale, 0);
	}

	@Test
	public void tileUrlWithoutApiKey() {
		String[] urls = new StamenMapProvider.Toner().getTileUrls(new Coordinate(2, 4, 3));
		assertEquals("https://tiles.stadiamaps.com/tiles/stamen_toner/3/4/2.png", urls[0]);
	}

	@Test
	public void tileUrlWithApiKey() {
		Coordinate coordinate = new Coordinate(2, 4, 3);
		assertEquals("https://tiles.stadiamaps.com/tiles/stamen_toner/3/4/2.png?api_key=KEY",
				new StamenMapProvider.Toner("KEY").getTileUrls(coordinate)[0]);
		assertEquals("https://basemaps.cartocdn.com/light_all/3/4/2.png?key=KEY",
				new CartoDB.Positron("KEY").getTileUrls(coordinate)[0]);
		assertEquals("https://tile.thunderforest.com/cycle/3/4/2.png?apikey=KEY",
				new ThunderforestProvider.OpenCycleMap("KEY").getTileUrls(coordinate)[0]);
	}

}
