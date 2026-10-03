package de.fhpotsdam.unfolding.providers;

import java.util.Map;

import processing.core.PApplet;
import processing.core.PImage;
import de.fhpotsdam.unfolding.core.Coordinate;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.geo.MercatorProjection;
import de.fhpotsdam.unfolding.geo.Transformation;
import de.fhpotsdam.unfolding.tiles.MBTilesLoaderUtils;

/**
 * MapProvider for local MBTiles.
 * 
 * Uses the metadata of the MBTiles file (minzoom, maxzoom, center, bounds) to restrict the zoom range of maps, and to
 * show the area with tiles initially.
 */
public class MBTilesMapProvider extends AbstractMapTileProvider {
	
	private static final String JDBC_PREFIX = "jdbc:sqlite:";
	protected String jdbcConnectionString;

	private int minZoomLevel = super.minZoomLevel();
	private int maxZoomLevel = super.maxZoomLevel();
	private Location defaultLocation = null;
	private int defaultZoomLevel = minZoomLevel;

	public MBTilesMapProvider() {
		super(new MercatorProjection(26, new Transformation(1.068070779e7, 0.0, 3.355443185e7, 0.0,
				-1.068070890e7, 3.355443057e7)));
	}

	public MBTilesMapProvider(String jdbcConnectionString) {
		this();
		if (jdbcConnectionString != null && !jdbcConnectionString.startsWith(JDBC_PREFIX)) {
			jdbcConnectionString = JDBC_PREFIX + jdbcConnectionString;
		}
		this.jdbcConnectionString = jdbcConnectionString;
		readMetadata();
	}

	private void readMetadata() {
		Map<String, String> metadata = MBTilesLoaderUtils.getMetadata(jdbcConnectionString);
		try {
			if (metadata.containsKey("minzoom")) {
				minZoomLevel = Integer.parseInt(metadata.get("minzoom").trim());
			}
			if (metadata.containsKey("maxzoom")) {
				maxZoomLevel = Integer.parseInt(metadata.get("maxzoom").trim());
			}
			defaultZoomLevel = minZoomLevel;

			// center: "longitude,latitude,zoom", bounds: "left,bottom,right,top"
			if (metadata.containsKey("center")) {
				String[] center = metadata.get("center").split(",");
				defaultLocation = new Location(Float.parseFloat(center[1].trim()), Float.parseFloat(center[0].trim()));
				if (center.length > 2) {
					defaultZoomLevel = Integer.parseInt(center[2].trim());
				}
			} else if (metadata.containsKey("bounds")) {
				String[] bounds = metadata.get("bounds").split(",");
				float left = Float.parseFloat(bounds[0].trim());
				float bottom = Float.parseFloat(bounds[1].trim());
				float right = Float.parseFloat(bounds[2].trim());
				float top = Float.parseFloat(bounds[3].trim());
				defaultLocation = new Location((bottom + top) / 2, (left + right) / 2);
			}
		} catch (RuntimeException e) {
			PApplet.println("Could not read metadata of MBTiles " + jdbcConnectionString + ": " + e);
		}
		defaultZoomLevel = PApplet.constrain(defaultZoomLevel, minZoomLevel, maxZoomLevel);
	}

	@Override
	public int minZoomLevel() {
		return minZoomLevel;
	}

	@Override
	public int maxZoomLevel() {
		return maxZoomLevel;
	}

	@Override
	public Location defaultLocation() {
		return defaultLocation;
	}

	@Override
	public int defaultZoomLevel() {
		return defaultZoomLevel;
	}

	public int tileWidth() {
		return 256;
	}

	public int tileHeight() {
		return 256;
	}

	@Override
	public PImage getTile(Coordinate coord) {
		float gridSize = PApplet.pow(2, coord.zoom);
		float negativeRow = gridSize - coord.row - 1;

		return MBTilesLoaderUtils.getMBTile((int) coord.column, (int) negativeRow, (int) coord.zoom, jdbcConnectionString);
	}

}
