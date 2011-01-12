package org.androad.sys.ors.lus.yournavigation;

import java.util.ArrayList;
import java.util.StringTokenizer;

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

public class YourNavigationLUSParser extends DefaultHandler implements TimeConstants, Constants {
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

	private boolean inSearchresults = false;
    private boolean inPlace = false;
    private boolean inReversegeocode = false;
    private boolean inResult = false;
	private boolean inAddressparts = false;
	private boolean inRoad = false;
	private boolean inTown = false;
	private boolean inState = false;
	private boolean inCountry = false;
	private boolean inCountrycode = false;

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

		if(localName.equals("searchresults")){
			this.inSearchresults = true;
        } else if(localName.equals("place")){
			this.inPlace = true;

			final String lat = attributes.getValue("", "lat");
			final String lon = attributes.getValue("", "lon");
            final double a = Double.parseDouble(lat);
            final double b = Double.parseDouble(lon);

            final GeoPoint gp = new GeoPoint((int) (a * 1E6), (int) (b * 1E6));
            this.mTmpGeocodedAddress = new GeocodedAddress(gp);
            this.mAddresses.add(this.mTmpGeocodedAddress);

            this.mTmpGeocodedAddress.setAccuracy(1);
			final String displayname = attributes.getValue("", "display_name");
			final String type = attributes.getValue("", "type");
			if(displayname != null) {
                final StringTokenizer st = new StringTokenizer(displayname, ",");
                final ArrayList<String> names = new ArrayList<String>();
                while(st.hasMoreTokens()) {
                    names.add(st.nextToken().trim());
                }

                final String country = ", " + names.get(names.size() - 1);
                if (type.compareTo("town") == 0) {
                    this.mTmpGeocodedAddress.setMunicipality(names.get(0));
                    this.mTmpGeocodedAddress.setCountrySubdivision(names.get(1) + country);
                } else if (type.compareTo("residential") == 0) {
                    if (names.size() == 4) {
                        this.mTmpGeocodedAddress.setStreetNameOfficial(names.get(0));
                        this.mTmpGeocodedAddress.setMunicipality(names.get(1));
                        this.mTmpGeocodedAddress.setCountrySubdivision(names.get(2) + country);
                    } else if (names.size() == 5) {
                        this.mTmpGeocodedAddress.setStreetNameOfficial(names.get(0));
                        this.mTmpGeocodedAddress.setMunicipality(names.get(1));
                        this.mTmpGeocodedAddress.setPostalCode(names.get(2));
                        this.mTmpGeocodedAddress.setCountrySubdivision(names.get(3) + country);
                    } else {
                        this.mTmpGeocodedAddress.setMunicipality(names.get(0));
                        this.mTmpGeocodedAddress.setCountrySubdivision(names.get(1) + country);
                    }
                } else {
                    this.mTmpGeocodedAddress.setCountrySubdivision(names.get(0) + country);
                }
				this.mTmpGeocodedAddress.setNationality(Country.UNKNOWN);
			}

        } else if(localName.equals("reversegeocode")){
            this.inReversegeocode = true;

			final String query = attributes.getValue("", "querystring");
            final StringTokenizer st = new StringTokenizer(query, "&");
            if (!st.hasMoreTokens())
                return;

            final double b;
            final double a;

            try {
                b = Double.parseDouble(st.nextToken().substring(4));
                a = Double.parseDouble(st.nextToken().substring(4));
            } catch (Exception e) {
                return;
            }

            final GeoPoint gp = new GeoPoint((int) (a * 1E6), (int) (b * 1E6));
            this.mTmpGeocodedAddress = new GeocodedAddress(gp);
            this.mTmpGeocodedAddress.setAccuracy(1);
            this.mAddresses.add(this.mTmpGeocodedAddress);
        } else if(localName.equals("result")){
			this.inResult = true;
        } else if(localName.equals("addressparts")){
			this.inAddressparts = true;
        } else if(localName.equals("road")){
			this.inRoad = true;
		} else if(localName.equals("town")){
			this.inTown = true;
		} else if(localName.equals("state")){
			this.inState = true;
		} else if(localName.equals("country")){
			this.inCountry = true;
		} else if(localName.equals("country_code")){
			this.inCountrycode = true;
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

		if(localName.equals("searchresults")){
			this.inSearchresults = false;
        } else if(localName.equals("place")){
			this.inPlace = false;
        } else if(localName.equals("reversegeocode")){
            this.inReversegeocode = false;
        } else if(localName.equals("result")){
			this.inResult = false;
        } else if(localName.equals("addressparts")){
			this.inAddressparts = false;
        } else if(localName.equals("road")){
			this.inRoad = false;
            this.mTmpGeocodedAddress.setStreetNameOfficial(this.sb.toString());
		} else if(localName.equals("town")){
			this.inTown = false;
		} else if(localName.equals("state")){
			this.inState = false;
            this.mTmpGeocodedAddress.setCountrySubdivision(this.sb.toString());
		} else if(localName.equals("country")){
			this.inCountry = false;
		} else if(localName.equals("country_code")){
			this.inCountrycode = false;
            this.mTmpGeocodedAddress.setNationality(Country.fromAbbreviation(this.sb.toString()));
		} else {
			Log.w(DEBUGTAG, "Unexpected end-tag: '" + name + "'");
		}

		// Reset the stringbuffer
		this.sb.setLength(0);

		super.endElement(uri, localName, name);
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

}
