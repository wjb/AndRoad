// Created by plusminus on 19:24:16 - 12.11.2008
package org.androad.osm.views.tiles.util;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.TreeSet;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.MapTileRequestState;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.util.GeoPoint;

import org.androad.osm.util.ValuePair;
import org.androad.osm.util.Util.PixelSetter;
import org.androad.osm.views.util.Util;
import org.androad.sys.ors.adt.rs.Route;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;

public class OSMMapTilePreloader extends MapTileProviderBasic implements Runnable {
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final Integer TILE_LOADED_SUCCESS = 0;
	private static final Integer TILE_LOADED_FAIL = TILE_LOADED_SUCCESS + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	HashMap<MapTile, Integer> mLoaded = new HashMap<MapTile, Integer>();
	MapTile[] mTiles;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapTilePreloader(Context pContext, ITileSource pTileSource, final MapTile[] pTiles) {
		super(pContext, pTileSource);
		this.setTileSource(pTileSource);
		mTiles = pTiles;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setHandler(Handler pHandler) {
		this.setTileRequestCompleteHandler(pHandler);
	}

	public int getCount() {
		return mLoaded.size();
	}

	public int getFailures() {
		int ret = 0;
		for(MapTile tile : mLoaded.keySet())
			if(mLoaded.get(tile) == TILE_LOADED_FAIL)
				ret++;
		return ret;
	}

	public int getSuccess() {
		int ret = 0;
		for(MapTile tile : mLoaded.keySet())
			if(mLoaded.get(tile) == TILE_LOADED_SUCCESS)
				ret++;
		return ret;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void mapTileRequestCompleted(MapTileRequestState aState, Drawable aDrawable) {
		super.mapTileRequestCompleted(aState, aDrawable);
		mLoaded.put(aState.getMapTile(), TILE_LOADED_SUCCESS);
	}

	@Override
	public void mapTileRequestFailed(MapTileRequestState aState) {
		super.mapTileRequestFailed(aState);
		if(mLoaded.get(aState.getMapTile()) == null)
			mLoaded.put(aState.getMapTile(), TILE_LOADED_FAIL);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * Loads a series of MapTiles to the various caches at a specific zoomlevel.
	 */
	public void run(){
        for (final MapTile tile : mTiles)
        	this.getMapTile(tile);
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

}
