package org.androad.sys.ors.rs.yahoo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

import org.andnav.osm.util.GeoPoint;

import org.androad.osm.util.Util;
import org.androad.osm.util.constants.OSMConstants;
import org.androad.osm.views.util.StreamUtils;
import org.androad.sys.ors.adt.Error;
import org.androad.sys.ors.adt.aoi.AreaOfInterest;
import org.androad.sys.ors.adt.rs.DirectionsLanguage;
import org.androad.sys.ors.adt.rs.Route;
import org.androad.sys.ors.adt.rs.RoutePreferenceType;
import org.androad.sys.ors.exceptions.ORSException;
import org.androad.sys.ors.rs.RSRequester;
import org.androad.util.constants.Constants;

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
        throw new ORSException(new Error(Error.ERRORCODE_UNKNOWN, Error.SEVERITY_ERROR, "org.androad.ors.rs.RSRequester.request(...)", "Operation not suported."));
    }

	public Route request(final Context ctx, final DirectionsLanguage nat, final GeoPoint start, final List<GeoPoint> vias, final GeoPoint end, final RoutePreferenceType pRoutePreference, final boolean pProvideGeometry, final boolean pAvoidTolls, final boolean pAvoidHighways, final boolean pRequestHandle, final ArrayList<AreaOfInterest> pAvoidAreas, final boolean pSaveRoute) throws MalformedURLException, IOException, SAXException, ORSException{
        return this.request(ctx, nat, start, vias, end, pRoutePreference, pProvideGeometry, pAvoidTolls, pAvoidHighways, pRequestHandle, pAvoidAreas, pSaveRoute, true);
    }

	public Route request(final Context ctx, final DirectionsLanguage nat, final GeoPoint start, final List<GeoPoint> vias, final GeoPoint end, final RoutePreferenceType pRoutePreference, final boolean pProvideGeometry, final boolean pAvoidTolls, final boolean pAvoidHighways, final boolean pRequestHandle, final ArrayList<AreaOfInterest> pAvoidAreas, final boolean pSaveRoute, final boolean pGetPartialsRoutes) throws MalformedURLException, IOException, SAXException, ORSException{
        final String yahoourl = "http://maps.yahoo.com/services/us/directions?" + YahooRSRequestComposer.create(nat, start, vias, end);
        Log.d(OSMConstants.DEBUGTAG, "Yahoo url " + yahoourl);
        final URL requestURL = new URL(yahoourl);

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
		final YahooRSParser openLSParser = new YahooRSParser();
		xr.setContentHandler(openLSParser);

		/* Parse the xml-data from our URL. */
        xr.parse(new InputSource(new BufferedInputStream(acon.getInputStream())));

        /* The Handler now provides the parsed data to us. */
        final Route r = openLSParser.getRoute();

		if(pSaveRoute){
			/* Exception would have been thrown in invalid route. */
			try {
				// Ensure folder exists
				final String traceFolderPath = Util.getAndNavExternalStoragePath() + SDCARD_SAVEDROUTES_PATH;
				new File(traceFolderPath).mkdirs();

				// Create file and ensure that needed folders exist.
				final String filename = traceFolderPath + SDF.format(new Date(System.currentTimeMillis()));
				final File dest = new File(filename + ".route");

				// Write Data
				final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dest));

                out.writeObject(r);
                StreamUtils.closeStream(out);
			} catch (final Exception e) {
				Log.e(OSMConstants.DEBUGTAG, "File-Writing-Error", e);
			}
        }

        if (pGetPartialsRoutes) {
            boolean mLoop = true;

            while (mLoop) {
                GeoPoint previous = r.getStart();
                mLoop = false;
                for (int i = 1; i < r.getPolyLine().size(); i++) {
                    final GeoPoint point = r.getPolyLine().get(i);
                    int distance = previous.distanceTo(point);
                    if (distance > 2500) {
                        Route partialroute = this.request(ctx, nat, previous, null, point, pRoutePreference, pProvideGeometry, pAvoidTolls, pAvoidHighways, pRequestHandle, null, false, false);
                        if (partialroute != null && partialroute.getPolyLine().size() > 2) {
                            int append = r.insert(partialroute, previous, point);
                            i += append;

                            if (append > 0)
                                mLoop = true;
                        }
                    }
                    previous = point;
                }
            }
        }

        r.finalizeRoute(vias);
        return r;
    }
}