// Created by plusminus on 7:28:43 PM - Mar 27, 2009
package org.androad.osm.views.overlay;

import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import org.andnav.osm.DefaultResourceProxyImpl;
import org.andnav.osm.views.overlay.OpenStreetMapViewOverlayItem;
import org.andnav.osm.views.overlay.OpenStreetMapViewItemizedOverlayWithFocus;

public class BaseOSMMapViewListItemizedOverlayWithFocus<T extends OpenStreetMapViewOverlayItem> extends OpenStreetMapViewItemizedOverlayWithFocus<T> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public BaseOSMMapViewListItemizedOverlayWithFocus(final Context ctx, final List<T> pList, final OnItemGestureListener<T> onItemTapListener) {
		super(ctx, pList, onItemTapListener);
	}

	public BaseOSMMapViewListItemizedOverlayWithFocus(final Context ctx, final List<T> pList, final Drawable pMarker, final Point pMarkerHotspot, final Drawable pMarkerFocusedBase, final Point pMarkerFocusedHotSpot, final int pFocusedBackgroundColor, final OnItemGestureListener<T> pOnItemTapListener) {
		super(ctx, pList, pMarker, pMarkerHotspot, pMarkerFocusedBase, pMarkerFocusedHotSpot, pFocusedBackgroundColor, pOnItemTapListener, new DefaultResourceProxyImpl(ctx));
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public List<T> getOverlayItems() {
		return this.mItemList;
	}

	public void setOverlayItems(final List<T> pItems) {
        this.mItemList.clear();
        this.mItemList.addAll(pItems);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}