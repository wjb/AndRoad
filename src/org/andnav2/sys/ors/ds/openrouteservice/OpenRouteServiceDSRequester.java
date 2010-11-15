// Created by plusminus on 18:22:30 - 05.11.2008
package org.andnav2.sys.ors.ds.openrouteservice;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.andnav2.sys.ors.ds.DSRequester;

import org.andnav.osm.util.GeoPoint;

import org.andnav2.sys.ors.adt.Error;
import org.andnav2.sys.ors.adt.ds.ORSPOI;
import org.andnav2.sys.ors.adt.ds.POIType;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.util.constants.Constants;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;

public class OpenRouteServiceDSRequester implements Constants, DSRequester {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

    private String url_directoryservice;

	// ===========================================================
	// Constructors
	// ===========================================================

    public OpenRouteServiceDSRequester(final String url) {
        this.url_directoryservice = url;
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
	 * @throws SAXException
	 * @throws ORSException
	 */
	public ArrayList<ORSPOI> request(final Context ctx, final GeoPoint aGeoPoint, final POIType aPOIType, final int pRadiusMeters) throws IOException, SAXException, ORSException {
		final URL requestURL = new URL(url_directoryservice);

		final HttpURLConnection acon = (HttpURLConnection) requestURL.openConnection();
		acon.setAllowUserInteraction(false);
		acon.setRequestMethod("POST");
		acon.setRequestProperty("Content-Type", "application/xml");
		acon.setDoOutput(true);
		acon.setDoInput(true);
		acon.setUseCaches(false);

		final BufferedWriter xmlOut;
		try{
			xmlOut = new BufferedWriter(new OutputStreamWriter(acon.getOutputStream()));
		}catch(final SocketException se){
			throw new ORSException(new Error(Error.ERRORCODE_UNKNOWN, Error.SEVERITY_ERROR, "org.andnav2.ors.ds.DSRequester.request(...)", "Host unreachable."));
		}catch(final UnknownHostException uhe){
			throw new ORSException(new Error(Error.ERRORCODE_UNKNOWN, Error.SEVERITY_ERROR, "org.andnav2.ors.ds.DSRequester.request(...)", "Host unresolved."));
		}

		final String routeRequest = OpenRouteServiceDSRequestComposer.create(ctx, aGeoPoint, aPOIType, pRadiusMeters);
		//		Log.d(DEBUGTAG, routeRequest);
		xmlOut.write(routeRequest);
		xmlOut.flush();
		xmlOut.close();


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
		final OpenRouteServiceDSParser openDSParser = new OpenRouteServiceDSParser();
		xr.setContentHandler(openDSParser);

		/* Parse the xml-data from our URL. */
		//		final char[] c = new char[100000];
		//		new InputStreamReader(acon.getInputStream()).read(c, 0, 100000);
		//		String s = new String(c);
		xr.parse(new InputSource(new BufferedInputStream(acon.getInputStream())));

		/* The Handler now provides the parsed data to us. */
		return openDSParser.getDSResponse();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
