// Created by plusminus on 00:17:35 - 07.07.2008
package org.androad.ui.util;

import org.androad.util.constants.Constants;

import org.osmdroid.util.GeoPoint;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.widget.Toast;

public class Util implements Constants{
	// ===========================================================
	// Final Fields
	// ===========================================================

	private static final String URLBASE_MARKET_SEARCH_PACKAGENAME = "http://market.android.com/search?q=pname:";

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static void startUnknownActivity(final Context ctx, final String pAction, final String pMarketPackageName){
		startUnknownActivity(ctx, new Intent(pAction), pMarketPackageName);
	}

	public static void startUnknownActivity(final Context ctx, final Intent pIntent, final String pMarketPackageName){
		try{
			ctx.startActivity(pIntent);
		}catch(final ActivityNotFoundException anfe){
			final Intent marketIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(URLBASE_MARKET_SEARCH_PACKAGENAME + pMarketPackageName));
			ctx.startActivity(marketIntent);
		}
	}

	public static void sendExceptionEmail(final Context ctx, final String pBody) {
		openEmail(ctx, pBody, "AndRoad-Exception (v" + org.androad.util.Util.getVersionName(ctx) + ")", new String[]{"support@andnav.org"});
		Toast.makeText(ctx, "Please describe your bug!", Toast.LENGTH_LONG).show();
	}

	public static void openEmail(final Context ctx, final String pBody, final String pSubject, final String[] pReceivers) {
		final Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
		mailIntent.setType("plain/text");

		if(pReceivers != null && pReceivers.length > 0) {
			mailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, pReceivers);
		}

		if(pSubject != null) {
			mailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, pSubject);
		}

		if(pBody != null) {
			mailIntent.putExtra(android.content.Intent.EXTRA_TEXT, pBody);
		}

		ctx.startActivity(Intent.createChooser(mailIntent, "Select Mail Client"));
	}

	public static GeoPoint locationToGeoPoint(final Location aLoc){
		return new GeoPoint((int)(aLoc.getLatitude() * 1E6), (int)(aLoc.getLongitude() * 1E6));
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
