package org.andnav2.sys.ors.rs.yahoo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.util.Util;
import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.osm.views.util.StreamUtils;
import org.andnav2.sys.ors.adt.Error;
import org.andnav2.sys.ors.adt.aoi.AreaOfInterest;
import org.andnav2.sys.ors.adt.rs.DirectionsLanguage;
import org.andnav2.sys.ors.adt.rs.Route;
import org.andnav2.sys.ors.adt.rs.RoutePreferenceType;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.sys.ors.rs.RSRequester;
import org.andnav2.util.constants.Constants;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.util.Log;

public class YahooRSRequester implements Constants, OSMConstants, RSRequester {
	// ====================================
	// Constants
	// ====================================

	protected static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd_EEE_HH-mm-ss");

	// ====================================
	// Fields
	// ====================================

	// ===========================================================
	// Constructors
	// ===========================================================

    public YahooRSRequester() {
    }

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ====================================
	// Methods from Superclasses
	// ====================================

	// ====================================
	// Methods
	// ====================================

	public Route request(final Context ctx, final DirectionsLanguage nat, final long pRouteHandle) throws MalformedURLException, IOException, SAXException, ORSException {
        throw new ORSException(new Error(Error.ERRORCODE_UNKNOWN, Error.SEVERITY_ERROR, "org.andnav2.ors.rs.RSRequester.request(...)", "Operation not suported."));
    }

	public Route request(final Context ctx, final DirectionsLanguage nat, final GeoPoint start, final List<GeoPoint> vias, final GeoPoint end, final RoutePreferenceType pRoutePreference, final boolean pProvideGeometry, final boolean pAvoidTolls, final boolean pAvoidHighways, final boolean pRequestHandle, final ArrayList<AreaOfInterest> pAvoidAreas, final boolean pSaveRoute) throws MalformedURLException, IOException, SAXException, ORSException{
        final URL requestURL = new URL("http://maps.yahoo.com/services/us/directions?" + YahooRSRequestComposer.create(nat, start, vias, end));

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
		final YahooRSParser openLSParser = new YahooRSParser(null);
		xr.setContentHandler(openLSParser);

		/* Parse the xml-data from our URL. */

		if(!pSaveRoute){
			xr.parse(new InputSource(new BufferedInputStream(acon.getInputStream())));

			/* The Handler now provides the parsed data to us. */
			return openLSParser.getRoute();
		}else{
			final StringBuilder sb = new StringBuilder();

			int read = 0;
			final char[] buf = new char[StreamUtils.IO_BUFFER_SIZE];
			final InputStreamReader isr = new InputStreamReader(new BufferedInputStream(acon.getInputStream()));
			while((read = isr.read(buf)) != -1) {
				sb.append(buf, 0, read);
			}

			final byte[] readBytes = sb.toString().getBytes();

			xr.parse(new InputSource(new ByteArrayInputStream(readBytes)));

			/* The Handler now provides the parsed data to us. */
			final Route r = openLSParser.getRoute();

			/* Exception would have been thrown in invalid route. */
			try {
				// Ensure folder exists
				final String traceFolderPath = Util.getAndNavExternalStoragePath() + SDCARD_SAVEDROUTES_PATH;
				new File(traceFolderPath).mkdirs();

				// Create file and ensure that needed folders exist.
				final String filename = traceFolderPath + RSRequester.YAHOO_PREFIX + SDF.format(new Date(System.currentTimeMillis()));
				final File dest = new File(filename + ".route");

				// Write Data
				final OutputStream out = new BufferedOutputStream(new FileOutputStream(dest),StreamUtils.IO_BUFFER_SIZE);

				out.write(readBytes);
				out.flush();
				out.close();
			} catch (final Exception e) {
				Log.e(OSMConstants.DEBUGTAG, "File-Writing-Error", e);
			}

			return r;
        }

    }
}