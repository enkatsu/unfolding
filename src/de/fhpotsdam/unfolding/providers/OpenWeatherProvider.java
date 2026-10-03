package de.fhpotsdam.unfolding.providers;

import de.fhpotsdam.unfolding.providers.AbstractMapTileUrlProvider;
import de.fhpotsdam.unfolding.core.Coordinate;
import de.fhpotsdam.unfolding.geo.MercatorProjection;
import de.fhpotsdam.unfolding.geo.Transformation;

/**
 * Provider based on Leaflet-providers: http://leaflet-extras.github.io/leaflet-providers/preview/index.html
 * Map data (c)OpenWeatherMap http://openweathermap.org
 * 
 * Requires an API key from https://openweathermap.org/, e.g. {@code new OpenWeatherProvider.Clouds("YOUR_API_KEY")}.
 */
public class OpenWeatherProvider {
	public static abstract class GenericOpenWeatherMapProvider extends AbstractMapTileUrlProvider {

		public GenericOpenWeatherMapProvider() {
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

	public static class Snow extends GenericOpenWeatherMapProvider {
		public Snow() {
		}

		public Snow(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tile.openweathermap.org/map/snow/" + getZoomString(coordinate) + ".png", "appid");
			return new String[] { url };
		}
	}
	
	public static class Temperature extends GenericOpenWeatherMapProvider {
		public Temperature() {
		}

		public Temperature(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tile.openweathermap.org/map/temp/" + getZoomString(coordinate) + ".png", "appid");
			return new String[] { url };
		}
	}
	
	public static class Wind extends GenericOpenWeatherMapProvider {
		public Wind() {
		}

		public Wind(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tile.openweathermap.org/map/wind/" + getZoomString(coordinate) + ".png", "appid");
			return new String[] { url };
		}
	}
	
	/**
	 * @deprecated The pressure_cntr layer is no longer available.
	 */
	@Deprecated
	public static class PressureContour extends GenericOpenWeatherMapProvider {
		public PressureContour() {
		}

		public PressureContour(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tile.openweathermap.org/map/pressure_cntr/" + getZoomString(coordinate) + ".png", "appid");
			return new String[] { url };
		}
	}
	
	public static class Pressure extends GenericOpenWeatherMapProvider {
		public Pressure() {
		}

		public Pressure(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tile.openweathermap.org/map/pressure/" + getZoomString(coordinate) + ".png", "appid");
			return new String[] { url };
		}
	}
	
	public static class RainClassic extends GenericOpenWeatherMapProvider {
		public RainClassic() {
		}

		public RainClassic(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tile.openweathermap.org/map/rain_cls/" + getZoomString(coordinate) + ".png", "appid");
			return new String[] { url };
		}
	}
	
	public static class Rain extends GenericOpenWeatherMapProvider {
		public Rain() {
		}

		public Rain(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tile.openweathermap.org/map/rain/" + getZoomString(coordinate) + ".png", "appid");
			return new String[] { url };
		}
	}
	
	public static class PrecipitationClassic extends GenericOpenWeatherMapProvider {
		public PrecipitationClassic() {
		}

		public PrecipitationClassic(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tile.openweathermap.org/map/precipitation_cls/" + getZoomString(coordinate) + ".png", "appid");
			return new String[] { url };
		}
	}
	
	public static class Precipitation extends GenericOpenWeatherMapProvider {
		public Precipitation() {
		}

		public Precipitation(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tile.openweathermap.org/map/precipitation/" + getZoomString(coordinate) + ".png", "appid");
			return new String[] { url };
		}
	}
	
	public static class CloudsClassic extends GenericOpenWeatherMapProvider {
		public CloudsClassic() {
		}

		public CloudsClassic(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tile.openweathermap.org/map/clouds_cls/" + getZoomString(coordinate) + ".png", "appid");
			return new String[] { url };
		}
	}
	
	public static class Clouds extends GenericOpenWeatherMapProvider {
		public Clouds() {
		}

		public Clouds(String apiKey) {
			setApiKey(apiKey);
		}

		public String[] getTileUrls(Coordinate coordinate) {
			String url = withApiKey("https://tile.openweathermap.org/map/clouds/" + getZoomString(coordinate) + ".png", "appid");
			return new String[] { url };
		}
	}
}

