package de.fhpotsdam.unfolding.providers;

import de.fhpotsdam.unfolding.providers.AbstractMapTileUrlProvider;
import de.fhpotsdam.unfolding.core.Coordinate;
import de.fhpotsdam.unfolding.geo.MercatorProjection;
import de.fhpotsdam.unfolding.geo.Transformation;

/**
 * Provider based on Leaflet-providers: http://leaflet-extras.github.io/leaflet-providers/preview/index.html
 * Various map tiles from Opencycle. http://www.opencyclemap.org
 */
public class ThunderforestProvider {
	public static abstract class GenericThunderforestProvider extends AbstractMapTileUrlProvider {

		public GenericThunderforestProvider() {
			super(new MercatorProjection(26, new Transformation(1.068070779e7, 0.0, 3.355443185e7, 0.0,
					-1.068070890e7, 3.355443057e7)));
		}

		public String getZoomString(Coordinate coordinate) {
			return (int) coordinate.zoom + "/" + (int) coordinate.column + "/" + (int) coordinate.row;
		}

		public int tileWidth() {
			return 256;
		}

		public int tileHeight() {
			return 256;
		}

		public abstract String[] getTileUrls(Coordinate coordinate);
	}

	public static class OpenCycleMap extends GenericThunderforestProvider {
		public OpenCycleMap() {
		}

		public OpenCycleMap(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tile.thunderforest.com/cycle/" + getZoomString(coordinate) + ".png", "apikey");
			return new String[] { url };
		}
	}

	public static class Transport extends GenericThunderforestProvider {
		public Transport() {
		}

		public Transport(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tile.thunderforest.com/transport/" + getZoomString(coordinate) + ".png", "apikey");
			return new String[] { url };
		}
	}
	
	public static class Landscape extends GenericThunderforestProvider {
		public Landscape() {
		}

		public Landscape(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tile.thunderforest.com/landscape/" + getZoomString(coordinate) + ".png", "apikey");
			return new String[] { url };
		}
	}
	
	public static class Outdoors extends GenericThunderforestProvider {
		public Outdoors() {
		}

		public Outdoors(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tile.thunderforest.com/outdoors/" + getZoomString(coordinate) + ".png", "apikey");
			return new String[] { url };
		}
	}

}

