// Created by plusminus on 18:22:30 - 05.11.2008
package org.androad.sys.ors.ds.cloudmade;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.androad.sys.ors.ds.DSRequester;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.tileprovider.util.CloudmadeUtil;

import org.androad.osm.util.constants.OSMConstants;
import org.androad.sys.ors.adt.ds.ORSPOI;
import org.androad.sys.ors.adt.ds.POIType;
import org.androad.sys.ors.exceptions.ORSException;
import org.androad.sys.ors.util.Util;
import org.androad.util.constants.Constants;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.util.Log;

public class CloudmadeDSRequester implements Constants, DSRequester {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

    public CloudmadeDSRequester() {
    }

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * @param gp
	 * @param pRadiusMeters
	 * @throws ORSException
	 */
	public ArrayList<ORSPOI> request(final Context ctx, final GeoPoint aGeoPoint, final POIType aPOIType, final int pRadiusMeters) throws IOException, SAXException, ORSException {
        CloudmadeUtil.retrieveCloudmadeKey(ctx.getApplicationContext());

        final String cloudmadeurl = "http://geocoding.cloudmade.com/" + CloudmadeUtil.getCloudmadeKey() + "/geocoding/v2/find.plist?" + CloudmadeDSRequestComposer.create(ctx, aGeoPoint, aPOIType, pRadiusMeters);
        Log.d(OSMConstants.DEBUGTAG, "Cloudmade url " + cloudmadeurl);
        final URL requestURL = new URL(cloudmadeurl);

		final HttpURLConnection acon = (HttpURLConnection) requestURL.openConnection();
		acon.setAllowUserInteraction(false);
		acon.setRequestMethod("GET");
		acon.setRequestProperty("Content-Type", "application/xml");
		acon.setDoOutput(true);
		acon.setDoInput(true);
		acon.setUseCaches(false);

		/* Get a SAXParser from the SAXPArserFactory. */
		final SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp;
		try {
			sp = spf.newSAXParser();
		} catch (final ParserConfigurationException e) {
			throw new SAXException(e);
		}

		/* Get the XMLReader of the SAXParser we created. */
		final XMLReader xr = sp.getXMLReader();
		/* Create a new ContentHandler and apply it to the XML-Reader*/
		final CloudmadeDSParser openDSParser = new CloudmadeDSParser(aGeoPoint, aPOIType);
		xr.setContentHandler(openDSParser);

		/* Parse the xml-data from our URL. */
		xr.parse(new InputSource(new BufferedInputStream(acon.getInputStream())));

		/* The Handler now provides the parsed data to us. */
		return openDSParser.getDSResponse();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
