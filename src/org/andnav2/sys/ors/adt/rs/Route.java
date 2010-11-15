package org.andnav2.sys.ors.adt.rs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.andnav.osm.util.BoundingBoxE6;
import org.andnav.osm.util.GeoPoint;

import org.andnav2.adt.other.GraphicsPoint;
import org.andnav2.util.constants.Constants;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.FloatMath;

/**
 * @since 2008-04-06 19:05:37
 * @author Nicolas 'plusminus' Gramlich
 * License:
 * @see Creative Commons Attribution-Noncommercial-Share Alike 2.0 Germany License .
 * Permissions beyond the scope of this license may be requested at plusminus {at} anddev {dot} org
 */
public class Route implements Parcelable, Serializable {

	// ===========================================================
	// Constants
	// ===========================================================

    static final long serialVersionUID = 4L;
	protected static final int LATITUDE_OVERMAX = (int)(81 * 1E6);
	protected static final int LONGITUDE_OVERMAX = (int)(181 * 1E6);

	// ===========================================================
	// Fields
	// ===========================================================

	protected int mDurationSeconds;
	protected String mDurationTextual;
	protected int mDistanceMeters;
	protected String mDistanceTextual;

	protected GeoPoint mStart;
	protected List<GeoPoint> mVias;
	protected GeoPoint mDestination;

	protected List<GeoPoint> mPolyLine;
	protected int[] mRouteSegmentLengths;
	protected int[] mRouteSegmentLengthsUpToDestination;

	protected int[] mLatitudeMinSpans;
	protected int[] mLatitudeMaxSpans;
	protected int[] mLongitudeMinSpans;
	protected int[] mLongitudeMaxSpans;

	protected RouteInstruction mStartInstruction;
	protected List<RouteInstruction> mRouteInstructions;
	protected RouteInstruction mDestinationInstruction;
	protected BoundingBoxE6 mBoundingBoxE6;

	protected int mHashCode;
	protected long mRouteHandleID;

	// ===========================================================
	// Constructors
	// ===========================================================

