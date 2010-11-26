package org.androad.sys.ors.rs.yahoo;

import java.util.ArrayList;
import java.util.List;

import org.andnav.osm.util.BoundingBoxE6;
import org.andnav.osm.util.GeoPoint;

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

public class YahooRSParser extends DefaultHandler implements TimeConstants, Constants {
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

	private GeoPoint tmp;
	private double a = -1;
	private double b = -1;

	private boolean inResultSet = false;
    private boolean inError = false;
    private boolean inErrorMessage = false;
    private boolean inLocale = false;
	private boolean inResult = false;
	private boolean inYahooDrivingDirections = false;
    private boolean inRouteHandle = false;
    private boolean inAddress = false;
    private boolean inType = false;
	private boolean inTotalDistance = false;
	private boolean inTotalTime = false;
	private boolean inTotalTimeWithTraffic = false;
	private boolean inBoundingBox = false;
	private boolean inNorth = false;
	private boolean inEast = false;
	private boolean inSouth = false;
	private boolean inWest = false;
	private boolean inDirections = false;
	private boolean inRouteLeg = false;
	private boolean inNumber = false;
	private boolean inLat = false;
	private boolean inLon = false;
	private boolean inDistance = false;
	private boolean inManType = false;
	private boolean inStreet = false;
	private boolean inSign = false;
	private boolean inDescription = false;
	private boolean inTime = false;
	private boolean inTimeWithTraffic = false;
	private boolean inCopyRight = false;

	private RouteInstruction mTmpRouteInstruction;

	// ===========================================================
	// Constructors
	// ===========================================================


