// Created by plusminus on 20:25:32 - 15.12.2008
package org.androad.sys.osb.views.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import java.util.List;

import org.androad.R;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;

public class OSMMapViewOSBOverlay extends ItemizedOverlayWithFocus<OSMMapViewOSBOverlayItem>{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected Drawable mMarkerClosed;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewOSBOverlay(final Context ctx, final List<OSMMapViewOSBOverlayItem> pList, final OnItemGestureListener<OSMMapViewOSBOverlayItem> pOnItemTapListener) {
		super(pList, ctx.getResources().getDrawable(R.drawable.osb_icon_bug_open),
              ctx.getResources().getDrawable(R.drawable.osb_marker_focused_base),
              Color.WHITE, pOnItemTapListener, new DefaultResourceProxyImpl(ctx));

		this.mMarkerClosed = ctx.getResources().getDrawable(R.drawable.osb_icon_bug_closed);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * Need to override the default-functionality, because we need two different map-markers.
	 */
	@Override
	protected void onDrawItem(final Canvas c, final OSMMapViewOSBOverlayItem item, final Point curScreenCoords) {
		if(this.mItemList == null || item.isOpenBug()){
			super.onDrawItem(c, item, curScreenCoords);
		}else{
			/* Save a reference to the original marker. */
			final Drawable tmp = super.mMarkerFocusedBase;
			/* Swithc the marker that will be drawn with the 'closed'-marker. */
			super.mMarkerFocusedBase = this.mMarkerClosed;
			/* Make superclass draw with that marker. */
			super.onDrawItem(c, item, curScreenCoords);
			/* Revert changes. */
			super.mMarkerFocusedBase = tmp;
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
