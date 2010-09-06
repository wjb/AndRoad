package org.andnav2.sys.ors.ds.yahoo;

import java.util.List;

import junit.framework.Assert;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.ds.DirectoryType;
import org.andnav2.sys.ors.adt.ds.POIType;
import org.andnav2.sys.ors.util.Util;
import org.andnav2.sys.ors.util.constants.ORSXMLConstants;

import android.content.Context;

public class YahooDSRequestComposer implements ORSXMLConstants {

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
        sb.append("stx=").append(aPOIType.RAWNAME);
        sb.append("&lat=").append(aGeoPoint.getLatitudeE6() / 1E6);
        sb.append("&lon=").append(aGeoPoint.getLongitudeE6() / 1E6);
        sb.append("&radius=").append(pRadiusMeters / 1000);

		return sb.toString();
	}
}
