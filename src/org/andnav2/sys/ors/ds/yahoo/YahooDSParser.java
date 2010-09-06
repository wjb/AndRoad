package org.andnav2.sys.ors.ds.yahoo;

import java.util.ArrayList;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.Error;
import org.andnav2.sys.ors.adt.ds.ORSPOI;
import org.andnav2.sys.ors.adt.ds.POIType;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.util.constants.Constants;
import org.andnav2.util.constants.TimeConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class YahooDSParser implements TimeConstants, Constants{
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

	public ArrayList<ORSPOI> getDSResponse(String json, final POIType aPOIType) throws ORSException{
        try {
            JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
            JSONArray listing = object.getJSONArray("listing");

            for(int i = 0; i < listing.length(); i++) {
                final JSONObject poi = listing.getJSONObject(i);

                final ORSPOI mCurPOI = new ORSPOI();
                final String poiname = poi.getString("title");
                final JSONObject ycats = poi.getJSONObject("ycatsprimary");
                final JSONObject data1 = ycats.optJSONObject("data");
                final JSONArray data2 = ycats.optJSONArray("data");

                String poitype = "";
                if (data1 != null) {
                    poitype = data1.getString("name");
                } else if (data2 != null) {
                    poitype = data2.getJSONObject(0).getString("name");
                }

                mCurPOI.setName(poiname);
                try {
                    mCurPOI.setPOIType(POIType.fromRawName(poitype));
                } catch (Exception e) {
                    mCurPOI.setPOIType(aPOIType);
                }

                final String distance = poi.getString("distance");
                mCurPOI.setDistance((int) (1609.344 * Float.parseFloat(distance)));

                final String lat = poi.getString("lat");
                final String lon = poi.getString("lon");
                mCurPOI.setGeoPoint(GeoPoint.fromDoubleString(lat + "," + lon, ','));

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
