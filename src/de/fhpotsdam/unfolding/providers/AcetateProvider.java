package de.fhpotsdam.unfolding.providers;

import de.fhpotsdam.unfolding.providers.AbstractMapTileUrlProvider;
import de.fhpotsdam.unfolding.core.Coordinate;
import de.fhpotsdam.unfolding.geo.MercatorProjection;
import de.fhpotsdam.unfolding.geo.Transformation;

/**
 * Provider based on Leaflet-providers: http://leaflet-extras.github.io/leaflet-providers/preview/index.html
 * Tiles (c)2012 Esri & Stamen, Data from OSM and Natural Earth
 * 
 * @deprecated The Acetate tile server (acetate.geoiq.com) is no longer available.
 */
@Deprecated
public class AcetateProvider {
	@Deprecated
	public static abstract class GenericAcetateProvider extends AbstractMapTileUrlProvider {

		public GenericAcetateProvider() {
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

	@Deprecated
	public static class Basemap extends GenericAcetateProvider {
		public String[] getTileUrls(Coordinate coordinate) {
			String url = "http://a.acetate.geoiq.com/tiles/acetate-base/" + getZoomString(coordinate) + ".png";
			return new String[] { url };
		}
	}
	
	@Deprecated
	public static class Terrain extends GenericAcetateProvider {
		public String[] getTileUrls(Coordinate coordinate) {
			String url = "http://a.acetate.geoiq.com/tiles/terrain/" + getZoomString(coordinate) + ".png";
			return new String[] { url };
		}
	}
	
	@Deprecated
	public static class All extends GenericAcetateProvider {
		public String[] getTileUrls(Coordinate coordinate) {
			String url = "http://a.acetate.geoiq.com/tiles/acetate-hillshading/" + getZoomString(coordinate) + ".png";
			return new String[] { url };
		}
	}
	
	@Deprecated
	public static class Hillshading extends GenericAcetateProvider {
		public String[] getTileUrls(Coordinate coordinate) {
			String url = "http://a.acetate.geoiq.com/tiles/hillshading/" + getZoomString(coordinate) + ".png";
			return new String[] { url };
		}
	}
	
	@Deprecated
	public static class Foreground extends GenericAcetateProvider {
		public String[] getTileUrls(Coordinate coordinate) {
			String url = "http://a.acetate.geoiq.com/tiles/acetate-fg/" + getZoomString(coordinate) + ".png";
			return new String[] { url };
		}
	}
	
	@Deprecated
	public static class Roads extends GenericAcetateProvider {
		public String[] getTileUrls(Coordinate coordinate) {
			String url = "http://a.acetate.geoiq.com/tiles/acetate-roads/" + getZoomString(coordinate) + ".png";
			return new String[] { url };
		}
	}
	
	@Deprecated
	public static class Labels extends GenericAcetateProvider {
		public String[] getTileUrls(Coordinate coordinate) {
			String url = "http://a.acetate.geoiq.com/tiles/acetate-labels/" + getZoomString(coordinate) + ".png";
			return new String[] { url };
		}
	}
	
}

