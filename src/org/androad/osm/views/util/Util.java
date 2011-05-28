// Created by plusminus on 17:53:07 - 25.09.2008
package org.androad.osm.views.util;

import android.graphics.Point;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.util.constants.MathConstants;
import org.osmdroid.views.util.constants.MapViewConstants;

import org.androad.osm.util.constants.OSMConstants;

import microsoft.mappoint.TileSystem;

import java.util.List;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class Util implements MapViewConstants, OSMConstants, MathConstants{
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

	public static void calculateNeededTilesForZoomLevelInBoundingBox(final List<MapTile> tilesNeeded, final int zoom, final BoundingBoxE6 bbE6Visible) {
        final MapTile upperLeftTile = getMapTileFromCoordinates(bbE6Visible.getLatNorthE6(), bbE6Visible.getLonWestE6(), zoom);
        final MapTile lowerRightTile = getMapTileFromCoordinates(bbE6Visible.getLatSouthE6(), bbE6Visible.getLonEastE6(), zoom);

        final int countOfTilesLat = Math.abs(upperLeftTile.getY() - lowerRightTile.getY()) + 1;
        final int countOfTilesLon = Math.abs(upperLeftTile.getX() - lowerRightTile.getX()) + 1;

        for(int i = 0; i < countOfTilesLat; i++) {
            for(int j = 0; j < countOfTilesLon; j++) {
                final MapTile tile = new MapTile(zoom, upperLeftTile.getX() + j, upperLeftTile.getY() + i  );

                tilesNeeded.add(tile);
			}
		}
	}

	public static MapTile getMapTileFromCoordinates(final GeoPoint gp, final int zoom) {
		return getMapTileFromCoordinates(gp.getLatitudeE6() / 1E6, gp.getLongitudeE6() / 1E6, zoom);
	}

	public static MapTile getMapTileFromCoordinates(final int aLat, final int aLon, final int zoom) {
		return getMapTileFromCoordinates(aLat / 1E6, aLon / 1E6, zoom);
	}

	public static MapTile getMapTileFromCoordinates(final double aLat, final double aLon, final int aZoom) {
        final Point coords = TileSystem.LatLongToPixelXY(aLat, aLon, aZoom, null);

		return new MapTile(aZoom, coords.x, coords.y);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
