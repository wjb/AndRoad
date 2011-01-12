package org.androad.sys.ors.ds.google;

import java.util.ArrayList;

import org.osmdroid.util.GeoPoint;

import org.androad.sys.ors.adt.Error;
import org.androad.sys.ors.adt.ds.ORSPOI;
import org.androad.sys.ors.adt.ds.POIType;
import org.androad.sys.ors.exceptions.ORSException;
import org.androad.util.constants.Constants;
import org.androad.util.constants.TimeConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class GoogleDSParser implements TimeConstants, Constants{
	// ====================================
	// Constants
	// ====================================

	// ====================================
	// Fields
	// ====================================

	private final ArrayList<Error> mErrors = new ArrayList<Error>();

	private ArrayList<ORSPOI> mPOIs = new ArrayList<ORSPOI>();

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArrayList<Error> getErrors(){
		return this.mErrors;
	}

	public ArrayList<ORSPOI> getDSResponse(final String page, final GeoPoint aGeoPoint, final POIType aPOIType) throws ORSException{
        final int spacerStartJS1 = page.indexOf("//<![CDATA[");
        final int spacerStartJS2 = page.indexOf('{', spacerStartJS1);
        final int spacerStartJS3 = page.indexOf('{', spacerStartJS2 + 1);
        final int spacerStartJS4 = page.indexOf("overlays:", spacerStartJS3);

        final int spacerEndJS1 = page.lastIndexOf("//]]>");
        final int spacerEndJS2 = page.lastIndexOf('}', spacerEndJS1);
        final int spacerEndJS3 = page.lastIndexOf('}', spacerEndJS2 - 1);
        final int spacerEndJS4 = page.lastIndexOf("]}]}", spacerEndJS3);

        final String json = page.substring(spacerStartJS4 + 9, spacerEndJS4 + 4);

        try {
            JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
            JSONArray markers = object.getJSONArray("markers");

            for(int i = 0; i < markers.length(); i++) {
                final JSONObject poi = markers.getJSONObject(i);

                final ORSPOI mCurPOI = new ORSPOI();
                final String poiname = poi.getString("name");
                mCurPOI.setName(poiname);
                mCurPOI.setPOIType(aPOIType);

                final JSONObject latlng = poi.getJSONObject("latlng");
                final String lat = latlng.getString("lat");
                final String lon = latlng.getString("lng");
                final GeoPoint gp = GeoPoint.fromDoubleString(lat + "," + lon, ',');
                final int distance = aGeoPoint.distanceTo(gp);

                mCurPOI.setGeoPoint(gp);
                mCurPOI.setDistance(distance);

                this.mPOIs.add(mCurPOI);
            }
        } catch (Exception e) {
            this.mErrors.add(new Error("err", "sev", "", e.toString()));
        }

		if(this.mErrors != null && this.mErrors.size() > 0) {
			throw new ORSException(this.mErrors);
		}

		return this.mPOIs;
	}

	// ====================================
	// Methods from Superclasses
	// ====================================

	// ====================================
	// Helper-Methods
	// ====================================
}
