package org.androad.sys.ors.ds.cloudmade;

import java.util.ArrayList;
import java.util.Stack;

import org.osmdroid.util.GeoPoint;

import org.androad.sys.ors.adt.Error;
import org.androad.sys.ors.adt.ds.ORSPOI;
import org.androad.sys.ors.adt.ds.POIType;
import org.androad.sys.ors.exceptions.ORSException;
import org.androad.util.constants.Constants;
import org.androad.util.constants.TimeConstants;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class CloudmadeDSParser extends DefaultHandler implements TimeConstants, Constants {
	// ====================================
	// Constants
	// ====================================

	// ====================================
	// Fields
	// ====================================

	private final ArrayList<Error> mErrors = new ArrayList<Error>();

	private ArrayList<ORSPOI> mPOIs = new ArrayList<ORSPOI>();

	private boolean inPlist = false;
	private boolean inArray = false;
	private boolean inDict = false;
	private boolean inKey = false;
	private boolean inString = false;

    private Stack<String> mKeys = new Stack<String>();

    private GeoPoint aGeoPoint;
    private POIType aPOIType;
    private ORSPOI mCurPOI;

    private double lat = 0.0;
    private double lon = 0.0;

	// ===========================================================
	// Constructors
	// ===========================================================


	public CloudmadeDSParser(final GeoPoint aGeoPoint, final POIType aPOIType) {
        this.aGeoPoint = aGeoPoint;
        this.aPOIType = aPOIType;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArrayList<Error> getErrors(){
		return this.mErrors;
	}

    public ArrayList<ORSPOI> getDSResponse() {
        return mPOIs;
    }

	// ====================================
	// Methods from Superclasses
	// ====================================

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void startElement(final String uri, final String localName, final String name, final Attributes attributes) throws SAXException {
		this.sb.setLength(0);

		if(localName.equals("plist")){
			this.inPlist = true;
        } else if(localName.equals("array")){
			this.inArray = true;
        } else if(localName.equals("dict")){
            this.inDict = true;
        } else if(localName.equals("key")){
			this.inKey = true;
		} else if(localName.equals("string")){
			this.inString = true;
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
		if(localName.equals("plist")){
			this.inPlist = false;
        } else if(localName.equals("array")){
			this.inArray = false;
        } else if(localName.equals("dict")){
            this.inDict = false;
            this.mKeys.pop();
        } else if(localName.equals("key")){
			this.inKey = false;
            final String key = this.sb.toString();
            this.mKeys.push(key);
            if ("centroid".equals(key)) {
                this.mCurPOI = new ORSPOI();
                this.mCurPOI.setName("No Name");
                this.mCurPOI.setPOIType(aPOIType);
                this.mPOIs.add(mCurPOI);
            }
		} else if(localName.equals("string")){
			this.inString = false;
            final String key = this.mKeys.peek();
            final String string = this.sb.toString();
            if ("lat".equals(key)) {
                lat = Double.parseDouble(string);
            }
            if ("lon".equals(key)) {
                lon = Double.parseDouble(string);
            }
            if ("amenity".equals(key)) {
                this.mCurPOI.setPOIType(POIType.fromRawName(string));
            }
            if ("name".equals(key)) {
                this.mCurPOI.setName(string);
            }
		} else {
			Log.w(DEBUGTAG, "Unexpected end-tag: '" + name + "'");
		}

		// Reset the stringbuffer
		this.sb.setLength(0);

        if (lat != 0 && lon != 0) {
            final GeoPoint geo = new GeoPoint(lat, lon);
            this.mCurPOI.setGeoPoint(geo);
            this.mCurPOI.setDistance(aGeoPoint.distanceTo(geo));
            lat = 0;
            lon = 0;
        }

		super.endElement(uri, localName, name);
	}

	@Override
	public void endDocument() throws SAXException {
		if(this.mErrors == null || this.mErrors.size() == 0){
		}
		super.endDocument();
	}

	// ====================================
	// Helper-Methods
	// ====================================
}
