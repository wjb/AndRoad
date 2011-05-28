// Created by plusminus on 23:18:23 - 02.10.2008
package org.androad.osm.views.overlay;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.DefaultResourceProxyImpl;

import org.androad.sys.ors.adt.ts.ISpatialDataOrganizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

/**
 * @author Nicolas Gramlich
 *
 * @param <T>
 */
public class OSMMapViewSpacialIndexItemizedOverlayWithFocus<T extends OverlayItem> extends ItemizedOverlayWithFocus<T> {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int ITEM_LIMIT = 20;

	// ===========================================================
	// Fields
	// ===========================================================

	private ISpatialDataOrganizer<T> mSpatialDataOrganizer;
	private List<T> mClosestItems;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewSpacialIndexItemizedOverlayWithFocus(final Context ctx, final ISpatialDataOrganizer<T> aManager, final Drawable pMarker, final Point pMarkerHotspot, final Drawable pMarkerFocusedBase, final Point pMarkerFocusedHotSpot, final int pFocusedBackgroundColor, final OnItemGestureListener<T> pOnItemTapListener) {
		super(new ArrayList<T>(), pMarker, pMarkerFocusedBase, pFocusedBackgroundColor, pOnItemTapListener, new DefaultResourceProxyImpl(ctx));
		this.mSpatialDataOrganizer = aManager;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * @return may return null !
	 */
	public List<T> getOverlayItems(){
		return this.mClosestItems;
	}

	public void setOverlayItems(final List<T> pItems){
		this.mSpatialDataOrganizer.clearIndex();
		this.mSpatialDataOrganizer.addAll(pItems);
		this.mSpatialDataOrganizer.buildIndex();
	}

	public void setSpacialIndexManager(final ISpatialDataOrganizer<T> pManager) {
		this.mSpatialDataOrganizer = pManager;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

    @Override
	public void draw(final Canvas c, final MapView mapView, final boolean shadow) {
		if(this.mSpatialDataOrganizer.isIndexBuilt()){
			switch(this.mSpatialDataOrganizer.getGetMode()){
				case BOUNDINGBOX:
					this.mClosestItems = this.mSpatialDataOrganizer.getWithinBoundingBox(mapView.getBoundingBox(), ITEM_LIMIT);
					break;
				case CLOSEST:
					this.mClosestItems = this.mSpatialDataOrganizer.getClosest(mapView.getMapCenter(), ITEM_LIMIT);
					break;
			}

			super.draw(c, mapView, shadow);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
