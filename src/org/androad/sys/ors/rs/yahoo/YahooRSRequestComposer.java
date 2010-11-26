package org.androad.sys.ors.rs.yahoo;

import java.util.List;

import junit.framework.Assert;

import org.andnav.osm.util.GeoPoint;

import org.androad.sys.ors.adt.rs.DirectionsLanguage;
import org.androad.sys.ors.util.constants.ORSXMLConstants;

public class YahooRSRequestComposer implements ORSXMLConstants {

	/**
	 * 
	 * @param ctx
	 * @param nat
	 * @param start
	 * @param vias
	 * @param end
	 * @return
	 */
	public static String create(final DirectionsLanguage nat, final GeoPoint start, final List<GeoPoint> vias, final GeoPoint end){
		Assert.assertNotNull(start);
		Assert.assertNotNull(end);

		final StringBuilder sb = new StringBuilder();
        sb.append("appid=ymapsaura");
        sb.append("&locale=").append(nat.ID);
        sb.append("&oq=").append(start.getLongitudeE6() / 1E6).append("%2C").append(start.getLatitudeE6() / 1E6);
        sb.append("&dq=").append(end.getLongitudeE6() / 1E6).append("%2C").append(end.getLatitudeE6() / 1E6);

		if(vias != null){
			for (int i = 0; i < vias.size(); i++) {
                final GeoPoint via = vias.get(i);
                sb.append("&w").append(i).append("lon=").append(via.getLatitudeE6() / 1E6);
                sb.append("&w").append(i).append("lat=").append(via.getLongitudeE6() / 1E6);
			}
		}

		return sb.toString();
	}
}
