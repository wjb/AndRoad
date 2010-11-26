// Created by plusminus on 00:50:30 - 20.01.2009
package org.androad.sys.ors.adt.ts;

import org.andnav.osm.util.GeoPoint;

import org.andnav2.traffic.tpeg.adt.rtm.table.RTM31_general_magnitude;

public class TrafficItem extends GeoPoint {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private String mDescription;
	private RTM31_general_magnitude mSeverity;

	// ===========================================================
	// Constructors
	// ===========================================================

	public TrafficItem(){
        super(0, 0);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

    public void setGeoPoint(GeoPoint gp) {
        this.setLongitudeE6(gp.getLongitudeE6());
        this.setLatitudeE6(gp.getLatitudeE6());
    }

	public String getDescription() {
		return this.mDescription;
	}

	public void setDescription(final String description) {
		this.mDescription = description;
	}

	public RTM31_general_magnitude getSeverity() {
		return this.mSeverity;
	}

	public void setSeverity(final RTM31_general_magnitude severity) {
		this.mSeverity = severity;
	}

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
