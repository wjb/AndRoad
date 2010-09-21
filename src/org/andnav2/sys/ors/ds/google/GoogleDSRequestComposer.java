package org.andnav2.sys.ors.ds.google;

import android.net.Uri;

import java.util.List;

import junit.framework.Assert;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.ds.DirectoryType;
import org.andnav2.sys.ors.adt.ds.POIType;
import org.andnav2.sys.ors.util.Util;
import org.andnav2.sys.ors.util.constants.ORSXMLConstants;

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
