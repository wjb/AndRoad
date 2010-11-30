// Created by plusminus on 19:24:16 - 12.11.2008
package org.androad.osm.views.tiles.util;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.andnav.osm.tileprovider.CloudmadeException;
import org.andnav.osm.tileprovider.IOpenStreetMapTileProviderCallback;
import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.tileprovider.OpenStreetMapTileFilesystemProvider;
import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.util.IOpenStreetMapRendererInfo;

import org.androad.osm.util.ValuePair;
import org.androad.osm.util.Util.PixelSetter;
import org.androad.osm.util.constants.OSMConstants;
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
	
	private static final int PRELOAD_SUCCESS = 0;
	private static final int PRELOAD_FAIL = PRELOAD_SUCCESS + 1;

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
		final IOpenStreetMapTileProviderCallback cbk = new IOpenStreetMapTileProviderCallback(){
			@Override
			public String getCloudmadeKey() throws CloudmadeException {
				return null;
			}

			@Override
			public void mapTileRequestCompleted(OpenStreetMapTile aTile, String aTilePath) {
				successCounter.increment();
				pProgressListener.onProgressChange(successCounter.getCount(), overallCount);
				if(overallCounter.getCount() == overallCount
						&& successCounter.getCount() != overallCount) {
					pProgressListener.onProgressChange(overallCount, overallCount);
				}
				if(DEBUGMODE) {
					Log.i(DEBUGTAG, "MapTile download success.");
				}
			}

			@Override
			public void mapTileRequestCompleted(OpenStreetMapTile aTile, InputStream aTileInputStream) {
				mapTileRequestCompleted(aTile, "");
			}

			@Override
			public void mapTileRequestCompleted(OpenStreetMapTile aTile) {
				if(overallCounter.getCount() == overallCount
						&& successCounter.getCount() != overallCount) {
					pProgressListener.onProgressChange(overallCount, overallCount);
				}
				if(DEBUGMODE) {
					Log.e(DEBUGTAG, "MapTile download error.");
				}
			}

			@Override
			public boolean useDataConnection(){return true;}
		};

		new Thread(new Runnable(){
			@Override
			public void run() {
				for(int i = 0; i < pTiles.length; i++){
					final OpenStreetMapTile[] tileSet = pTiles[i];
					for (final OpenStreetMapTile tile : tileSet) {
						new OpenStreetMapTileFilesystemProvider(cbk, null).loadMapTileAsync(tile);
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
		final IOpenStreetMapTileProviderCallback cbk = new IOpenStreetMapTileProviderCallback(){
			@Override
			public String getCloudmadeKey() throws CloudmadeException {
				return null;
			}

			@Override
			public void mapTileRequestCompleted(OpenStreetMapTile aTile, String aTilePath) {
				successCounter.increment();
				pProgressListener.onProgressChange(successCounter.getCount(), overallCount);
				if(overallCounter.getCount() == overallCount
						&& successCounter.getCount() != overallCount) {
					pProgressListener.onProgressChange(overallCount, overallCount);
				}
				if(DEBUGMODE) {
					Log.i(DEBUGTAG, "MapTile download success.");
				}
			}

			@Override
			public void mapTileRequestCompleted(OpenStreetMapTile aTile, InputStream aTileInputStream) {
				mapTileRequestCompleted(aTile, "");
			}

			@Override
			public void mapTileRequestCompleted(OpenStreetMapTile aTile) {
				if(overallCounter.getCount() == overallCount
						&& successCounter.getCount() != overallCount) {
					pProgressListener.onProgressChange(overallCount, overallCount);
				}
				if(DEBUGMODE) {
					Log.e(DEBUGTAG, "MapTile download error.");
				}
			}

			@Override
			public boolean useDataConnection(){return true;}
		};

		new Thread(new Runnable(){
			@Override
			public void run() {
				for (final OpenStreetMapTile tile : pTiles) {
					new OpenStreetMapTileFilesystemProvider(cbk, null).loadMapTileAsync(tile);
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