	public Route(){
		this.mHashCode = new Long(System.currentTimeMillis()).intValue();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public boolean hasRouteHandleID() {
		return this.mRouteHandleID != Constants.NOT_SET;
	}

	public long getRouteHandleID() {
		return this.mRouteHandleID;
	}

	public List<GeoPoint> getVias() {
		if(this.mVias == null) {
			this.mVias = new ArrayList<GeoPoint>();
		}
		return this.mVias;
	}

	/**
	 * @return the mDurationSeconds
	 */
	public int getDurationSeconds() {
		return this.mDurationSeconds;
	}

	/**
	 * @return the mDurationTextual
	 */
	public String getDurationHtmlFormatted() {
		return this.mDurationTextual;
	}

	/**
	 * @return the mDistanceMeters
	 */
	public int getDistanceMeters() {
		return this.mDistanceMeters;
	}

	/**
	 * @return the mDistanceTextual
	 */
	public String getDistanceHtmlFormatted() {
		return this.mDistanceTextual;
	}

	/**
	 * @return the mLatitudeMaxSpans
	 */
	public int[] getLatitudeMaxSpans() {
		return this.mLatitudeMaxSpans;
	}

	public int getLatitudeMaxSpan(final int index) {
		return this.mLatitudeMaxSpans[index];
	}

	/**
	 * @return the mLatitudeMinSpans
	 */
	public int[] getLatitudeMinSpans() {
		return this.mLatitudeMinSpans;
	}

	public int getLatitudeMinSpan(final int index) {
		return this.mLatitudeMinSpans[index];
	}

	/**
	 * @return the mLongitudeMaxSpans
	 */
	public int[] getLongitudeMaxSpans() {
		return this.mLongitudeMaxSpans;
	}

	public int getLongitudeMaxSpan(final int index) {
		return this.mLongitudeMaxSpans[index];
	}

	/**
	 * @return the mLongitudeMinSpans
	 */
	public int[] getLongitudeMinSpans() {
		return this.mLongitudeMinSpans;
	}

	public int getLongitudeMinSpan(final int index) {
		return this.mLongitudeMinSpans[index];
	}


	/**
	 * @return the mStart
	 */
	public GeoPoint getStart() {
		return this.mStart;
	}

	/**
	 * @return the mDestination
	 */
	public GeoPoint getDestination() {
		return this.mDestination;
	}

	/**
	 * @return the mPolyLine
	 */
	public List<GeoPoint> getPolyLine() {
		return this.mPolyLine;
	}

	/**
	 * @return the mRouteSegmentLengths
	 */
	public int[] getRouteSegmentLengths() {
		return this.mRouteSegmentLengths;
	}

	/**
	 * @return the mRouteSegmentLengthsUpToDestination
	 */
	public int[] getRouteSegmentLengthsUpToDestination() {
		return this.mRouteSegmentLengthsUpToDestination;
	}

	public RouteInstruction getStartRouteInstruction() {
		return this.mStartInstruction;
	}

	public RouteInstruction getDestinationRouteInstruction() {
		return this.mDestinationInstruction;
	}

	/**
	 * @return the mTurnPointsRaw
	 */
	public List<RouteInstruction> getRouteInstructions() {
		return this.mRouteInstructions;
	}

	public BoundingBoxE6 getBoundingBoxE6(){
		return this.mBoundingBoxE6;
	}

	public int getEstimatedRestSeconds(final int indexInRoute, final int distanceToNextTurnPoint) {
		int out = 0;
		for(int i = this.mRouteInstructions.size() - 1; i >= 0; i--){
			final RouteInstruction ri = this.mRouteInstructions.get(i);
			if(!ri.contains(indexInRoute)){
				out += ri.mDurationSeconds;
			}else{
				out += ri.getEstimatedRestSeconds(indexInRoute, distanceToNextTurnPoint);
				break;
			}
		}
		return out;
	}

	public int findInPolyLine(final GeoPoint findMe, final int pStartingFrom) {
		final int polyLineLength = this.mPolyLine.size();
		for (int i = pStartingFrom; i < polyLineLength; i++){
			if(this.mPolyLine.get(i).equals(findMe)) {
				return i;
			}
		}
		throw new IllegalArgumentException("Could not find GeoPoint: " + findMe.toDoubleString() + " (started search at index: " + pStartingFrom + ")");
	}


	public void setRouteHandleID(final long routeHandleID) {
		this.mRouteHandleID = routeHandleID;
	}

	public void setDurationSeconds(final int durationSeconds) {
		this.mDurationSeconds = durationSeconds;
	}


	public void setDurationTextual(final String durationTextual) {
		this.mDurationTextual = durationTextual;
	}


	public void setDistanceMeters(final int distanceMeters) {
		this.mDistanceMeters = distanceMeters;
	}


	public void setDistanceTextual(final String distanceTextual) {
		this.mDistanceTextual = distanceTextual;
	}


	public void setStart(final GeoPoint start) {
		this.mStart = start;
	}


	public void setVias(final ArrayList<GeoPoint> vias) {
		this.mVias = vias;
	}


	public void setDestination(final GeoPoint destination) {
		this.mDestination = destination;
	}


	public void setPolyLine(final ArrayList<GeoPoint> polyLine) {
		this.mPolyLine = polyLine;
	}


	public void setRouteSegmentLengths(final int[] routeSegmentLengths) {
		this.mRouteSegmentLengths = routeSegmentLengths;
	}


	public void setRouteSegmentLengthsUpToDestination(final int[] routeSegmentLengthsUpToDestination) {
		this.mRouteSegmentLengthsUpToDestination = routeSegmentLengthsUpToDestination;
	}


	public void setLatitudeMinSpans(final int[] latitudeMinSpans) {
		this.mLatitudeMinSpans = latitudeMinSpans;
	}


	public void setLatitudeMaxSpans(final int[] latitudeMaxSpans) {
		this.mLatitudeMaxSpans = latitudeMaxSpans;
	}


	public void setLongitudeMinSpans(final int[] longitudeMinSpans) {
		this.mLongitudeMinSpans = longitudeMinSpans;
	}


	public void setLongitudeMaxSpans(final int[] longitudeMaxSpans) {
		this.mLongitudeMaxSpans = longitudeMaxSpans;
	}


	public void setStartInstruction(final RouteInstruction startInstruction) {
		this.mStartInstruction = startInstruction;
	}


	public void setRouteInstructions(final ArrayList<RouteInstruction> routeInstructions) {
		this.mRouteInstructions = routeInstructions;
	}


	public void setDestinationInstruction(final RouteInstruction destinationInstruction) {
		this.mDestinationInstruction = destinationInstruction;
	}


	public void setBoundingBoxE6(final BoundingBoxE6 boundingBoxE6) {
		this.mBoundingBoxE6 = boundingBoxE6;
	}

    public int insert(final Route partialroute, final GeoPoint start, final GeoPoint end) {
        int index = this.findInPolyLine(start, 0);
        int add = 0;
        int k = 1;

        for (int i = 1; i < partialroute.getPolyLine().size(); i++) {
            GeoPoint point = partialroute.getPolyLine().get(i);

            if (point.equals(start) || point.equals(end)) {
                continue;
            }

            try {
                int j = this.findInPolyLine(point, index);
                index = j;
                k = 1;
            } catch (IllegalArgumentException ex) {
                mPolyLine.add(index + k, point);
                add++;
                k++;
            }
        }

        return add;
    }

	public void finalizeRoute(List<GeoPoint> pVias) {
		/* Drag to local variables, for performance reasons */
		final List<GeoPoint> polyLine = getPolyLine();
		final List<RouteInstruction> routeInstructions = getRouteInstructions();


		final int[] routeSegmentLengths = new int[polyLine.size() - 1];
		setRouteSegmentLengths(routeSegmentLengths);
		{
			final int[] routeSegmentLengthsUpToDestination = new int[polyLine.size()];
			setRouteSegmentLengthsUpToDestination(routeSegmentLengthsUpToDestination);

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
			for(final RouteInstruction ri : getRouteInstructions()){
				curIndexInPolyLine = ri.getFirstMotherPolylineIndex();
				if(pVias != null) {
					for(final GeoPoint v : pVias) {
						if(this.mPolyLine.get(curIndexInPolyLine).equals(v)) {
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
			setLatitudeMinSpans(new int[polyLine.size()]);
			setLatitudeMaxSpans(new int[polyLine.size()]);
			setLongitudeMinSpans(new int[polyLine.size()]);
			setLongitudeMaxSpans(new int[polyLine.size()]);

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

				getLatitudeMaxSpans()[j] = maxLatitude;
				getLatitudeMinSpans()[j] = minLatitude;
				getLongitudeMaxSpans()[j] = maxLongitude;
				getLongitudeMinSpans()[j] = minLongitude;
			}
		}
	}

	// ===========================================================
	// Methods from SuperClasses
	// ===========================================================

	@Override
	public int hashCode() {
		return this.mHashCode;
	}

	@Override
	public boolean equals(final Object other){
		return (other != null) && (other instanceof Route) && (other.hashCode() == hashCode());
	}

	// ===========================================================
	// Parcelable
	// ===========================================================

	public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
		public Route createFromParcel(final Parcel in) {
			return readFromParcel(in);
		}

		public Route[] newArray(final int size) {
			return new Route[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel out, final int flags) {
		out.writeInt(this.mDurationSeconds);
		out.writeString(this.mDurationTextual);
		out.writeInt(this.mDistanceMeters);
		out.writeString(this.mDistanceTextual);

		out.writeParcelable(this.mStart, 0);
		out.writeTypedList(this.mVias);
		out.writeParcelable(this.mDestination, 0);

		out.writeTypedList(this.mPolyLine);
		out.writeIntArray(this.mRouteSegmentLengths);
		out.writeIntArray(this.mRouteSegmentLengthsUpToDestination);

		out.writeIntArray(this.mLatitudeMinSpans);
		out.writeIntArray(this.mLatitudeMaxSpans);
		out.writeIntArray(this.mLongitudeMinSpans);
		out.writeIntArray(this.mLongitudeMaxSpans);

		out.writeParcelable(this.mStartInstruction, 0);
		out.writeTypedList(this.mRouteInstructions);
		out.writeParcelable(this.mDestinationInstruction, 0);

		out.writeParcelable(this.mBoundingBoxE6, 0);

		out.writeInt(this.mHashCode);

		out.writeLong(this.mRouteHandleID);
	}

	private static Route readFromParcel(final Parcel in){
		final Route out = new Route();

		final ClassLoader geoPointClassLoader = GeoPoint.class.getClassLoader();

		out.mDurationSeconds = in.readInt();
		out.mDurationTextual = in.readString();
		out.mDistanceMeters = in.readInt();
		out.mDistanceTextual = in.readString();

		out.mStart = in.readParcelable(geoPointClassLoader);
		out.mVias = new ArrayList<GeoPoint>(); in.readTypedList(out.mVias, GeoPoint.CREATOR);
		out.mDestination = in.readParcelable(geoPointClassLoader);

		out.mPolyLine = new ArrayList<GeoPoint>(); in.readTypedList(out.mPolyLine, GeoPoint.CREATOR);

		out.mRouteSegmentLengths = in.createIntArray();
		out.mRouteSegmentLengthsUpToDestination = in.createIntArray();

		out.mLatitudeMinSpans = in.createIntArray();
		out.mLatitudeMaxSpans = in.createIntArray();
		out.mLongitudeMinSpans = in.createIntArray();
		out.mLongitudeMaxSpans = in.createIntArray();

		out.mStartInstruction = in.readParcelable(null);
		out.mRouteInstructions = new ArrayList<RouteInstruction>(); in.readTypedList(out.mRouteInstructions, RouteInstruction.CREATOR);
		out.mDestinationInstruction = in.readParcelable(null);
		out.mBoundingBoxE6 = in.readParcelable(null);

		out.mHashCode = in.readInt();

		out.mRouteHandleID = in.readLong();

		return out;
	}
}
