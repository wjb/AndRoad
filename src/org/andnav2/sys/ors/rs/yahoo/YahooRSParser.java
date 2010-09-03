package org.andnav2.sys.ors.rs.yahoo;

import java.util.ArrayList;
import java.util.List;

import org.andnav2.adt.other.GraphicsPoint;
import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.sys.ors.adt.Error;
import org.andnav2.sys.ors.adt.rs.Route;
import org.andnav2.sys.ors.adt.rs.RouteInstruction;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.util.constants.Constants;
import org.andnav2.util.constants.TimeConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.Point;
import android.util.FloatMath;
import android.util.Log;

public class YahooRSParser extends DefaultHandler implements TimeConstants, Constants {
	// ====================================
	// Constants
	// ====================================

	protected static final int LATITUDE_OVERMAX = (int)(81 * 1E6);
	protected static final int LONGITUDE_OVERMAX = (int)(181 * 1E6);

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
	private final List<GeoPoint> mVias;

	// ===========================================================
	// Constructors
	// ===========================================================


	public YahooRSParser() {
		this.mVias = null;
	}

	public YahooRSParser(final List<GeoPoint> vias) {
		this.mVias = vias;
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

			finalizeRoute();
		}
		super.endDocument();
	}

	private void finalizeRoute() {
		/* Drag to local variables, for performance reasons */
		final List<GeoPoint> polyLine = this.mRoute.getPolyLine();
		final List<RouteInstruction> routeInstructions = this.mRoute.getRouteInstructions();


		final int[] routeSegmentLengths = new int[polyLine.size() - 1];
		this.mRoute.setRouteSegmentLengths(routeSegmentLengths);
		{
			final int[] routeSegmentLengthsUpToDestination = new int[polyLine.size()];
			this.mRoute.setRouteSegmentLengthsUpToDestination(routeSegmentLengthsUpToDestination);

			final int routeSegmentLengthsCount = routeSegmentLengths.length;
			int routeSegmentLengthsSummed = 0;
			/* Loop through all the MapPoints and determine the lengths of the segments and the length. */
			for(int i = routeSegmentLengthsCount - 1; i > 0; i--){
				routeSegmentLengthsUpToDestination[i-1] = routeSegmentLengthsSummed
				+= routeSegmentLengths[i-1] = polyLine.get(i-1).distanceTo(polyLine.get(i));
			}

			// TODO Check if list-edges get filled properly
		}


		{ /* Fill the TurnPoints-Array. */
			final Point vIn = new Point(), vOut = new Point(); /* Needed to calculate the angles. */
			int curIndexInPolyLine;


			final int polyLineLenght = polyLine.size();
			for(final RouteInstruction ri : this.mRoute.getRouteInstructions()){
				curIndexInPolyLine = ri.getFirstMotherPolylineIndex();
				if(this.mVias != null) {
					for(final GeoPoint v : this.mVias) {
						if(this.mPolyline.get(curIndexInPolyLine).equals(v)) {
							ri.setIsWaypoint(true);
						}
					}
				}

				if(curIndexInPolyLine == 0 || curIndexInPolyLine >= polyLineLenght - 1){ // TODO anners wie bei AndNav1 --> funzt trotzdem?
					ri.setAngle(0);
				}else{

					/* To calculate the angle, we need the line "into" that turnpoint. */
					vIn.x =  polyLine.get(curIndexInPolyLine - 1).getLongitudeE6() - polyLine.get(curIndexInPolyLine).getLongitudeE6();
					vIn.y = polyLine.get(curIndexInPolyLine - 1).getLatitudeE6() - polyLine.get(curIndexInPolyLine).getLatitudeE6();

					/* And the line 'out of' that turnpoint. */
					vOut.x = polyLine.get(curIndexInPolyLine + 1).getLongitudeE6() - polyLine.get(curIndexInPolyLine).getLongitudeE6();
					vOut.y = polyLine.get(curIndexInPolyLine + 1).getLatitudeE6() - polyLine.get(curIndexInPolyLine).getLatitudeE6();

					/* Formula: angle = acos[(x * y) / (|x| * |y|)] */
					if(GraphicsPoint.crossProduct(vIn, vOut) > 0) {
						ri.setAngle(- 180 + (float)Math.toDegrees(Math.acos(GraphicsPoint.dotProduct(vIn, vOut) / (FloatMath.sqrt(vIn.x * vIn.x + vIn.y * vIn.y) * FloatMath.sqrt(vOut.x * vOut.x + vOut.y * vOut.y)))));
					} else {
						ri.setAngle(180 - (float)Math.toDegrees(Math.acos(GraphicsPoint.dotProduct(vIn, vOut) / (FloatMath.sqrt(vIn.x * vIn.x + vIn.y * vIn.y) * FloatMath.sqrt(vOut.x * vOut.x + vOut.y * vOut.y)))));
					}
				}
			}
		}

		{
			/* Calculate the Lat/Lng Spans for each Point up to the next TurnPoint. */
			this.mRoute.setLatitudeMinSpans(new int[polyLine.size()]);
			this.mRoute.setLatitudeMaxSpans(new int[polyLine.size()]);
			this.mRoute.setLongitudeMinSpans(new int[polyLine.size()]);
			this.mRoute.setLongitudeMaxSpans(new int[polyLine.size()]);

			/* Minimum & maximum latitude so we can span it
			 * The latitude is clamped between -80 degrees and +80 degrees inclusive
			 * thus we ensure that we go beyond that number. */
			int minLatitude = LATITUDE_OVERMAX;
			int maxLatitude = -LATITUDE_OVERMAX;

			/* Minimum & maximum longitude so we can span it
			 * The longitude is clamped between -180 degrees and +180 degrees inclusive
			 * thus we ensure that we go beyond that number. */
			int minLongitude  = LONGITUDE_OVERMAX;
			int maxLongitude  = -LONGITUDE_OVERMAX;

			int currentLatitude;
			int currentLongitude;

			GeoPoint current = null;
			int currentNextTurnIndex = routeInstructions.size() - 1;
			/* Starting backwards! */
			for(int j = polyLine.size() - 1; j >= 0 ; j--){
				current = polyLine.get(j);
				// FIXME Wird net richtig zurueckgesetzt... new :( Bug noch am leben?
				/* Check if we are 'on' a turnPoint. */
				final int turnPointIndexInRoute;
				if(currentNextTurnIndex < 0) {
					turnPointIndexInRoute = 0;
				} else {
					turnPointIndexInRoute = routeInstructions.get(currentNextTurnIndex).getFirstMotherPolylineIndex();
				}

				if(j == turnPointIndexInRoute){
					currentNextTurnIndex--;
					/* Reset min/max. */
					minLatitude = LATITUDE_OVERMAX;
					maxLatitude = -LATITUDE_OVERMAX;
					minLongitude = LONGITUDE_OVERMAX;
					maxLongitude = -LONGITUDE_OVERMAX;
				}
				/* Store to local field. */
				currentLatitude = current.getLatitudeE6();
				currentLongitude = current.getLongitudeE6();

				/* Sets the minimum and maximum latitude so we can span and zoom */
				if(minLatitude > currentLatitude) {
					minLatitude = currentLatitude;
				}
				if(maxLatitude < currentLatitude) {
					maxLatitude = currentLatitude;
				}


				/* Sets the minimum and maximum latitude so we can span and zoom */
				if(minLongitude > currentLongitude) {
					minLongitude = currentLongitude;
				}
				if(maxLongitude < currentLongitude) {
					maxLongitude = currentLongitude;
				}

				this.mRoute.getLatitudeMaxSpans()[j] = maxLatitude;
				this.mRoute.getLatitudeMinSpans()[j] = minLatitude;
				this.mRoute.getLongitudeMaxSpans()[j] = maxLongitude;
				this.mRoute.getLongitudeMinSpans()[j] = minLongitude;
			}
		}
	}
}
