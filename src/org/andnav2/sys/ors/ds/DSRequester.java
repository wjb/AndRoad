// Created by plusminus on 18:22:30 - 05.11.2008
package org.andnav2.sys.ors.ds;

import java.io.IOException;
import java.util.ArrayList;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.ds.ORSPOI;
import org.andnav2.sys.ors.adt.ds.POIType;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.xml.sax.SAXException;

import android.content.Context;

public interface DSRequester {
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

	public ArrayList<ORSPOI> request(final Context ctx, final GeoPoint aGeoPoint, final POIType aPOIType, final int pRadiusMeters) throws IOException, SAXException, ORSException;

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
