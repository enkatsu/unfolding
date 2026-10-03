package de.fhpotsdam.unfolding.providers;

import processing.core.PImage;
import de.fhpotsdam.unfolding.core.Coordinate;
import de.fhpotsdam.unfolding.geo.AbstractProjection;

/**
 * Handles tiles from URLs, such as web map services, etc.
 */
public abstract class AbstractMapTileUrlProvider extends AbstractMapProvider {

	/** API key for tile services that require one. */
	protected String apiKey;

	public AbstractMapTileUrlProvider(AbstractProjection projection) {
		super(projection);
	}

	/**
	 * Sets the API key for tile services that require one, e.g. CARTO, Stadia Maps (Stamen), or Thunderforest.
	 *
	 * @param apiKey
	 *            The API key issued by the tile service.
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * Appends the API key to the URL as query parameter, if an API key has been set.
	 *
	 * @param url
	 *            The tile URL.
	 * @param parameterName
	 *            The name of the query parameter the tile service expects the API key in.
	 * @return The URL with the API key, or the unchanged URL if no API key has been set.
	 */
	protected String withApiKey(String url, String parameterName) {
		if (apiKey == null || apiKey.isEmpty()) {
			return url;
		}
		return url + (url.contains("?") ? "&" : "?") + parameterName + "=" + apiKey;
	}

	@Override
	public PImage getTile(Coordinate coordinate) {
		return null;
	}

}