	public YahooRSParser() {
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

		if(localName.equals("ResultSet")){
			this.inResultSet = true;
        } else if(localName.equals("Error")){
			this.inError = true;
        } else if(localName.equals("ErrorMessage")){
            this.inErrorMessage = true;
        } else if(localName.equals("Locale")){
			this.inLocale = true;
		} else if(localName.equals("Result")){
			this.inResult = true;
		} else if(localName.equals("yahoo_driving_directions")){
			this.inYahooDrivingDirections = true;
        } else if(localName.equals("routeHandle")){
			this.inRouteHandle = true;
        } else if(localName.equals("address")){
			this.inAddress = true;
        } else if(localName.equals("type")){
			this.inType = true;
		} else if(localName.equals("total_distance")){
			this.inTotalDistance = true;
		} else if(localName.equals("total_time")){
			this.inTotalTime = true;
		} else if(localName.equals("total_time_with_traffic")){
			this.inTotalTimeWithTraffic = true;
		} else if(localName.equals("boundingbox")){
			this.inBoundingBox = true;
		} else if(localName.equals("North")){
			this.inNorth = true;
		} else if(localName.equals("East")){
			this.inEast = true;
		} else if(localName.equals("South")){
			this.inSouth = true;
		} else if(localName.equals("West")){
			this.inWest = true;
		} else if(localName.equals("directions")){
			this.inDirections = true;
		} else if(localName.equals("route_leg")){
			this.inRouteLeg = true;
            this.mTmpRouteInstruction = new RouteInstruction();
			this.mRoute.getRouteInstructions().add(this.mTmpRouteInstruction);
		} else if(localName.equals("number")){
			this.inNumber = true;
		} else if(localName.equals("lat")){
			this.inLat = true;
		} else if(localName.equals("lon")){
			this.inLon = true;
		} else if(localName.equals("distance")){
			this.inDistance = true;
		} else if(localName.equals("man_type")){
			this.inManType = true;
		} else if(localName.equals("street")){
			this.inStreet = true;
		} else if(localName.equals("sign")){
			this.inSign = true;
		} else if(localName.equals("description")){
			this.inDescription = true;
		} else if(localName.equals("time")){
			this.inTime = true;
		} else if(localName.equals("time_with_traffic")){
			this.inTimeWithTraffic = true;
        } else if(localName.equals("copy_right")){
            this.inCopyRight = true;
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
		if(localName.equals("ResultSet")){
			this.inResultSet = false;
        } else if(localName.equals("Error")){
			this.inError = false;
            this.mErrorNumber = Integer.parseInt(this.sb.toString());
        } else if(localName.equals("ErrorMessage")){
            this.inErrorMessage = false;
            if (this.mErrorNumber > 0) {
                this.mErrors.add(new Error("Err", "Sev", "", this.sb.toString()));
            }
        } else if(localName.equals("Locale")){
			this.inLocale = false;
		} else if(localName.equals("Result")){
			this.inResult = false;
		} else if(localName.equals("yahoo_driving_directions")){
			this.inYahooDrivingDirections = false;
        } else if(localName.equals("routeHandle")){
			this.inRouteHandle = false;
            this.mRoute.setRouteHandleID(Long.parseLong(this.sb.toString()));
        } else if(localName.equals("address")){
			this.inAddress = false;
        } else if(localName.equals("type")){
			this.inType = false;
		} else if(localName.equals("total_distance")){
			this.inTotalDistance = false;
			this.mRoute.setDistanceMeters((int)(1609.344 * Float.parseFloat(this.sb.toString())));
		} else if(localName.equals("total_time")){
			this.inTotalTime = false;
			this.mRoute.setDurationSeconds(Integer.parseInt(this.sb.toString()) * 60);
		} else if(localName.equals("total_time_with_traffic")){
			this.inTotalTimeWithTraffic = false;
		} else if(localName.equals("boundingbox")){
			this.inBoundingBox = false;
		} else if(localName.equals("North")){
            this.a = Double.parseDouble(this.sb.toString());
			this.inNorth = false;
		} else if(localName.equals("East")){
            this.b = Double.parseDouble(this.sb.toString());
			this.inEast = false;
		} else if(localName.equals("South")){
            this.a = Double.parseDouble(this.sb.toString());
			this.inSouth = false;
		} else if(localName.equals("West")){
            this.b = Double.parseDouble(this.sb.toString());
			this.inWest = false;
		} else if(localName.equals("directions")){
			this.inDirections = false;
		} else if(localName.equals("route_leg")){
			this.inRouteLeg = false;
		} else if(localName.equals("number")){
			this.inNumber = false;
		} else if(localName.equals("lat")){
            this.a = Double.parseDouble(this.sb.toString());
			this.inLat = false;
		} else if(localName.equals("lon")){
            this.b = Double.parseDouble(this.sb.toString());
			this.inLon = false;
		} else if(localName.equals("distance")){
			this.inDistance = false;
            this.mTmpRouteInstruction.setLengthMeters((int)(1609.344 * Float.parseFloat(this.sb.toString())));
		} else if(localName.equals("man_type")){
			this.inManType = false;
		} else if(localName.equals("street")){
			this.inStreet = false;
		} else if(localName.equals("sign")){
			this.inSign = false;
		} else if(localName.equals("description")){
			this.inDescription = false;
			if(this.mTmpRouteInstruction.getDescriptionHtml() == null) {
				this.mTmpRouteInstruction.setDescriptionHtml(this.sb.toString());
			} else {
				this.mTmpRouteInstruction.setDescriptionHtml(this.mTmpRouteInstruction.getDescriptionHtml() + this.sb.toString());
			}
		} else if(localName.equals("time")){
			this.inTime = false;
			this.mTmpRouteInstruction.setDurationSeconds(Integer.parseInt(this.sb.toString()) * 60);
		} else if(localName.equals("time_with_traffic")){
			this.inTimeWithTraffic = false;
        } else if(localName.equals("copy_right")){
            this.inCopyRight = false;
		} else {
			Log.w(DEBUGTAG, "Unexpected end-tag: '" + name + "'");
		}

        if (this.a != -1 && this.b != -1) {
			final GeoPoint gp = new GeoPoint((int) (a * 1E6), (int) (b * 1E6));

            if (this.inRouteLeg) {
                this.mPolyline.add(gp);
                this.mTmpRouteInstruction.getPartialPolyLine().add(gp);
				// If this was the first element, we will determine its position in the OverallPolyline
				if(this.mTmpRouteInstruction.getPartialPolyLine().size() == 1) {
					this.mLastFirstMotherPolylineIndex = this.mRoute.findInPolyLine(gp, this.mLastFirstMotherPolylineIndex);
					this.mTmpRouteInstruction.setFirstMotherPolylineIndex(this.mLastFirstMotherPolylineIndex);
				}
            }

			if (this.inBoundingBox) {
                if (this.tmp == null){ // First GeoPoint
                    this.tmp = gp;
				}else{ // Second one
					final int mFirstLatE6 = this.tmp.getLatitudeE6();
					final int mFirstLonE6 = this.tmp.getLongitudeE6();
					this.tmp = gp;
					final int mSecondLatE6 = this.tmp.getLatitudeE6();
					final int mSecondLonE6 = this.tmp.getLongitudeE6();
					this.mRoute.setBoundingBoxE6(new BoundingBoxE6(Math.max(mFirstLatE6, mSecondLatE6),
							Math.max(mFirstLonE6, mSecondLonE6),
							Math.min(mFirstLatE6, mSecondLatE6),
							Math.min(mFirstLonE6, mSecondLonE6)));
				}
            }

            this.a = -1;
            this.b = -1;
        }

		// Reset the stringbuffer
		this.sb.setLength(0);

		super.endElement(uri, localName, name);
	}

	@Override
	public void endDocument() throws SAXException {
		if(this.mErrors == null || this.mErrors.size() == 0){
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
