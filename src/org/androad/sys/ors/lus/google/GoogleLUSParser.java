package org.androad.sys.ors.lus.google;

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

public class GoogleLUSParser extends DefaultHandler implements TimeConstants, Constants {
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
    private String thorough;
    private String locality;
    private String administrative;
	private String country;

	private boolean inKml = false;
    private boolean inResponse = false;
    private boolean inName = false;
    private boolean inStatus = false;
	private boolean inCode = false;
	private boolean inRequest = false;
	private boolean inPlacemark = false;
	private boolean inAddress = false;
	private boolean inAddressDetails = false;
	private boolean inCountry = false;
	private boolean inCountryNameCode = false;
	private boolean inCountryName = false;
	private boolean inAdministrativeArea = false;
	private boolean inAdministrativeAreaName = false;
	private boolean inLocality = false;
	private boolean inLocalityName = false;
    private boolean inThoroughfare = false;
    private boolean inThoroughfareName = false;
	private boolean inExtendedData = false;
	private boolean inLatLonBox = false;
	private boolean inPoint = false;
	private boolean inCoordinates = false;

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

		if(localName.equals("kml")){
			this.inKml = true;
        } else if(localName.equals("Response")){
            this.inResponse = true;
        } else if(localName.equals("name")){
            this.inName = true;
        } else if(localName.equals("Status")){
            this.inStatus = true;
        } else if(localName.equals("code")){
            this.inCode = true;
        } else if(localName.equals("request")){
            this.inRequest = true;
        } else if(localName.equals("Placemark")){
            this.inPlacemark = true;
        } else if(localName.equals("address")){
            this.inAddress = true;
        } else if(localName.equals("AddressDetails")){
            this.quality = Float.parseFloat(attributes.getValue("", "Accuracy"));
            this.inAddressDetails = true;
        } else if(localName.equals("Country")){
            this.inCountry = true;
        } else if(localName.equals("CountryNameCode")){
            this.inCountryNameCode = true;
        } else if(localName.equals("CountryName")){
            this.inCountryName = true;
        } else if(localName.equals("AdministrativeArea")){
            this.inAdministrativeArea = true;
        } else if(localName.equals("AdministrativeAreaName")){
            this.inAdministrativeAreaName = true;
        } else if(localName.equals("Locality")){
            this.inLocality = true;
        } else if(localName.equals("LocalityName")){
            this.inLocalityName = true;
        } else if(localName.equals("Thoroughfare")){
            this.inThoroughfare = true;
        } else if(localName.equals("ThoroughfareName")){
            this.inThoroughfareName = true;
        } else if(localName.equals("ExtendedData")){
            this.inExtendedData = true;
        } else if(localName.equals("LatLonBox")){
            this.inLatLonBox = true;
        } else if(localName.equals("Point")){
            this.inPoint = true;
        } else if(localName.equals("coordinates")){
            this.inCoordinates = true;
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

		if(localName.equals("kml")){
			this.inKml = false;
        } else if(localName.equals("Response")){
            this.inResponse = false;
        } else if(localName.equals("name")){
            this.inName = false;
        } else if(localName.equals("Status")){
            this.inStatus = false;
        } else if(localName.equals("code")){
            this.inCode = false;
        } else if(localName.equals("request")){
            this.inRequest = false;
        } else if(localName.equals("Placemark")){
            this.inPlacemark = false;
        } else if(localName.equals("address")){
            this.inAddress = false;
        } else if(localName.equals("AddressDetails")){
            this.inAddressDetails = false;
        } else if(localName.equals("Country")){
            this.inCountry = false;
        } else if(localName.equals("CountryNameCode")){
            this.country = this.sb.toString();
            this.inCountryNameCode = false;
        } else if(localName.equals("CountryName")){
            this.inCountryName = false;
        } else if(localName.equals("AdministrativeArea")){
            this.inAdministrativeArea = false;
        } else if(localName.equals("AdministrativeAreaName")){
            this.administrative = this.sb.toString();
            this.inAdministrativeAreaName = false;
        } else if(localName.equals("Locality")){
            this.inLocality = false;
        } else if(localName.equals("LocalityName")){
            this.locality = this.sb.toString();
            this.inLocalityName = false;
        } else if(localName.equals("Thoroughfare")){
            this.inThoroughfare = false;
        } else if(localName.equals("ThoroughfareName")){
            this.thorough = this.sb.toString();
            this.inThoroughfareName = false;
        } else if(localName.equals("ExtendedData")){
            this.inExtendedData = false;
        } else if(localName.equals("LatLonBox")){
            this.inLatLonBox = false;
        } else if(localName.equals("Point")){
            this.inPoint = false;
        } else if(localName.equals("coordinates")){
            final String s = this.sb.toString();
            final int spacerPos1 = s.indexOf(',');
            final int spacerPos2 = s.indexOf(',', (spacerPos1 + 1));
            final String lo = s.substring(0, spacerPos1);
            final String la = s.substring(spacerPos1 + 1, spacerPos2);
            final int longitude = (int) (Double.parseDouble(lo) * 1E6);
            final int latitude = (int) (Double.parseDouble(la) * 1E6);
			final GeoPoint gp = new GeoPoint(latitude, longitude);
			this.mTmpGeocodedAddress = new GeocodedAddress(gp);

            this.mTmpGeocodedAddress.setAccuracy(this.quality);
            this.mTmpGeocodedAddress.setStreetNameOfficial(this.thorough);
            this.mTmpGeocodedAddress.setMunicipality(this.locality);
            this.mTmpGeocodedAddress.setCountrySubdivision(this.administrative);
            this.mTmpGeocodedAddress.setNationality(Country.fromAbbreviation(this.country));

			this.mAddresses.add(this.mTmpGeocodedAddress);
            this.inCoordinates = false;
		} else {
			Log.w(DEBUGTAG, "Unexpected tag: '" + name + "'");
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
