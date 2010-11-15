package org.andnav2.sys.ors.rs.google;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.andnav.osm.util.BoundingBoxE6;
import org.andnav.osm.util.GeoPoint;

import org.andnav2.sys.ors.adt.Error;
import org.andnav2.sys.ors.adt.rs.Route;
import org.andnav2.sys.ors.adt.rs.RouteInstruction;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.andnav2.util.constants.Constants;
import org.andnav2.util.constants.TimeConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class GoogleRSParser extends DefaultHandler implements TimeConstants, Constants {
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

	private boolean Document = false;
	private boolean Placemark = false;
	private boolean name = false;
	private boolean description = false;
	private boolean Point = false;
    private boolean LineString = false;
	private boolean coordinates = false;

	private RouteInstruction mTmpRouteInstruction;

	// ===========================================================
	// Constructors
	// ===========================================================


	public GoogleRSParser() {
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

		if(localName.equals("Document")){
			this.Document = true;
        } else if(localName.equals("Placemark")){
			this.Placemark = true; 
            this.mTmpRouteInstruction = new RouteInstruction();
        } else if(localName.equals("name")){
            this.name = true;
        } else if(localName.equals("description")){
			this.description = true;
		} else if(localName.equals("Point")){
			this.Point = true;
		} else if(localName.equals("LineString")){
			this.LineString = true;
		} else if(localName.equals("coordinates")){
			this.coordinates = true;
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
		if(localName.equals("Document")){
			this.Document = false;
        } else if(localName.equals("Placemark")){
			this.Placemark = false;
        } else if(localName.equals("name")){
			this.name = false;
            if (this.Placemark) {
                if(this.mTmpRouteInstruction.getDescriptionHtml() == null) {
                    this.mTmpRouteInstruction.setDescriptionHtml(this.sb.toString());
                } else {
                    this.mTmpRouteInstruction.setDescriptionHtml(this.mTmpRouteInstruction.getDescriptionHtml() + this.sb.toString());
                }
            }
		} else if(localName.equals("description")){
			this.description = false;
		} else if(localName.equals("Point")){
			this.Point = false;
		} else if(localName.equals("LineString")){
			this.LineString = false;
        } else if(localName.equals("coordinates")){
			this.coordinates = false;
            final String coords = this.sb.toString();
            if (Point) {
                this.mRoute.getRouteInstructions().add(this.mTmpRouteInstruction);

                final StringTokenizer st = new StringTokenizer(coords, ",");
                final double a = Double.parseDouble(st.nextToken());
                final double b = Double.parseDouble(st.nextToken());

                final GeoPoint gp = new GeoPoint((int) (a * 1E6), (int) (b * 1E6));
                this.mTmpRouteInstruction.getPartialPolyLine().add(gp);
            }
            if (LineString) {
                final StringTokenizer st1 = new StringTokenizer(coords, " ");
                GeoPoint lastgp = null;
                while(st1.hasMoreTokens()){
                    final StringTokenizer st2 = new StringTokenizer(st1.nextToken(), ",");
                    if (!st2.hasMoreTokens())
                        continue;
                    final double a = Double.parseDouble(st2.nextToken());
                    final double b = Double.parseDouble(st2.nextToken());
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
                    if (lastgp == null ||
                        !lastgp.equals(gp)) {
                        this.mPolyline.add(gp);
                    }
                    lastgp = gp;
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
            int gpindex = 0;
            for (int i = 0; i < this.mRoute.getRouteInstructions().size(); i++) {
                final RouteInstruction ri = this.mRoute.getRouteInstructions().get(i);

                if (ri.getPartialPolyLine().size() == 0) {
                    continue;
                }

                RouteInstruction nextri = null;
                if (i + 1 < this.mRoute.getRouteInstructions().size()) {
                    nextri = this.mRoute.getRouteInstructions().get(i + 1);
                }
                final GeoPoint rigp = ri.getPartialPolyLine().get(0);
                GeoPoint nextrigp = null;
                if (nextri != null && nextri.getPartialPolyLine().size() > 0) {
                    nextrigp = nextri.getPartialPolyLine().get(0);
                }

                ri.setFirstMotherPolylineIndex(gpindex);

                for (; gpindex < this.mPolyline.size(); gpindex++) {
                    final GeoPoint gp = this.mPolyline.get(gpindex);
                    if (nextrigp != null && nextrigp.equals(gp)) {
                        break;
                    }
                    ri.getPartialPolyLine().add(gp);
                }
                ri.setLengthMeters(ri.getPartialPolyLine().get(0).distanceTo(
                                                                             ri.getPartialPolyLine().get(ri.getPartialPolyLine().size() - 1)));
            }

            this.mRoute.setBoundingBoxE6(new BoundingBoxE6(maxLat * 1E6, maxLon * 1E6,
                                                           minLat * 1E6, minLon * 1E6));

            if (this.mPolyline.size() > 0) {
                this.mRoute.setStart(this.mPolyline.get(0));
                this.mRoute.setDestination(this.mPolyline.get(this.mPolyline.size() - 1));

                this.mRoute.setDistanceMeters(this.mRoute.getDestination().distanceTo(this.mRoute.getStart()));

                this.mRoute.setStartInstruction(this.mRoute.getRouteInstructions().remove(0));

                // Modify the arrival-instruction that is just shows
                final RouteInstruction last = this.mRoute.getRouteInstructions().get(this.mRoute.getRouteInstructions().size() - 1);
                last.setFirstMotherPolylineIndex(this.mPolyline.size() - 1);
            }
		}
		super.endDocument();
	}

}
