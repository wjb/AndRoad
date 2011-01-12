// Created by plusminus on 19:24:16 - 12.11.2008
package org.androad.osm.views.tiles.util;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.util.constants.MapViewConstants;

import org.androad.osm.util.ValuePair;
import org.androad.osm.util.Util.PixelSetter;
import org.androad.osm.util.constants.OSMConstants;
import org.androad.osm.views.util.Util;
import org.androad.sys.ors.adt.rs.Route;

import android.os.Handler;
import android.os.Message;

public class OSMMapTilePreloader implements OSMConstants, MapViewConstants {
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

	/**
	 * Loads a series of MapTiles to the various caches at a specific zoomlevel.
	 */
	public void loadAllToCacheAsync(final MapTile[] pTiles, final MapTileProviderBase aRendererInfo, final OnProgressChangeListener pProgressListener){
		final int overallCount = pTiles.length;

        int sendingCount = 0;

        aRendererInfo.setTileRequestCompleteHandler(pProgressListener);

        for (final MapTile tile : pTiles)
        	aRendererInfo.getMapTile(tile);
	}


	/**
	 * 
	 * @param aRoute
	 * @param aZoomLevel
	 * @param aProviderInfo
	 * @param pSmoothed Smoothed by a Bresenham-Algorithm
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static MapTile[] getNeededMaptiles(final Route aRoute, final int aZoomLevel, final MapTileProviderBase aProviderInfo, final boolean pSmoothed) throws IllegalArgumentException {
		return getNeededMaptiles(aRoute.getPolyLine(), aZoomLevel, aProviderInfo, pSmoothed);
	}

	/**
	 * 
	 * @param aPath
	 * @param aZoomLevel
	 * @param aProviderInfo
	 * @param pSmoothed Smoothed by a Bresenham-Algorithm
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static MapTile[] getNeededMaptiles(final List<GeoPoint> aPath, final int aZoomLevel, final MapTileProviderBase aProviderInfo, final boolean pSmoothed) throws IllegalArgumentException {
		if(aZoomLevel > aProviderInfo.getMaximumZoomLevel()) {
			throw new IllegalArgumentException("Zoomlevel higher than Renderer supplies!");
		}

		/* We need only unique MapTile-indices, so we use a Set. */
		final Set<ValuePair> needed = new TreeSet<ValuePair>(new Comparator<ValuePair>(){
			@Override
			public int compare(final ValuePair a, final ValuePair b) {
				return a.compareTo(b);
			}
		});

		/* Contains the values of a single line. */
		final Set<ValuePair> rasterLine = new TreeSet<ValuePair>(new Comparator<ValuePair>(){
			@Override
			public int compare(final ValuePair a, final ValuePair b) {
				return a.compareTo(b);
			}
		});

		final PixelSetter rasterLinePixelSetter = new PixelSetter(){
			@Override
			public void setPixel(final int x, final int y) {
				rasterLine.add(new ValuePair(x,y));
			}
		};

		MapTile cur = null;

		GeoPoint previous = null;
		/* Get the mapTile-coords of every point in the polyline and add to the set. */
		for (final GeoPoint gp : aPath) {
			cur = Util.getMapTileFromCoordinates(gp, aZoomLevel);
			needed.add(new ValuePair(cur.getX(), cur.getY()));

			if(previous != null){
				final int prevX = cur.getX();
				final int prevY = cur.getY();

				cur = Util.getMapTileFromCoordinates(GeoPoint.fromCenterBetween(gp, previous), aZoomLevel);

				final int curX = cur.getX();
				final int curY = cur.getY();

				rasterLine.clear();
				org.androad.osm.util.Util.rasterLine(prevX, prevY, curX, curY, rasterLinePixelSetter);

				/* If wanted smooth that line. */
				if(pSmoothed){
					org.androad.osm.util.Util.smoothLine(rasterLine);
				}

				needed.addAll(rasterLine);
			}

			previous = gp;
		}

		/* Put the unique MapTile-indices into an array. */
		final int countNeeded = needed.size();
		final MapTile[] out = new MapTile[countNeeded];

		int i = 0;
		for (final ValuePair valuePair : needed) {
			out[i++] = new MapTile(aZoomLevel, valuePair.getValueA(), valuePair.getValueB());
		}

		return out;
	}


	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public abstract class OnProgressChangeListener extends Handler{

		protected int count = 0;

		/** Between 0 and 100 (including). */
		public abstract void onProgressChange();

        @Override
        public void handleMessage(Message msg) {
        	count++;
            this.onProgressChange();
        }
	}

}
