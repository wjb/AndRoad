package org.androad.sys.ors.ds.cloudmade;

import junit.framework.Assert;

import org.osmdroid.util.GeoPoint;

import org.androad.sys.ors.adt.ds.POIType;
import org.androad.sys.ors.util.constants.ORSXMLConstants;

import android.content.Context;

public class CloudmadeDSRequestComposer implements ORSXMLConstants {

	/**
	 * 
	 * @param ctx
	 * @param aGeoPoint
	 * @param aPOITyp
	 * @param pRadiusMeters
	 * @return
	 */
	public static String create(final Context ctx, final GeoPoint aGeoPoint, final POIType aPOIType, final int pRadiusMeters){
		Assert.assertNotNull(aGeoPoint);
		Assert.assertNotNull(aPOIType);

		final StringBuilder sb = new StringBuilder();
        sb.append("around=").append(aGeoPoint.getLatitudeE6() / 1E6).append(",").append(aGeoPoint.getLongitudeE6() / 1E6);
        sb.append("&distance=").append(pRadiusMeters);
        sb.append("&object_type=").append(aPOIType.RAWNAME);

		return sb.toString();
	}
}
