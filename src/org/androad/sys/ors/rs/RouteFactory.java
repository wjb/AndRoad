// Created by plusminus on 00:27:06 - 17.10.2008
package org.androad.sys.ors.rs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.andnav.osm.util.GeoPoint;

import org.androad.preferences.Preferences;
import org.androad.sys.ors.adt.aoi.AreaOfInterest;
import org.androad.sys.ors.adt.rs.DirectionsLanguage;
import org.androad.sys.ors.adt.rs.Route;
import org.androad.sys.ors.adt.rs.RoutePreferenceType;
import org.androad.sys.ors.exceptions.ORSException;
import org.androad.util.constants.Constants;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;


public class RouteFactory implements Constants{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static Route create(final Context ctx, final long pRouteHandle) throws ORSException, Exception{
		final DirectionsLanguage nat = Preferences.getDrivingDirectionsLanguage(ctx);
		try {
            final RSRequester rs = Preferences.getORSServer(ctx).ROUTESERVICE;
			final Route route = rs.request(ctx, nat, pRouteHandle);

			return route;
		} catch(final ORSException e){
			throw e;
		} catch (final MalformedURLException e) {
			Log.e(DEBUGTAG, "Error", e);
			throw new Exception(e);
		} catch (final IOException e) {
			Log.e(DEBUGTAG, "Error", e);
			throw new Exception(e);
		} catch (final SAXException e) {
			Log.e(DEBUGTAG, "Error", e);
			throw new Exception(e);
		}
	}

	public static Route create(final Context ctx, final GeoPoint start, final GeoPoint end, final ArrayList<GeoPoint> vias, final ArrayList<AreaOfInterest> pAvoidAreas, final boolean pSaveRoute) throws ORSException, Exception{
		final DirectionsLanguage nat = Preferences.getDrivingDirectionsLanguage(ctx);
		final boolean pAvoidHighways = Preferences.getAvoidHighways(ctx);
		final boolean pAvoidTolls = Preferences.getAvoidTolls(ctx);
		final boolean requestHandle = true;
		final RoutePreferenceType pRoutePreference = Preferences.getRoutePreferenceType(ctx);
		try {
            final RSRequester rs = Preferences.getORSServer(ctx).ROUTESERVICE;
			final Route route = rs.request(ctx, nat, start, vias, end, pRoutePreference, true, pAvoidTolls, pAvoidHighways, requestHandle, pAvoidAreas, pSaveRoute);

			route.getVias().addAll(vias);

			return route;
		} catch(final ORSException e){
			throw e;
		} catch (final MalformedURLException e) {
			Log.e(DEBUGTAG, "Error", e);
			throw new Exception(e);
		} catch (final IOException e) {
			Log.e(DEBUGTAG, "Error", e);
			throw new Exception(e);
		} catch (final SAXException e) {
			Log.e(DEBUGTAG, "Error", e);
			throw new Exception(e);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
