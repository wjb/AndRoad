package org.androad.sys.ors.ds.yournavigation;

import java.io.IOException;
import java.util.ArrayList;

import org.androad.sys.ors.ds.DSRequester;

import org.osmdroid.util.GeoPoint;

import org.androad.sys.ors.adt.ds.ORSPOI;
import org.androad.sys.ors.adt.ds.POIType;
import org.androad.sys.ors.exceptions.ORSException;
import org.androad.util.constants.Constants;

import android.content.Context;

public class YourNavigationDSRequester implements Constants, DSRequester {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

    public YourNavigationDSRequester() {
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

	/**
	 * @param gp
	 * @param pRadiusMeters
	 * @throws ORSException
	 */
	public ArrayList<ORSPOI> request(final Context ctx, final GeoPoint aGeoPoint, final POIType aPOIType, final int pRadiusMeters) throws IOException, ORSException {
		return new ArrayList<ORSPOI>();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
