package org.androad.osm.views.overlay;

import java.util.List;

import org.osmdroid.contributor.util.RecordedGeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.Overlay;

import org.androad.R;
import org.androad.osm.OpenStreetMapActivity;
import org.androad.preferences.PreferenceConstants;
import org.androad.ui.map.overlay.util.ManagedLinePath;
import org.androad.util.constants.Constants;
import org.androad.util.constants.GeoConstants;
import org.androad.util.constants.MathematicalConstants;
import org.androad.util.constants.TimeConstants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Cap;
/**
 * TODO Should be used to overlay simple route over WhereAmIMap
 * @author Nicolas Gramlich
 *
 */
public class OSMMapViewSimpleTraceOverlay extends Overlay implements Constants, TimeConstants, MathematicalConstants, PreferenceConstants, GeoConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	private static final int MARKER_START_HOTSPOT_X = 16;
	private static final int MARKER_START_HOTSPOT_Y = 30;

	private final Bitmap MARKER_START;

	// ===========================================================
	// Fields
	// ===========================================================

	//	private float mMapRotationDegree = 0;

	private List<RecordedGeoPoint> mPolyline;

	private OpenStreetMapActivity mMapActivity;
	private final Paint mPathPaint;
	private final Paint mMarkerPaint;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewSimpleTraceOverlay(final OpenStreetMapActivity aMapAct, final List<RecordedGeoPoint> aPolyline, final int aDisplayQuality) {
        super(aMapAct);
		this.mMapActivity = aMapAct;
		this.mPolyline = aPolyline;

		{ /* Setup the paints. Needs to be inside of the constructor, as all paints are declared final. */
			this.mPathPaint = new Paint();
			this.mPathPaint.setStyle(Paint.Style.STROKE);
			this.mPathPaint.setStrokeWidth(4);
			this.mPathPaint.setARGB(255, 252, 113, 105); // red
			this.mPathPaint.setStrokeCap(Cap.ROUND);

			this.mMarkerPaint = new Paint();

			switch(aDisplayQuality){
				case PREF_DISPLAYQUALITY_BEST:
					this.mPathPaint.setAntiAlias(true);
				case PREF_DISPLAYQUALITY_HIGH:
					this.mPathPaint.setPathEffect(new CornerPathEffect(this.mPathPaint.getStrokeWidth() / 2));
				case PREF_DISPLAYQUALITY_STANDARD:
				case PREF_DISPLAYQUALITY_LOW:
					break;
			}
		}

		this.MARKER_START = BitmapFactory.decodeResource(this.mMapActivity.getResources(), R.drawable.trace_startpin);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setPolyline(final List<RecordedGeoPoint> pPolyline) {
		this.mPolyline = pPolyline;
	}

	public void setMapRotationDegree(final float rotationDegree) {
		//		this.mMapRotationDegree = rotationDegree;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void release() {
		this.MARKER_START.recycle();
		this.mMapActivity = null;
	}

	@Override
	protected void onDrawFinished(final Canvas c, final MapView osmv) {
		// Nothing
	}

	/** This function does some fancy drawing, could be shortened a lot.*/
	@Override
	public void onDraw(final Canvas canvas, final MapView mapView) {
		/* Get the width/height of the underlying MapView.*/
		//		final int mapViewWidth = this.mMapActivity.getMapViewWidth();
		//		final int mapViewHeight = this.mMapActivity.getMapViewHeight();
		//		final GeoPoint curMapCenter = mapView.getMapCenter();

		/* Will hold various screen-coordinates. */
		final Point screenCoords = new Point();


		final Projection pj = mapView.getProjection();

		final ManagedLinePath path = new ManagedLinePath();

		final List<RecordedGeoPoint> polyLine = this.mPolyline;
		if(polyLine.size() > 0){
	
			/* Loop through all MapPoints returned. */
	
			final int currentZoomLevel = mapView.getZoomLevel();
			final int increment = (int)(Math.max(1,Math.pow(2, 16-currentZoomLevel)));
	
			final int polyLineLength = polyLine.size();
	
			int i = 0;
			while(i < polyLineLength){
				path.lineTo(pj.toMapPixels(polyLine.get(i), screenCoords));
				i += increment;
			}
	
			path.lineTo(pj.toMapPixels(polyLine.get(polyLineLength - 1), screenCoords));
	
			/* Used for transforming all paths. */
			//		final float scaleFactor = (this.mapRotationDegree == Constants.NOT_SET)
			//		? 1.0f
			//				: FloatMath.sqrt(mapViewHeight * mapViewHeight + mapViewWidth
			//						* mapViewWidth) / Math.min(mapViewHeight, mapViewWidth));
	
			/* Draw the polyline to the canvas. */
			//				if(!canvas.quickReject(pathDone, EdgeType.BW))
			canvas.drawPath(path, this.mPathPaint);
	
			{ /* Print Pin-Markers. */
				/* ...for the start of the route.*/
				pj.toMapPixels(polyLine.get(0), screenCoords);
	
				canvas.drawBitmap(this.MARKER_START,
						screenCoords.x - MARKER_START_HOTSPOT_X,
						screenCoords.y - MARKER_START_HOTSPOT_Y,
						this.mMarkerPaint);
			}
		}
		{ /* DEBUG Output */
			//					routedrawEndMs = System.currentTimeMillis();
		} /* END DEBUG Output */
	}

	//	===========================================================
	//	Methods
	//	===========================================================



	//	===========================================================
	//	Inner and Anonymous Classes
	//	===========================================================
}
