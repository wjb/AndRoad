// Created by plusminus on 5:29:23 PM - Mar 4, 2009
package org.androad.osm.views.overlay;

import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.overlay.OpenStreetMapViewOverlay;

import org.androad.osm.adt.GeoLine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;

public class OSMMapViewSimpleLineOverlay extends OpenStreetMapViewOverlay {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private GeoPoint mGeoPointFrom;
	private GeoPoint mGeoPointTo;
	private Paint mPaint = new Paint();

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewSimpleLineOverlay(final Context ctx) {
		this(ctx, null, null);
	}


	public OSMMapViewSimpleLineOverlay(final Context ctx, final GeoLine pGeoLine) {
		this(ctx, pGeoLine.getGeoPointA(), pGeoLine.getGeoPointB());
	}

	public OSMMapViewSimpleLineOverlay(final Context ctx, final GeoPoint pGeoPointFrom, final GeoPoint pGeoPointTo) {
        super(ctx);
		this.mGeoPointFrom = pGeoPointFrom;
		this.mGeoPointTo = pGeoPointTo;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Paint getPaint() {
		return this.mPaint;
	}

	public void setPaint(final Paint pPaint) {
		this.mPaint = pPaint;
	}

	/**
	 * Dashed, Red, Width=5;
	 */
	public void setPaintDashed() {
		this.mPaint.setPathEffect(new DashPathEffect(new float[]{10,5}, 0));
		this.mPaint.setStrokeWidth(5);
		this.mPaint.setColor(Color.RED);
		this.mPaint.setAntiAlias(false);
	}

	/**
	 * Dashed, Red, Width=5;
	 */
	public void setPaintNormal() {
		this.mPaint.setStrokeWidth(5);
		this.mPaint.setColor(Color.RED);
		this.mPaint.setAntiAlias(true);
	}

	public GeoPoint getFrom() {
		return this.mGeoPointFrom;
	}

	public void setFrom(final GeoPoint pGeoPointFrom) {
		this.mGeoPointFrom = pGeoPointFrom;
	}

	public GeoPoint getTo() {
		return this.mGeoPointTo;
	}

	public void setTo(final GeoPoint pGeoPointTo) {
		this.mGeoPointTo = pGeoPointTo;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onDraw(final Canvas c, final OpenStreetMapView osmv) {
		if(this.mGeoPointFrom != null && this.mGeoPointTo != null){
			final Point from = osmv.getProjection().toMapPixels(this.mGeoPointFrom, null);
			final Point to = osmv.getProjection().toMapPixels(this.mGeoPointTo, null);

			c.drawLine(from.x, from.y, to.x, to.y, this.mPaint);
		}
	}

	@Override
	protected void onDrawFinished(final Canvas c, final OpenStreetMapView osmv) {
		/* Nothing. */
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
