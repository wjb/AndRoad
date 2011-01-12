// Created by plusminus on 19:06:15 - 25.05.2008
package org.androad.nav;

import java.util.List;

import org.osmdroid.util.GeoPoint;

public interface WayPointListener {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void onWaypointPassed(List<GeoPoint> waypointsLeft);

	public void onTargetReached();
}
