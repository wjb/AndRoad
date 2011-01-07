// Created by plusminus on 17:53:07 - 25.09.2008
package org.androad.osm.views.util;

import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.util.BoundingBoxE6;
import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.util.IOpenStreetMapRendererInfo;
import org.andnav.osm.views.util.Mercator;
import org.andnav.osm.views.util.constants.MathConstants;
import org.andnav.osm.views.util.constants.OpenStreetMapViewConstants;

import org.androad.osm.util.constants.OSMConstants;

import android.util.FloatMath;

import java.util.List;

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

	public static void calculateNeededTilesForZoomLevelInBoundingBox(final List tilesNeeded, final IOpenStreetMapRendererInfo aRenderer, final int zoom, final BoundingBoxE6 bbE6Visible) {
        final OpenStreetMapTile upperLeftTile = getMapTileFromCoordinates(aRenderer, bbE6Visible.getLatNorthE6(), bbE6Visible.getLonWestE6(), zoom);
        final OpenStreetMapTile lowerRightTile = getMapTileFromCoordinates(aRenderer, bbE6Visible.getLatSouthE6(), bbE6Visible.getLonEastE6(), zoom);

        final int countOfTilesLat = Math.abs(upperLeftTile.getY() - lowerRightTile.getY()) + 1;
        final int countOfTilesLon = Math.abs(upperLeftTile.getX() - lowerRightTile.getX()) + 1;

        for(int i = 0; i < countOfTilesLat; i++) {
            for(int j = 0; j < countOfTilesLon; j++) {
                final OpenStreetMapTile tile = new OpenStreetMapTile(aRenderer, zoom, upperLeftTile.getX() + j, upperLeftTile.getY() + i  );

                tilesNeeded.add(tile);
			}
		}
	}

	public static OpenStreetMapTile getMapTileFromCoordinates(final IOpenStreetMapRendererInfo aRenderer, final GeoPoint gp, final int zoom) {
		return getMapTileFromCoordinates(aRenderer, gp.getLatitudeE6() / 1E6, gp.getLongitudeE6() / 1E6, zoom);
	}

	public static OpenStreetMapTile getMapTileFromCoordinates(final IOpenStreetMapRendererInfo aRenderer, final int aLat, final int aLon, final int zoom) {
		return getMapTileFromCoordinates(aRenderer, aLat / 1E6, aLon / 1E6, zoom);
	}

	public static OpenStreetMapTile getMapTileFromCoordinates(final IOpenStreetMapRendererInfo aRenderer, final double aLat, final double aLon, final int aZoom) {
        final int[] coords = Mercator.projectGeoPoint(aLat, aLon, aZoom, null);
		final int x = coords[Mercator.MAPTILE_LONGITUDE_INDEX];
		final int y = coords[Mercator.MAPTILE_LATITUDE_INDEX];

		return new OpenStreetMapTile(aRenderer, aZoom, x, y);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
