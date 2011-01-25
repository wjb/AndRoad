package org.androad.sys.ors.rs.yournavigation;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;

import org.androad.R;
import org.androad.adt.other.GraphicsPoint;
import org.androad.sys.ors.adt.Error;
import org.androad.sys.ors.adt.rs.Route;
import org.androad.sys.ors.adt.rs.RouteInstruction;
import org.androad.sys.ors.exceptions.ORSException;
import org.androad.util.constants.Constants;
import org.androad.util.constants.TimeConstants;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.graphics.Point;
import android.util.FloatMath;
import android.util.Log;

public class YourNavigationRSParser extends DefaultHandler implements TimeConstants, Constants {
	// ====================================
	// Constants
	// ====================================

	// ====================================
	// Fields
	// ====================================

	private final ArrayList<Error> mErrors = new ArrayList<Error>();
    private int mErrorNumber = 0;

    private Context context;

	private Route mRoute;
	private ArrayList<GeoPoint> mPolyline;

    private double maxLat = 0;
    private double maxLon = 0;
    private double minLat = 0;
    private double minLon = 0;

	private boolean inKml = false;
    private boolean inDocument = false;
    private boolean inName = false;
    private boolean inOpen = false;
	private boolean inDistance = false;
	private boolean inDescription = false;
    private boolean inFolder = false;
    private boolean inVisibility = false;
	private boolean inPlacemark = false;
	private boolean inLineString = false;
	private boolean inTessellate = false;
	private boolean inCoordinates = false;

	private RouteInstruction mTmpRouteInstruction;

	// ===========================================================
	// Constructors
	// ===========================================================


