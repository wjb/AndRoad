package org.androad.sys.ors.rs.yournavigation;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;

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

public class YourNavigationRSParser extends DefaultHandler implements TimeConstants, Constants {
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


	public YourNavigationRSParser() {
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
			this.mTmpRouteInstruction = new RouteInstruction();
			this.mRoute.getRouteInstructions().add(this.mTmpRouteInstruction);
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
            boolean first = true;
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
                if (first) {
                    first = false;
                    this.mTmpRouteInstruction.getPartialPolyLine().add(gp);
                }
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
			this.mRoute.setStart(this.mPolyline.get(0));
			this.mRoute.setDestination(this.mPolyline.get(this.mPolyline.size() - 1));

			this.mRoute.setStartInstruction(this.mRoute.getRouteInstructions().remove(0));
            this.mRoute.setBoundingBoxE6(new BoundingBoxE6(maxLat, maxLon, minLat, minLon));
		}
		super.endDocument();
	}

}
