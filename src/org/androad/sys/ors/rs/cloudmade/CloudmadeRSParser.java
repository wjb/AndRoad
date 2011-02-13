package org.androad.sys.ors.rs.cloudmade;

import java.util.ArrayList;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;

import org.androad.R;
import org.androad.sys.ors.adt.Error;
import org.androad.sys.ors.adt.rs.Route;
import org.androad.sys.ors.adt.rs.RouteInstruction;
import org.androad.sys.ors.exceptions.ORSException;
import org.androad.util.constants.Constants;
import org.androad.util.constants.TimeConstants;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class CloudmadeRSParser extends DefaultHandler implements TimeConstants, Constants {
	// ====================================
	// Constants
	// ====================================

	// ====================================
	// Fields
	// ====================================

	private final ArrayList<Error> mErrors = new ArrayList<Error>();
    private int mErrorNumber = 0;

	private Route mRoute;
	private ArrayList<GeoPoint> mPolyline;

    private double maxLat = 0;
    private double maxLon = 0;
    private double minLat = 0;
    private double minLon = 0;

	private boolean inGpx = false;
    private boolean inExtensions = false;
    private boolean inDistance = false;
    private boolean inTime = false;
	private boolean inStart = false;
	private boolean inEnd = false;
    private boolean inWpt = false;
    private boolean inRte = false;
	private boolean inRtept = false;
	private boolean inDesc = false;
	private boolean inOffset = false;
	private boolean inDistanceText = false;
	private boolean inDirection = false;
	private boolean inAzimuth = false;

	private RouteInstruction mTmpRouteInstruction;

	// ===========================================================
	// Constructors
	// ===========================================================


	public CloudmadeRSParser() {
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArrayList<Error> getErrors(){
		return this.mErrors;
	}

	public Route getRoute() throws ORSException{
		if(this.mErrors != null && this.mErrors.size() > 0) {
			throw new ORSException(this.mErrors);
		}

		return this.mRoute;
	}

	// ====================================
	// Methods from Superclasses
	// ====================================

	@Override
	public void startDocument() throws SAXException {
		this.mRoute = new Route();
		this.mRoute.setRouteInstructions(new ArrayList<RouteInstruction>());
		this.mPolyline = new ArrayList<GeoPoint>();
        this.mRoute.setPolyLine(this.mPolyline);
		super.startDocument();
	}

	@Override
	public void startElement(final String uri, final String localName, final String name, final Attributes attributes) throws SAXException {
		this.sb.setLength(0);

		if(localName.equals("gpx")){
			this.inGpx = true;
        } else if(localName.equals("extensions")){
			this.inExtensions = true;
        } else if(localName.equals("distance")){
            this.inDistance = true;
        } else if(localName.equals("time")){
			this.inTime = true;
		} else if(localName.equals("start")){
			this.inStart = true;
		} else if(localName.equals("end")){
			this.inEnd = true;
        } else if(localName.equals("wpt")){
			this.inWpt = true;
            final double lat = Double.parseDouble(attributes.getValue("", "lat"));
            final double lon = Double.parseDouble(attributes.getValue("", "lon"));
                if (maxLat == 0 || lat > maxLat) {
                    maxLat = lat;
                }
                if (maxLon == 0 || lon > maxLon) {
                    maxLon = lon;
                }
                if (minLat == 0 || lat < minLat) {
                    minLat = lat;
                }
                if (minLon == 0 || lon < minLon) {
                    minLon = lon;
                }
            final GeoPoint geo = new GeoPoint(
                    (int) (lat * 1E6),
					(int) (lon * 1E6));
            this.mPolyline.add(geo);
        } else if(localName.equals("rte")){
			this.inRte = true;
		} else if(localName.equals("rtept")){
			this.inRtept = true;
            final String lat = attributes.getValue("", "lat");
            final String lon = attributes.getValue("", "lon");
            final GeoPoint gp = new GeoPoint(
                    (int) (Double.parseDouble(lat) * 1E6),
					(int) (Double.parseDouble(lon) * 1E6));
            int first = this.mLastFirstMotherPolylineIndex + 1;
            this.mLastFirstMotherPolylineIndex = this.mRoute.findInPolyLine(gp, this.mLastFirstMotherPolylineIndex);
            for (;first < this.mLastFirstMotherPolylineIndex; first++) {
                final GeoPoint geo = this.mPolyline.get(first);
                this.mTmpRouteInstruction.getPartialPolyLine().add(geo);
            }

			this.mTmpRouteInstruction = new RouteInstruction();
			this.mRoute.getRouteInstructions().add(this.mTmpRouteInstruction);
            this.mTmpRouteInstruction.getPartialPolyLine().add(gp);
            this.mTmpRouteInstruction.setFirstMotherPolylineIndex(this.mLastFirstMotherPolylineIndex);
		} else if(localName.equals("desc")){
			this.inDesc = true;
		} else if(localName.equals("offset")){
			this.inOffset = true;
		} else if(localName.equals("distance-text")){
			this.inDistanceText = true;
		} else if(localName.equals("direction")){
			this.inDirection = true;
		} else if(localName.equals("azimuth")){
			this.inAzimuth = true;
		} else {
			Log.w(DEBUGTAG, "Unexpected tag: '" + name + "'");
		}

		super.startElement(uri, localName, name, attributes);
	}

	protected StringBuilder sb = new StringBuilder();
	private int mLastFirstMotherPolylineIndex = 0;

	@Override
	public void characters(final char[] chars, final int start, final int length) throws SAXException {
		this.sb.append(chars, start, length);
		super.characters(chars, start, length);
	}

	@Override
	public void endElement(final String uri, final String localName, final String name) throws SAXException {
		if(localName.equals("gpx")){
			this.inGpx = false;
        } else if(localName.equals("extensions")){
			this.inExtensions = false;
        } else if(localName.equals("distance")){
            this.inDistance = false;
            if (inExtensions) {
                if (!inRte) {
                    this.mRoute.setDistanceMeters((int)Float.parseFloat(this.sb.toString()));
                } else {
                    this.mTmpRouteInstruction.setLengthMeters((int)Float.parseFloat(this.sb.toString()));
                }
            }
        } else if(localName.equals("time")){
			this.inTime = false;
            if (inExtensions) {
                if (!inRte) {
                    this.mRoute.setDurationSeconds((int)Float.parseFloat(this.sb.toString()));
                } else {
                    this.mTmpRouteInstruction.setDurationSeconds((int)Float.parseFloat(this.sb.toString()));
                }
            }
		} else if(localName.equals("start")){
			this.inStart = false;
		} else if(localName.equals("end")){
			this.inEnd = false;
        } else if(localName.equals("wpt")){
			this.inWpt = false;
        } else if(localName.equals("rte")){
			this.inRte = false;
        } else if(localName.equals("rtept")){
			this.inRtept = false;
		} else if(localName.equals("desc")){
			this.inDesc = false;
            this.mTmpRouteInstruction.setDescriptionHtml(this.sb.toString());
		} else if(localName.equals("offset")){
			this.inOffset = false;
		} else if(localName.equals("distance-text")){
			this.inDistanceText = false;
		} else if(localName.equals("direction")){
			this.inDirection = false;
		} else if(localName.equals("azimuth")){
			this.inAzimuth = false;
		} else {
			Log.w(DEBUGTAG, "Unexpected end-tag: '" + name + "'");
		}

		// Reset the stringbuffer
		this.sb.setLength(0);

		super.endElement(uri, localName, name);
	}

	@Override
	public void endDocument() throws SAXException {
		if(this.mErrors == null || this.mErrors.size() == 0){
            int first = this.mLastFirstMotherPolylineIndex + 1;
            for (;first < this.mPolyline.size(); first++) {
                final GeoPoint geo = this.mPolyline.get(first);
                this.mTmpRouteInstruction.getPartialPolyLine().add(geo);
            }

            this.mRoute.setBoundingBoxE6(new BoundingBoxE6(maxLat, maxLon, minLat, minLon));

			this.mRoute.setStart(this.mPolyline.get(0));
			this.mRoute.setDestination(this.mPolyline.get(this.mPolyline.size() - 1));

			this.mRoute.setStartInstruction(this.mRoute.getRouteInstructions().remove(0));

			// Modify the arrival-instruction that is just shows
			final RouteInstruction last = this.mRoute.getRouteInstructions().get(this.mRoute.getRouteInstructions().size() - 1);
			last.setFirstMotherPolylineIndex(this.mPolyline.size() - 1);
		}
		super.endDocument();
	}

}
