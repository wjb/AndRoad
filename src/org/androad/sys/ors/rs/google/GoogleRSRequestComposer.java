package org.androad.sys.ors.rs.google;

import java.util.List;

import junit.framework.Assert;

import org.andnav.osm.util.GeoPoint;

import org.androad.sys.ors.adt.rs.DirectionsLanguage;
import org.androad.sys.ors.adt.rs.RoutePreferenceType;
import org.androad.sys.ors.util.constants.ORSXMLConstants;

public class GoogleRSRequestComposer implements ORSXMLConstants {

	/**
	 * 
	 * @param ctx
	 * @param nat
	 * @param start
	 * @param vias
	 * @param end
	 * @return
	 */
	public static String create(final DirectionsLanguage nat, final GeoPoint start, final List<GeoPoint> vias, final GeoPoint end, final RoutePreferenceType pRoutePreference, final boolean pAvoidTolls, final boolean pAvoidHighways){
		Assert.assertNotNull(start);
		Assert.assertNotNull(end);
		Assert.assertNotNull(pRoutePreference);

		final StringBuilder sb = new StringBuilder();
        sb.append("output=kml&doflg=ptk");
        sb.append("&hl=").append(nat.ID);
        sb.append("&saddr=").append(start.getLatitudeE6() / 1E6).append("%2C").append(start.getLongitudeE6() / 1E6);

        boolean addDirflg = false;
        final StringBuilder dirflg = new StringBuilder("&dirflg=");

        if (pAvoidHighways) {
            dirflg.append("h");
            addDirflg = true;
        }
        if (pAvoidTolls) {
            dirflg.append("t");
            addDirflg = true;
        }
        if (pRoutePreference.mDefinedName.equals(RoutePreferenceType.PEDESTRIAN.mDefinedName)) {
            dirflg.append("w");
            addDirflg = true;
        }
        if (pRoutePreference.mDefinedName.equals(RoutePreferenceType.BICYCLE.mDefinedName)) {
            dirflg.append("b");
            addDirflg = true;
        }
        if (addDirflg) {
            sb.append(dirflg);
        }

        sb.append("&daddr=").append(end.getLatitudeE6() / 1E6).append("%2C").append(end.getLongitudeE6() / 1E6);
		if(vias != null){
			for (int i = 0; i < vias.size(); i++) {
                final GeoPoint via = vias.get(i);
                sb.append("+to:").append(via.getLatitudeE6() / 1E6).append("%2C").append(via.getLongitudeE6() / 1E6);
			}
		}

		return sb.toString();
	}
}
