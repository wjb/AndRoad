package org.androad.sys.ors.rs.cloudmade;

import java.util.List;

import junit.framework.Assert;

import org.osmdroid.util.GeoPoint;

import org.androad.sys.ors.adt.rs.DirectionsLanguage;
import org.androad.sys.ors.adt.rs.RoutePreferenceType;
import org.androad.sys.ors.util.constants.ORSXMLConstants;

public class CloudmadeRSRequestComposer implements ORSXMLConstants {

	/**
	 * 
	 * @param ctx
	 * @param nat
	 * @param start
	 * @param vias
	 * @param end
	 * @return
	 */
	public static String create(final DirectionsLanguage nat, final GeoPoint start, final List<GeoPoint> vias, final GeoPoint end, final RoutePreferenceType pRoutePreference){
		Assert.assertNotNull(start);
		Assert.assertNotNull(end);
		Assert.assertNotNull(pRoutePreference);

		final StringBuilder sb = new StringBuilder();
        sb.append(start.getLatitudeE6() / 1E6).append(",").append(start.getLongitudeE6() / 1E6);
		if(vias != null && vias.size() > 0){
            sb.append(",[");
			for (int i = 0; i < vias.size(); i++) {
                final GeoPoint via = vias.get(i);
                sb.append(via.getLatitudeE6() / 1E6).append(",").append(via.getLongitudeE6() / 1E6);
                if (i + 1 != vias.size()) {
                    sb.append(",");
                }
			}
            sb.append("]");
		}
        sb.append(",").append(end.getLatitudeE6() / 1E6).append(",").append(end.getLongitudeE6() / 1E6).append("/");

        if (pRoutePreference.mDefinedName.equals(RoutePreferenceType.PEDESTRIAN.mDefinedName)) {
            sb.append("foot");
        } else if (pRoutePreference.mDefinedName.equals(RoutePreferenceType.BICYCLE.mDefinedName)) {
            sb.append("bicycle");
        } else {
            sb.append("car/fastest");
        }
        sb.append(".gpx?lang=").append(nat.ID).append("&units=km");

		return sb.toString();
	}
}
