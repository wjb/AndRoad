// Created by plusminus on 20:25:32 - 15.12.2008
package org.andnav2.sys.osb.views.overlay;

import java.util.List;

import org.andnav2.R;
import org.andnav2.osm.views.overlay.BaseOSMMapViewListItemizedOverlayWithFocus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;


public class OSMMapViewOSBOverlay extends BaseOSMMapViewListItemizedOverlayWithFocus<OSMMapViewOSBOverlayItem>{
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
		super(ctx,
				pList,
				ctx.getResources().getDrawable(R.drawable.osb_icon_bug_open),
				new Point(16,16),
				ctx.getResources().getDrawable(R.drawable.osb_marker_focused_base),
				new Point(16,20),
				Color.WHITE,
				pOnItemTapListener);

		this.mMarkerClosed = ctx.getResources().getDrawable(R.drawable.osb_icon_bug_closed);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * Need to override the default-functionality, because we need two different map-markers.
	 */
	@Override
	protected void onDrawItem(final Canvas c, final int index, final Point curScreenCoords) {
		final List<OSMMapViewOSBOverlayItem> overlayItems = this.getOverlayItems();
		if(overlayItems == null || overlayItems.get(index).isOpenBug()){
			super.onDrawItem(c, index, curScreenCoords);
		}else{
			/* Save a reference to the original marker. */
			final Drawable tmp = super.mMarkerFocusedBase;
			/* Swithc the marker that will be drawn with the 'closed'-marker. */
			super.mMarkerFocusedBase = this.mMarkerClosed;
			/* Make superclass draw with that marker. */
			super.onDrawItem(c, index, curScreenCoords);
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
