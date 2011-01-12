// Created by plusminus on 23:47:17 - 18.01.2009
package org.androad.sys.ors.views.overlay;

import org.osmdroid.views.overlay.OverlayItem;

import org.androad.sys.ors.adt.ts.TrafficItem;

import android.content.Context;

public class TrafficOverlayItem extends OverlayItem {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public TrafficOverlayItem(final Context ctx, final TrafficItem pFeature) {
		super(pFeature.getSeverity().name(), pFeature.getDescription(), pFeature);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
