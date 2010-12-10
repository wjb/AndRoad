package org.androad.sys.ors.ds.google;

import android.net.Uri;

import junit.framework.Assert;

import org.andnav.osm.util.GeoPoint;

import org.androad.sys.ors.adt.ds.POIType;
import org.androad.sys.ors.util.constants.ORSXMLConstants;

import android.content.Context;

public class GoogleDSRequestComposer implements ORSXMLConstants {

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
        sb.append("output=js");
        sb.append("&hl=en");
        sb.append("&q=").append(aPOIType.RAWNAME);
        sb.append("&sll=").append(Uri.encode(aGeoPoint.toDoubleString()));

		return sb.toString();
	}
}
