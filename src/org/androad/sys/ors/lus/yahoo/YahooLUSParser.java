package org.androad.sys.ors.lus.yahoo;

import java.util.ArrayList;

import org.osmdroid.util.GeoPoint;

import org.androad.sys.ors.adt.Error;
import org.androad.sys.ors.adt.GeocodedAddress;
import org.androad.sys.ors.adt.lus.Country;
import org.androad.sys.ors.exceptions.ORSException;
import org.androad.util.constants.Constants;
import org.androad.util.constants.TimeConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class YahooLUSParser extends DefaultHandler implements TimeConstants, Constants {
	// ====================================
	// Constants
	// ====================================

	protected static final int LATITUDE_OVERMAX = (int)(81 * 1E6);
	protected static final int LONGITUDE_OVERMAX = (int)(181 * 1E6);

	// ====================================
	// Fields
	// ====================================

	private final ArrayList<Error> mErrors = new ArrayList<Error>();

	private ArrayList<GeocodedAddress> mAddresses;

	private float quality;
	private double a = -1;
	private double b = -1;

	private boolean inResultSet = false;
    private boolean inError = false;
    private boolean inErrorMessage = false;
    private boolean inLocale = false;
	private boolean inQuality = false;
	private boolean inFound = false;
	private boolean inResult = false;
	private boolean inquality = false;
	private boolean inlatitude = false;
	private boolean inlongitude = false;
	private boolean inoffsetlat = false;
	private boolean inoffsetlon = false;
	private boolean inradius = false;
	private boolean inname = false;
	private boolean inline1 = false;
	private boolean inline2 = false;
	private boolean inline3 = false;
	private boolean inline4 = false;
	private boolean inhouse = false;
	private boolean instreet = false;
	private boolean inxstreet = false;
	private boolean inunittype = false;
	private boolean inunit = false;
	private boolean inpostal = false;
	private boolean inneighborhood = false;
	private boolean incity = false;
	private boolean incounty = false;
	private boolean instate = false;
	private boolean incountry = false;
	private boolean incountrycode = false;
	private boolean instatecode = false;
	private boolean incountycode = false;
	private boolean inuzip = false;
	private boolean inhash = false;
	private boolean inwoeid = false;
	private boolean inwoetype = false;

	private GeocodedAddress mTmpGeocodedAddress;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArrayList<Error> getErrors(){
		return this.mErrors;
	}

	public ArrayList<GeocodedAddress> getAddresses() throws ORSException{
		if(this.mErrors != null && this.mErrors.size() > 0) {
			throw new ORSException(this.mErrors);
		}

		return this.mAddresses;
	}

	// ====================================
	// Methods from Superclasses
	// ====================================

	@Override
	public void startDocument() throws SAXException {
		this.mAddresses = new ArrayList<GeocodedAddress>();
		super.startDocument();
	}

	@Override
	public void startElement(final String uri, final String localName, final String name, final Attributes attributes) throws SAXException {
		this.sb.setLength(0);

		if(localName.equals("ResultSet")){
			this.inResultSet = true;
        } else if(localName.equals("Error")){
			this.inError = true;
        } else if(localName.equals("ErrorMessage")){
            this.inErrorMessage = true;
        } else if(localName.equals("Locale")){
			this.inLocale = true;
        } else if(localName.equals("Quality")){
			this.inQuality = true;
        } else if(localName.equals("Found")){
			this.inFound = true;
		} else if(localName.equals("Result")){
			this.inResult = true;
		} else if(localName.equals("quality")){
			this.inquality = true;
		} else if(localName.equals("latitude")){
			this.inlatitude = true;
		} else if(localName.equals("longitude")){
			this.inlongitude = true;
		} else if(localName.equals("offsetlat")){
			this.inoffsetlat = true;
		} else if(localName.equals("offsetlon")){
			this.inoffsetlon = true;
		} else if(localName.equals("radius")){
			this.inradius = true;
		} else if(localName.equals("name")){
			this.inname = true;
		} else if(localName.equals("line1")){
			this.inline1 = true;
		} else if(localName.equals("line2")){
			this.inline2 = true;
		} else if(localName.equals("line3")){
			this.inline3 = true;
		} else if(localName.equals("line4")){
			this.inline4 = true;
		} else if(localName.equals("house")){
			this.inhouse = true;
		} else if(localName.equals("street")){
			this.instreet = true;
		} else if(localName.equals("xstreet")){
			this.inxstreet = true;
		} else if(localName.equals("unittype")){
			this.inunittype = true;
		} else if(localName.equals("unit")){
			this.inunit = true;
		} else if(localName.equals("postal")){
			this.inpostal = true;
		} else if(localName.equals("neighborhood")){
			this.inneighborhood = true;
		} else if(localName.equals("city")){
			this.incity = true;
		} else if(localName.equals("county")){
			this.incountry = true;
		} else if(localName.equals("state")){
			this.instate = true;
		} else if(localName.equals("country")){
			this.incountry = true;
		} else if(localName.equals("countrycode")){
			this.incountrycode = true;
		} else if(localName.equals("statecode")){
			this.instatecode = true;
		} else if(localName.equals("countycode")){
			this.incountycode = true;
		} else if(localName.equals("uzip")){
			this.inuzip = true;
		} else if(localName.equals("hash")){
			this.inhash = true;
		} else if(localName.equals("woeid")){
			this.inwoeid = true;
		} else if(localName.equals("woetype")){
			this.inwoetype = true;
		} else {
			Log.w(DEBUGTAG, "Unexpected tag: '" + name + "'");
		}

		super.startElement(uri, localName, name, attributes);
	}

	protected StringBuilder sb = new StringBuilder();

	@Override
	public void characters(final char[] chars, final int start, final int length) throws SAXException {
		this.sb.append(chars, start, length);
		super.characters(chars, start, length);
	}

	@Override
	public void endElement(final String uri, final String localName, final String name) throws SAXException {

		if(localName.equals("ResultSet")){
			this.inResultSet = false;
        } else if(localName.equals("Error")){
			this.inError = false;
        } else if(localName.equals("ErrorMessage")){
            this.inErrorMessage = false;
            String error = this.sb.toString();
            if (!"No error".equals(error)) {
                this.mErrors.add(new Error("Err", "Sev", "", error));
            }
        } else if(localName.equals("Locale")){
			this.inLocale = false;
        } else if(localName.equals("Quality")){
			this.inQuality = false;
        } else if(localName.equals("Found")){
			this.inFound = false;
		} else if(localName.equals("Result")){
			this.inResult = false;
		} else if(localName.equals("quality")){
            quality = Float.parseFloat(this.sb.toString());
			this.inquality = false;
		} else if(localName.equals("latitude")){
            this.a = Double.parseDouble(this.sb.toString());
			this.inlatitude = false;
		} else if(localName.equals("longitude")){
            this.b = Double.parseDouble(this.sb.toString());
			this.inlongitude = false;
		} else if(localName.equals("offsetlat")){
			this.inoffsetlat = false;
		} else if(localName.equals("offsetlon")){
			this.inoffsetlon = false;
		} else if(localName.equals("radius")){
			this.inradius = false;
		} else if(localName.equals("name")){
			this.inname = false;
		} else if(localName.equals("line1")){
			this.inline1 = false;
		} else if(localName.equals("line2")){
			this.inline2 = false;
		} else if(localName.equals("line3")){
			this.inline3 = false;
		} else if(localName.equals("line4")){
			this.inline4 = false;
		} else if(localName.equals("house")){
			this.inhouse = false;
		} else if(localName.equals("street")){
            this.mTmpGeocodedAddress.setStreetNameOfficial(this.sb.toString());
			this.instreet = false;
		} else if(localName.equals("xstreet")){
			this.inxstreet = false;
		} else if(localName.equals("unittype")){
			this.inunittype = false;
		} else if(localName.equals("unit")){
			this.inunit = false;
		} else if(localName.equals("postal")){
			this.mTmpGeocodedAddress.setPostalCode(this.sb.toString());
			this.inpostal = false;
		} else if(localName.equals("neighborhood")){
			this.inneighborhood = false;
		} else if(localName.equals("city")){
			this.incity = false;
		} else if(localName.equals("county")){
            this.mTmpGeocodedAddress.setMunicipality(this.sb.toString());
			this.incountry = false;
		} else if(localName.equals("state")){
            this.mTmpGeocodedAddress.setCountrySubdivision(this.sb.toString());
			this.instate = false;
		} else if(localName.equals("country")){
			this.incountry = false;
		} else if(localName.equals("countrycode")){
            this.mTmpGeocodedAddress.setNationality(Country.fromAbbreviation(this.sb.toString()));
			this.incountrycode = false;
		} else if(localName.equals("statecode")){
			this.instatecode = false;
		} else if(localName.equals("countycode")){
			this.incountycode = false;
		} else if(localName.equals("uzip")){
			this.inuzip = false;
		} else if(localName.equals("hash")){
			this.inhash = false;
		} else if(localName.equals("woeid")){
			this.inwoeid = false;
		} else if(localName.equals("woetype")){
			this.inwoetype = false;
		} else {
			Log.w(DEBUGTAG, "Unexpected end-tag: '" + name + "'");
		}

		// Reset the stringbuffer
		this.sb.setLength(0);

        if (this.a != -1 && this.b != -1) {
			final GeoPoint gp = new GeoPoint((int) (a * 1E6), (int) (b * 1E6));
			this.mTmpGeocodedAddress = new GeocodedAddress(gp);
            this.mTmpGeocodedAddress.setAccuracy(quality);
			this.mAddresses.add(this.mTmpGeocodedAddress);

            this.a = -1;
            this.b = -1;
        }

		super.endElement(uri, localName, name);
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

}
