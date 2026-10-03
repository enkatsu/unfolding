package de.fhpotsdam.unfolding.providers;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import de.fhpotsdam.unfolding.core.Coordinate;
import de.fhpotsdam.unfolding.providers.OpenStreetMap.GenericOpenStreetMapProvider;
import de.fhpotsdam.unfolding.providers.OpenStreetMap.OpenStreetMapProvider;

/**
 * Various map tiles from MapBox.
 */
public class MapBox {

	public static abstract class MapBoxProvider extends OpenStreetMapProvider {

		public MapBoxProvider() {
			super();
		}

		public String getZoomString(Coordinate coordinate) {
			// Rows are numbered from bottom to top (opposite to OSM)
			float gridSize = PApplet.pow(2, coordinate.zoom);
			float negativeRow = gridSize - coordinate.row - 1;

			return (int) coordinate.zoom + "/" + (int) coordinate.column + "/" + (int) negativeRow;
		}

		public String getPositiveZoomString(Coordinate coordinate) {
			float gridSize = PApplet.pow(2, coordinate.zoom);

			return (int) coordinate.zoom + "/" + (int) coordinate.column + "/" + (int) coordinate.row;
		}
	}

	/**
	 * @deprecated The MapBox tile server (tile.mapbox.com) is no longer available. Use {@link Light} instead.
	 */
	@Deprecated
	public static class WorldLightProvider extends MapBoxProvider {
		public String[] getTileUrls(Coordinate coordinate) {
			String url = "http://c.tile.mapbox.com/mapbox/1.0.0/world-light/" + getZoomString(coordinate) + ".png";
			return new String[] { url };
		}
	}

	/**
	 * @deprecated The MapBox tile server (tile.mapbox.com) is no longer available. Use {@link Dark} instead.
	 */
	@Deprecated
	public static class ControlRoomProvider extends MapBoxProvider {
		public String[] getTileUrls(Coordinate coordinate) {
			String url = "http://c.tile.mapbox.com/mapbox/1.0.0/control-room/" + getZoomString(coordinate) + ".png";
			return new String[] { url };
		}
	}

	/**
	 * @deprecated The MapBox v3 API has been retired. Use {@link Dark} instead.
	 */
	@Deprecated
	public static class LacquerProvider extends MapBoxProvider {
		public String[] getTileUrls(Coordinate coordinate) {
			String url = "http://c.tiles.mapbox.com/v3/mapbox.mapbox-lacquer/" + getPositiveZoomString(coordinate)
					+ ".png";
			return new String[] { url };
		}
	}

	/**
	 * Map style via the Mapbox Static Tiles API. Requires an access token from https://www.mapbox.com/.
	 * 
	 * Use one of the Mapbox styles below, or your own style, e.g.
	 * {@code new MapBox.StyleProvider("YOUR_USERNAME/YOUR_STYLE_ID", "YOUR_ACCESS_TOKEN")}.
	 */
	public static class StyleProvider extends GenericOpenStreetMapProvider {
		private String styleId;

		/**
		 * @param styleId
		 *            The style ID including its owner, e.g. "mapbox/streets-v12".
		 * @param accessToken
		 *            The Mapbox access token.
		 */
		public StyleProvider(String styleId, String accessToken) {
			this.styleId = styleId;
			setApiKey(accessToken);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://api.mapbox.com/styles/v1/" + styleId + "/tiles/256/"
					+ getZoomString(coordinate), "access_token");
			return new String[] { url };
		}
	}

	public static class Streets extends StyleProvider {
		public Streets(String accessToken) {
			super("mapbox/streets-v12", accessToken);
		}
	}

	public static class Outdoors extends StyleProvider {
		public Outdoors(String accessToken) {
			super("mapbox/outdoors-v12", accessToken);
		}
	}

	public static class Light extends StyleProvider {
		public Light(String accessToken) {
			super("mapbox/light-v11", accessToken);
		}
	}

	public static class Dark extends StyleProvider {
		public Dark(String accessToken) {
			super("mapbox/dark-v11", accessToken);
		}
	}

	public static class Satellite extends StyleProvider {
		public Satellite(String accessToken) {
			super("mapbox/satellite-v9", accessToken);
		}
	}

	public static class SatelliteStreets extends StyleProvider {
		public SatelliteStreets(String accessToken) {
			super("mapbox/satellite-streets-v12", accessToken);
		}
	}

	public static class CustomMapBoxProvider extends MapBoxProvider {
		String urlTemplate;

		public CustomMapBoxProvider(String urlTemplate) {
			this.urlTemplate = urlTemplate;
		}

		public String[] getTileUrls(Coordinate coordinate) {
			int zoom = (int) coordinate.zoom;
			int col = (int) coordinate.column;
			int row = (int) coordinate.row;

			String url = urlTemplate.replace("{z}", "" + zoom);
			url = url.replace("{x}", "" + col);
			url = url.replace("{y}", "" + row);

			return new String[] { url };
		}

	}

	// For testing non-exported map styles from your local TileMill server.
	// TODO Mention and explain in tutorial
	public static class MuseDarkStyleProvider extends MapBoxProvider {
		public String[] getTileUrls(Coordinate coordinate) {
			String url = "http://localhost:8889/1.0.0/aHR0cDovL2xvY2FsaG9zdDo4ODg5L2FwaS9Qcm9qZWN0L2NvbnRyb2xfcm9vbT8xMzA3MjEwNDEw/"
					+ getZoomString(coordinate) + ".png";
			return new String[] { url };
		}
	}

	/** Example for local TileMill usage, for testing purposes. For actual usage tiles should be exported. */
	public static class PlainUSAProvider extends MapBoxProvider {

		// TODO Use new TileMill app
		// http://localhost:20008/tile/border-bumps/{z}/{x}/{y}.png?updated=' + new Date().getTime()

		public String[] getTileUrls(Coordinate coordinate) {
			String url = "http://localhost:8889/1.0.0/plain-usa/" + getZoomString(coordinate) + ".png";
			return new String[] { url };
		}
	}

	// REMOVE
	/**
	 * Transparent tiles. Same as {@link EmptyMapProvider}.
	 */
	public static class BlankProvider extends MapBoxProvider {
		private PImage blankImage;

		// Created locally, as the blank tile from tillnagel.com is no longer available
		@Override
		public PImage getTile(Coordinate coordinate) {
			if (blankImage == null) {
				blankImage = new PImage(tileWidth(), tileHeight(), PConstants.ARGB);
			}
			return blankImage;
		}

		public String[] getTileUrls(Coordinate coordinate) {
			return null;
		}
	}

}
