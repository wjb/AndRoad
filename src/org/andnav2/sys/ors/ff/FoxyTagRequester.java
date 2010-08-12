// Created by Fabien Carrion
package org.andnav2.sys.ors.ff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.views.overlay.FoxyTagPoint;
import org.andnav2.util.constants.Constants;

import android.content.Context;
import android.util.Log;

public class FoxyTagRequester implements Constants{
	// ===========================================================
	// Constants
	// ===========================================================

	protected static final String FOXYTAG_BYGPS_BASEURL = "http://www.foxytag.com/php/mapgettags.php?";

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * @param pGeoPoint
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static ArrayList<FoxyTagPoint> request(final Context ctx, final GeoPoint pGeoPoint) throws IOException, MalformedURLException {
        ArrayList<FoxyTagPoint> foxytaglocation = new ArrayList<FoxyTagPoint>();
        final String mapPointString = "lat="  + pGeoPoint.getLatitudeE6() / 1E6 + "&lon=" + pGeoPoint.getLongitudeE6() / 1E6;
        final String queryString = FOXYTAG_BYGPS_BASEURL + mapPointString;

        /* Replace blanks with HTML-Equivalent. */
        URL url = new URL(queryString);

        Log.d(Constants.DEBUGTAG, url.toString());

        final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String inputLine = "";
        while ((inputLine = reader.readLine()) != null) {
            final int spacerPos = inputLine.indexOf('\t');
            final GeoPoint foxytagtag = new GeoPoint((int) (Double.parseDouble(inputLine.substring(0, spacerPos - 1)) * 1E6),
                                                       (int) (Double.parseDouble(inputLine.substring(spacerPos + 1, inputLine.length() - 1)) * 1E6));
            final FoxyTagPoint fpp = new FoxyTagPoint(foxytagtag, ctx);
            foxytaglocation.add(fpp);
        }

        Log.d(Constants.DEBUGTAG, "Foxytag " + foxytaglocation.size());

        return foxytaglocation;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
