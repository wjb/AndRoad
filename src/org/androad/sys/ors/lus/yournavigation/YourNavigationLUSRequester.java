package org.androad.sys.ors.lus.yournavigation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.andnav.osm.util.GeoPoint;

import org.androad.sys.ors.adt.Error;
import org.androad.sys.ors.adt.GeocodedAddress;
import org.androad.sys.ors.adt.lus.Country;
import org.androad.sys.ors.adt.lus.ICountrySubdivision;
import org.androad.sys.ors.adt.lus.ReverseGeocodePreferenceType;
import org.androad.sys.ors.exceptions.ORSException;
import org.androad.sys.ors.lus.LUSRequester;
import org.androad.util.constants.Constants;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

public class YourNavigationLUSRequester implements Constants, LUSRequester {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final boolean WORKAROUND_STRUCTURED_AS_FREEFORM = true;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

    public YourNavigationLUSRequester() {
    }

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArrayList<GeocodedAddress> requestReverseGeocode(final Context ctx, final GeoPoint aGeoPoint, final ReverseGeocodePreferenceType aPreferenceType) throws MalformedURLException, IOException, SAXException, ORSException{
		if(Math.abs(aGeoPoint.getLatitudeE6()) < 10000 && Math.abs(aGeoPoint.getLongitudeE6()) < 10000) {
			return null;
		}

		return request(YourNavigationLUSRequestComposer.reverseGeocode(aGeoPoint));
	}

	public ArrayList<GeocodedAddress> requestFreeformAddress(final Context ctx, final Country nat, final String freeFormAddress) throws MalformedURLException, IOException, SAXException, ORSException{
		return request(YourNavigationLUSRequestComposer.createFreeformAddressRequest(nat, freeFormAddress));
	}

	public ArrayList<GeocodedAddress> requestStreetaddressCity(final Context ctx, final Country nat, final ICountrySubdivision pCountrySubdivision, final String pCity, final String pStreetName, final String pStreetNumber) throws MalformedURLException, IOException, SAXException, ORSException{
		if(shouldDoWorkaround(nat)) {
			return request(YourNavigationLUSRequestComposer.createFreeformAddressRequest(nat, structuredToFreeform(pCountrySubdivision, pCity, pStreetName, pStreetNumber)));
		} else {
			return request(YourNavigationLUSRequestComposer.createStreetaddressCityRequest(nat, pCountrySubdivision, pCity, pStreetName, pStreetNumber));
		}
	}

	public ArrayList<GeocodedAddress> requestStreetaddressPostalcode(final Context ctx, final Country nat, final ICountrySubdivision pCountrySubdivision, final String pPostalCode, final String pStreetName, final String pStreetNumber) throws MalformedURLException, IOException, SAXException, ORSException{
		if(shouldDoWorkaround(nat)) {
			return request(YourNavigationLUSRequestComposer.createFreeformAddressRequest(nat, structuredToFreeform(pCountrySubdivision, pPostalCode, pStreetName, pStreetNumber)));
		} else {
			return request(YourNavigationLUSRequestComposer.createStreetaddressPostalcodeRequest(nat, pCountrySubdivision, pPostalCode, pStreetName, pStreetNumber));
		}
	}

	private boolean shouldDoWorkaround(final Country nat) {
		final boolean doWorkaround;
		switch(nat){
			case USA:
			case CANADA:
				doWorkaround = false;
				break;
			default:
				doWorkaround = WORKAROUND_STRUCTURED_AS_FREEFORM; // true
		}
		return doWorkaround;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	private String structuredToFreeform(final ICountrySubdivision pCountrySubdivision, final String pCityOrZipCode, final String pStreetName, final String pStreetNumber) throws ORSException {
		/* Either one of these has to be set. */
		if((pCityOrZipCode == null || pCityOrZipCode.length() == 0) && (pStreetName == null || pStreetName.length() == 0)) {
			throw new ORSException(new Error(Error.ERRORCODE_UNKNOWN, Error.SEVERITY_ERROR, "org.androad.ors.lus.structuredToFreeform.LUSRequester.structuredToFreeform()", "Either street/zip or streetname has to be set."));
		}

		final StringBuilder sb = new StringBuilder();

		if(pCityOrZipCode != null){
			sb.append(pCityOrZipCode);
			if(pStreetName != null){
				sb.append(',');
			}
		}
		if(pStreetName != null){
			sb.append(pStreetName);
			if(pStreetNumber != null){
				sb.append(' ')
				.append(pStreetNumber);
			}
		}

		return sb.toString();
	}

	private ArrayList<GeocodedAddress> request(final String locationRequest) throws SAXException, ORSException, IOException{
		final URL requestURL = new URL("http://yournavigation.org/transport.php?url=http://nominatim.openstreetmap.org/" + locationRequest);

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
		final DefaultHandler openLUSParser = new YourNavigationLUSParser();
		xr.setContentHandler(openLUSParser);

		/* Parse the xml-data from our URL. */
		try{
			xr.parse(new InputSource(new BufferedInputStream(acon.getInputStream())));
		}catch(final Exception e){
			Log.e(DEBUGTAG, "Error", e);
		}

		/* The Handler now provides the parsed data to us. */
        return ((YourNavigationLUSParser)openLUSParser).getAddresses();
	}


	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
