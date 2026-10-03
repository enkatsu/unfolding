package de.fhpotsdam.unfolding.providers;

import de.fhpotsdam.unfolding.core.Coordinate;
import de.fhpotsdam.unfolding.providers.OpenStreetMap.GenericOpenStreetMapProvider;
import de.fhpotsdam.unfolding.providers.OpenStreetMap.OpenStreetMapProvider;

/**
 * Stamen map styles, hosted by Stadia Maps. Requires an API key from https://stadiamaps.com/ for use outside of
 * localhost, e.g. {@code new StamenMapProvider.Toner("YOUR_API_KEY")}.
 */
public class StamenMapProvider extends OpenStreetMapProvider {

	public static class Toner extends GenericOpenStreetMapProvider {
		public Toner() {
		}

		public Toner(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tiles.stadiamaps.com/tiles/stamen_toner/" + getZoomString(coordinate) + ".png", "api_key");
			return new String[] { url };
		}
	}

	public static class TonerBackground extends GenericOpenStreetMapProvider {
		public TonerBackground() {
		}

		public TonerBackground(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tiles.stadiamaps.com/tiles/stamen_toner_background/" + getZoomString(coordinate) + ".png", "api_key");
			return new String[] { url };
		}
	}

	public static class TonerLite extends GenericOpenStreetMapProvider {
		public TonerLite() {
		}

		public TonerLite(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tiles.stadiamaps.com/tiles/stamen_toner_lite/" + getZoomString(coordinate) + ".png", "api_key");
			return new String[] { url };
		}
	}

	public static class WaterColor extends GenericOpenStreetMapProvider {
		public WaterColor() {
		}

		public WaterColor(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tiles.stadiamaps.com/tiles/stamen_watercolor/" + getZoomString(coordinate) + ".jpg", "api_key");
			return new String[] { url };
		}
	}

}
