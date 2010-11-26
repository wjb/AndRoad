// Created by plusminus on 19:24:16 - 12.11.2008
package org.androad.osm.views.tiles.util;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.util.IOpenStreetMapRendererInfo;

import org.androad.osm.util.ValuePair;
import org.androad.osm.util.Util.PixelSetter;
import org.androad.osm.util.constants.OSMConstants;
import org.androad.osm.views.tiles.OSMAbstractMapTileProvider;
import org.androad.osm.views.util.Util;
import org.androad.osm.views.util.constants.OSMMapViewConstants;
import org.androad.sys.ors.adt.rs.Route;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class OSMMapTilePreloader implements OSMConstants, OSMMapViewConstants {
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
	 * Loads all MapTiles needed to cover a route at a specific zoomlevel.
	 */
	public void loadAllToCacheAsync(final Route aRoute, final int aZoomLevel, final IOpenStreetMapRendererInfo aRendererInfo, final OpenStreetMapView pOsmView, final OnProgressChangeListener pProgressListener, final boolean pSmoothed) throws IllegalArgumentException {
		loadAllToCacheAsync(OSMMapTilePreloader.getNeededMaptiles(aRoute, aZoomLevel, aRendererInfo, pSmoothed), aZoomLevel, aRendererInfo, pOsmView, pProgressListener);
	}

	/**
	 * Loads a series of MapTiles to the various caches at a specific zoomlevel.
	 */
	public void loadAllToCacheAsync(final OpenStreetMapTile[][] pTiles, final int uptoZoomLevel, final IOpenStreetMapRendererInfo aRendererInfo, final OpenStreetMapView pOsmView, final OnProgressChangeListener pProgressListener){
		int tmpCount = 0;
		for(final OpenStreetMapTile[] tiles : pTiles) {
			tmpCount += tiles.length;
		}

		final int overallCount = tmpCount;

		final Counter overallCounter = new Counter();
		final Counter successCounter = new Counter();

		final Handler h = new Handler(){
			@Override
			public void handleMessage(final Message msg) {
				final int what = msg.what;
				overallCounter.increment();
				switch(what){
					case OSMAbstractMapTileProvider.MAPTILEPROVIDER_SUCCESS_ID:
						successCounter.increment();
						pProgressListener.onProgressChange(successCounter.getCount(), overallCount);
						if(DEBUGMODE) {
							Log.i(DEBUGTAG, "MapTile download success.");
						}
						break;
					case OSMAbstractMapTileProvider.MAPTILEPROVIDER_FAIL_ID:
						if(DEBUGMODE) {
							Log.e(DEBUGTAG, "MapTile download error.");
						}
						break;
				}
				if(overallCounter.getCount() == overallCount
						&& successCounter.getCount() != overallCount) {
					pProgressListener.onProgressChange(overallCount, overallCount);
				}

				super.handleMessage(msg);
			}
		};

		new Thread(new Runnable(){
			@Override
			public void run() {
				for(int i = 0; i < pTiles.length; i++){
					final OpenStreetMapTile[] tileSet = pTiles[i];
					for (final OpenStreetMapTile tile : tileSet) {
						if(!pOsmView.preloadMaptileAsync(tile, h)) {
							h.sendEmptyMessage(OSMAbstractMapTileProvider.MAPTILEPROVIDER_SUCCESS_ID);
						}
					}
				}
			}
		}, "Maptile-Preloader preparer").start();
	}

	/**
	 * Loads a series of MapTiles to the various caches at a specific zoomlevel.
	 */
	public void loadAllToCacheAsync(final OpenStreetMapTile[] pTiles, final int aZoomLevel, final IOpenStreetMapRendererInfo aRendererInfo, final OpenStreetMapView pOsmView, final OnProgressChangeListener pProgressListener){
		final int overallCount = pTiles.length;

		final Counter overallCounter = new Counter();
		final Counter successCounter = new Counter();
		final Handler h = new Handler(){
			@Override
			public void handleMessage(final Message msg) {
				final int what = msg.what;
				overallCounter.increment();
				switch(what){
					case OSMAbstractMapTileProvider.MAPTILEPROVIDER_SUCCESS_ID:
						successCounter.increment();
						pProgressListener.onProgressChange(successCounter.getCount(), overallCount);
						if(DEBUGMODE) {
							Log.i(DEBUGTAG, "MapTile download success.");
						}
						break;
					case OSMAbstractMapTileProvider.MAPTILEPROVIDER_FAIL_ID:
						if(DEBUGMODE) {
							Log.e(DEBUGTAG, "MapTile download error.");
						}
						break;
				}
				if(overallCounter.getCount() == overallCount
						&& successCounter.getCount() != overallCount) {
					pProgressListener.onProgressChange(overallCount, overallCount);
				}

				super.handleMessage(msg);
			}
		};

		new Thread(new Runnable(){
			@Override
			public void run() {
				for (final OpenStreetMapTile tile : pTiles) {
					if(!pOsmView.preloadMaptileAsync(tile, h)) {
						h.sendEmptyMessage(OSMAbstractMapTileProvider.MAPTILEPROVIDER_SUCCESS_ID);
					}
				}
			}
		}, "Maptile-Preloader preparer").start();
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
	public static OpenStreetMapTile[] getNeededMaptiles(final Route aRoute, final int aZoomLevel, final IOpenStreetMapRendererInfo aProviderInfo, final boolean pSmoothed) throws IllegalArgumentException {
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
	public static OpenStreetMapTile[] getNeededMaptiles(final List<GeoPoint> aPath, final int aZoomLevel, final IOpenStreetMapRendererInfo aProviderInfo, final boolean pSmoothed) throws IllegalArgumentException {
		if(aZoomLevel > aProviderInfo.zoomMaxLevel()) {
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

		OpenStreetMapTile cur = null;

		GeoPoint previous = null;
		/* Get the mapTile-coords of every point in the polyline and add to the set. */
		for (final GeoPoint gp : aPath) {
			cur = Util.getMapTileFromCoordinates(aProviderInfo, gp, aZoomLevel);
			needed.add(new ValuePair(cur.getX(), cur.getY()));

			if(previous != null){
				final int prevX = cur.getX();
				final int prevY = cur.getY();

				cur = Util.getMapTileFromCoordinates(aProviderInfo, GeoPoint.fromCenterBetween(gp, previous), aZoomLevel);

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
		final OpenStreetMapTile[] out = new OpenStreetMapTile[countNeeded];

		int i = 0;
		for (final ValuePair valuePair : needed) {
			out[i++] = new OpenStreetMapTile(aProviderInfo, aZoomLevel, valuePair.getValueA(), valuePair.getValueB());
		}

		return out;
	}


	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public interface OnProgressChangeListener{
		/** Between 0 and 100 (including). */
		void onProgressChange(final int aProgress, final int aMax);
	}

	private static class Counter{
		int mCount;

		public void increment() {
			this.mCount++;
		}

		public int getCount() {
			return this.mCount;
		}
	}
}
