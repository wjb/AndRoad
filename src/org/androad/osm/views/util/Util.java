// Created by plusminus on 17:53:07 - 25.09.2008
package org.androad.osm.views.util;

import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.util.BoundingBoxE6;
import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.util.IOpenStreetMapRendererInfo;
import org.andnav.osm.views.util.constants.MathConstants;
import org.andnav.osm.views.util.constants.OpenStreetMapViewConstants;

import org.androad.osm.util.constants.OSMConstants;

import android.util.FloatMath;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class Util implements OpenStreetMapViewConstants, OSMConstants, MathConstants{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static float calculateDistance(int x1, int x2, int y1, int y2) {
	        int x = x1 - x2;
	        int y = y1 - y2;
	        return FloatMath.sqrt(x * x + y * y);
	    }

	public static OpenStreetMapTile[] calculateNeededTilesForZoomLevelInBoundingBox(final IOpenStreetMapRendererInfo aRenderer, final int zoom, final BoundingBoxE6 bbE6Visible) {
		final OpenStreetMapTile upperLeftTile = getMapTileFromCoordinates(aRenderer, bbE6Visible.getLatNorthE6(), bbE6Visible.getLonWestE6(), zoom);
		final OpenStreetMapTile lowerRightTile = getMapTileFromCoordinates(aRenderer, bbE6Visible.getLatSouthE6(), bbE6Visible.getLonEastE6(), zoom);

		final int countOfTilesLat = Math.abs(upperLeftTile.getY() - lowerRightTile.getY()) + 1;
		final int countOfTilesLon = Math.abs(upperLeftTile.getX() - lowerRightTile.getX()) + 1;


		final OpenStreetMapTile[] out = new OpenStreetMapTile[countOfTilesLat * countOfTilesLon];

		for(int i = 0; i < countOfTilesLat; i++) {
			for(int j = 0; j < countOfTilesLon; j++) {
				out[countOfTilesLon * i + j] = new OpenStreetMapTile(aRenderer, upperLeftTile.getX() + j, upperLeftTile.getY() + i, zoom);
			}
		}

		return out;
	}

	public static OpenStreetMapTile getMapTileFromCoordinates(final IOpenStreetMapRendererInfo aRenderer, final GeoPoint gp, final int zoom) {
		return getMapTileFromCoordinates(aRenderer, gp.getLatitudeE6() / 1E6, gp.getLongitudeE6() / 1E6, zoom);
	}

	public static OpenStreetMapTile getMapTileFromCoordinates(final IOpenStreetMapRendererInfo aRenderer, final int aLat, final int aLon, final int zoom) {
		return getMapTileFromCoordinates(aRenderer, aLat / 1E6, aLon / 1E6, zoom);
	}

	public static OpenStreetMapTile getMapTileFromCoordinates(final IOpenStreetMapRendererInfo aRenderer, final double aLat, final double aLon, final int zoom) {
		final int y = (int) Math.floor((1 - Math.log(Math.tan(aLat * PI / 180) + 1 / Math.cos(aLat * PI / 180)) / PI) / 2 * (1 << zoom));
		final int x = (int) Math.floor((aLon + 180) / 360 * (1 << zoom));

		return new OpenStreetMapTile(aRenderer, x, y, zoom);
	}

	private static double tile2lon(final int x, final int aZoom) {
		return (360.0f * x / (1 << aZoom)) - 180;
	}

	private static double tile2lat(final int y, final int aZoom) {
		final float n = PI - ((2.0f * PI * y) / (1 << aZoom));
		return 180.0f / PI * Math.atan(0.5f * (Math.exp(n) - Math.exp(-n)));
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