	public YourNavigationRSParser(Context ctx) {
        this.context = ctx;
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

		if(localName.equals("kml")){
			this.inKml = true;
        } else if(localName.equals("Document")){
			this.inDocument = true;
        } else if(localName.equals("name")){
            this.inName = true;
        } else if(localName.equals("open")){
			this.inOpen = true;
		} else if(localName.equals("distance")){
			this.inDistance = true;
		} else if(localName.equals("description")){
			this.inDescription = true;
        } else if(localName.equals("Folder")){
			this.inFolder = true;
        } else if(localName.equals("visibility")){
			this.inVisibility = true;
		} else if(localName.equals("Placemark")){
			this.inPlacemark = true;
		} else if(localName.equals("LineString")){
			this.inLineString = true;
		} else if(localName.equals("tessellate")){
			this.inTessellate = true;
		} else if(localName.equals("coordinates")){
			this.inCoordinates = true;
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
		if(localName.equals("kml")){
			this.inKml = false;
        } else if(localName.equals("Document")){
			this.inDocument = false;
        } else if(localName.equals("name")){
            this.inName = false;
        } else if(localName.equals("open")){
			this.inOpen = false;
		} else if(localName.equals("distance")){
			this.inDistance = false;
			this.mRoute.setDistanceMeters((int)(1000 * Float.parseFloat(this.sb.toString())));
		} else if(localName.equals("description")){
			this.inDescription = false;
        } else if(localName.equals("Folder")){
			this.inFolder = false;
        } else if(localName.equals("visibility")){
			this.inVisibility = false;
        } else if(localName.equals("Placemark")){
			this.inPlacemark = false;
		} else if(localName.equals("LineString")){
			this.inLineString = false;
		} else if(localName.equals("tessellate")){
			this.inTessellate = false;
		} else if(localName.equals("coordinates")){
			this.inCoordinates = false;
            final String coords = this.sb.toString();
            final StringTokenizer st1 = new StringTokenizer(coords, "\n");

            while (st1.hasMoreTokens()){
                final String point = st1.nextToken();
                final StringTokenizer st2 = new StringTokenizer(point, ",");
                if (!st2.hasMoreTokens())
                    continue;

                final double b;
                final double a;

                try {
                    b = Double.parseDouble(st2.nextToken());
                    a = Double.parseDouble(st2.nextToken());
                } catch (Exception e) {
                    continue;
                }
                if (maxLat == 0 || a > maxLat) {
                    maxLat = a;
                }
                if (maxLon == 0 || b > maxLon) {
                    maxLon = b;
                }
                if (minLat == 0 || a < minLat) {
                    minLat = a;
                }
                if (minLon == 0 || b < minLon) {
                    minLon = b;
                }
                final GeoPoint gp = new GeoPoint((int) (a * 1E6), (int) (b * 1E6));
                this.mPolyline.add(gp);
            }

			final Point vIn = new Point(), vOut = new Point(); /* Needed to calculate the angles. */
			final int polyLineLenght = this.mPolyline.size();
            String mRouteInstructionDescr = null;
			int curIndexInPolyLine = 0;
            int turnAngle = 0;
            for (final GeoPoint gp : this.mPolyline) {

                if (curIndexInPolyLine == 0) {
                    this.mTmpRouteInstruction = new RouteInstruction();
                    this.mRoute.getRouteInstructions().add(this.mTmpRouteInstruction);
                    this.mTmpRouteInstruction.setDescriptionHtml(context.getString(R.string.begin_at));
                } else if (curIndexInPolyLine >= polyLineLenght - 1) {
                    this.mTmpRouteInstruction.setDescriptionHtml(context.getString(R.string.arrived_at));
                } else {
                    final GeoPoint lastgp = this.mPolyline.get(curIndexInPolyLine - 1);
                    final GeoPoint nextgp = this.mPolyline.get(curIndexInPolyLine + 1);

					/* To calculate the angle, we need the line "into" that turnpoint. */
					vIn.x = lastgp.getLongitudeE6() - gp.getLongitudeE6();
					vIn.y = lastgp.getLatitudeE6() - gp.getLatitudeE6();

					/* And the line 'out of' that turnpoint. */
					vOut.x = nextgp.getLongitudeE6() - gp.getLongitudeE6();
					vOut.y = nextgp.getLatitudeE6() - gp.getLatitudeE6();

					/* Formula: angle = acos[(x * y) / (|x| * |y|)] */
					if(GraphicsPoint.crossProduct(vIn, vOut) > 0) {
						turnAngle = - 180 + (int)Math.toDegrees(Math.acos(GraphicsPoint.dotProduct(vIn, vOut) / (FloatMath.sqrt(vIn.x * vIn.x + vIn.y * vIn.y) * FloatMath.sqrt(vOut.x * vOut.x + vOut.y * vOut.y))));
					} else {
						turnAngle = 180 - (int)Math.toDegrees(Math.acos(GraphicsPoint.dotProduct(vIn, vOut) / (FloatMath.sqrt(vIn.x * vIn.x + vIn.y * vIn.y) * FloatMath.sqrt(vOut.x * vOut.x + vOut.y * vOut.y))));
					}

                    if(turnAngle > 60) {
                        mRouteInstructionDescr = context.getString(R.string.turn_left_90);
                    } else if(turnAngle > 35) {
                        mRouteInstructionDescr = context.getString(R.string.turn_left_45);
                    } else if(turnAngle > 15) {
                        mRouteInstructionDescr = context.getString(R.string.turn_left_25);
                    } else if(turnAngle < -60) {
                        mRouteInstructionDescr = context.getString(R.string.turn_right_90);
                    } else if(turnAngle < -35) {
                        mRouteInstructionDescr = context.getString(R.string.turn_right_45);
                    } else if(turnAngle < -15) {
                        mRouteInstructionDescr = context.getString(R.string.turn_right_25);
                    } else {
                        mRouteInstructionDescr = null;
                    }
                    if (mRouteInstructionDescr != null) {
                        final int distance = gp.distanceTo(this.mTmpRouteInstruction.getPartialPolyLine().get(0));
                        this.mTmpRouteInstruction.setLengthMeters(distance);
                        this.mTmpRouteInstruction.setDescriptionHtml(mRouteInstructionDescr);

                        this.mTmpRouteInstruction = new RouteInstruction();
                        this.mRoute.getRouteInstructions().add(this.mTmpRouteInstruction);
                    }
                }

                this.mTmpRouteInstruction.getPartialPolyLine().add(gp);
                // If this was the first element, we will determine its position in the OverallPolyline
                if(this.mTmpRouteInstruction.getPartialPolyLine().size() == 1) {
                    this.mLastFirstMotherPolylineIndex = this.mRoute.findInPolyLine(gp, this.mLastFirstMotherPolylineIndex);
                    this.mTmpRouteInstruction.setFirstMotherPolylineIndex(this.mLastFirstMotherPolylineIndex);
                }

                curIndexInPolyLine++;
            }

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
            final GeoPoint gp = this.mTmpRouteInstruction.getPartialPolyLine().get(this.mTmpRouteInstruction.getPartialPolyLine().size() - 1);
            int distance = gp.distanceTo(this.mTmpRouteInstruction.getPartialPolyLine().get(0));
            this.mTmpRouteInstruction.setLengthMeters(distance);

            distance = gp.distanceTo(this.mPolyline.get(0));
			this.mRoute.setDistanceMeters(distance);

			this.mRoute.setStart(this.mPolyline.get(0));
			this.mRoute.setDestination(this.mPolyline.get(this.mPolyline.size() - 1));

			this.mRoute.setStartInstruction(this.mRoute.getRouteInstructions().remove(0));
            this.mRoute.setBoundingBoxE6(new BoundingBoxE6(maxLat, maxLon, minLat, minLon));
		}
		super.endDocument();
	}

}
