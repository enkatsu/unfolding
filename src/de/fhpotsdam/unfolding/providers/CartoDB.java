package de.fhpotsdam.unfolding.providers;

import de.fhpotsdam.unfolding.providers.AbstractMapTileUrlProvider;
import de.fhpotsdam.unfolding.core.Coordinate;
import de.fhpotsdam.unfolding.geo.MercatorProjection;
import de.fhpotsdam.unfolding.geo.Transformation;

/**
 * Provider based on Leaflet-providers: http://leaflet-extras.github.io/leaflet-providers/preview/index.html
 * Tiles (c)OpenStreetMap (http://www.openstreetmap.org/copyright), Style (c)CartoDB (http://cartodb.com/attributions)
 */
public class CartoDB {
	public static abstract class GenericCartoDBProvider extends AbstractMapTileUrlProvider {
		public GenericCartoDBProvider() {
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

	public static class Positron extends GenericCartoDBProvider {
		public Positron() {
		}

		public Positron(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://basemaps.cartocdn.com/light_all/" + getZoomString(coordinate) + ".png", "key");
			return new String[] { url };
		}
	}

	public static class DarkMatter extends GenericCartoDBProvider {
		public DarkMatter() {
		}

		public DarkMatter(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://basemaps.cartocdn.com/dark_all/" + getZoomString(coordinate) + ".png", "key");
			return new String[] { url };
		}
	}

	public static class DarkMatterNoLabels extends GenericCartoDBProvider {
		public DarkMatterNoLabels() {
		}

		public DarkMatterNoLabels(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://basemaps.cartocdn.com/dark_nolabels/" + getZoomString(coordinate) + ".png", "key");
			return new String[] { url };
		}
	}

}
