package org.androad.sys.ors.rs.yournavigation;

import junit.framework.Assert;

import org.osmdroid.util.GeoPoint;

import org.androad.sys.ors.adt.rs.RoutePreferenceType;
import org.androad.sys.ors.util.constants.ORSXMLConstants;

public class YourNavigationRSRequestComposer implements ORSXMLConstants {

	/**
	 * 
	 * @param ctx
	 * @param nat
	 * @param start
	 * @param vias
	 * @param end
	 * @return
	 */
	public static String create(final GeoPoint start, final GeoPoint end, final RoutePreferenceType pRoutePreference){
		Assert.assertNotNull(start);
		Assert.assertNotNull(end);
		Assert.assertNotNull(pRoutePreference);

		final StringBuilder sb = new StringBuilder();
        sb.append("format=kml&layer=mapnik");
        if (pRoutePreference.mDefinedName.equals(RoutePreferenceType.PEDESTRIAN.mDefinedName)) {
            sb.append("&v=foot");
        } else if (pRoutePreference.mDefinedName.equals(RoutePreferenceType.BICYCLE.mDefinedName)) {
            sb.append("&v=bicycle");
        } else {
            sb.append("&v=motorcar");
        }
        sb.append("&fast=1");
        
        sb.append("&flon=").append(start.getLongitudeE6() / 1E6);
        sb.append("&flat=").append(start.getLatitudeE6() / 1E6);
        sb.append("&tlon=").append(end.getLongitudeE6() / 1E6);
        sb.append("&tlat=").append(end.getLatitudeE6() / 1E6);

		return sb.toString();
	}
}
