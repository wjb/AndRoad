// Created by plusminus on 5:29:23 PM - Mar 4, 2009
package org.androad.osm.views.overlay;

import org.andnav.osm.util.BoundingBoxE6;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.overlay.OpenStreetMapViewOverlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;

public class OSMMapViewSimpleRectangleOverlay extends OpenStreetMapViewOverlay {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private BoundingBoxE6 mBoundingBox;
	private Paint mPaint = new Paint();

	// ===========================================================
	// Constructors
	// ===========================================================

	public OSMMapViewSimpleRectangleOverlay(final Context ctx) {
		this(ctx, null);
	}

	public OSMMapViewSimpleRectangleOverlay(final Context ctx, final BoundingBoxE6 pBoundingBoxE6) {
        super(ctx);
		this.mBoundingBox = pBoundingBoxE6;
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
	 * Dashed, Red, Width=2;
	 */
	public void initDefaultPaint() {
		this.mPaint.setPathEffect(new DashPathEffect(new float[]{10,5}, 0));
		this.mPaint.setStyle(Style.STROKE);
		this.mPaint.setStrokeWidth(2);
		this.mPaint.setColor(Color.RED);
		this.mPaint.setAntiAlias(false);
	}

	public BoundingBoxE6 getBoundingBoxE6() {
		return this.mBoundingBox;
	}

	public void setBoundingBoxE6(final BoundingBoxE6 pBoundingBoxE6) {
		this.mBoundingBox = pBoundingBoxE6;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onDraw(final Canvas c, final OpenStreetMapView osmv) {
		if(this.mBoundingBox != null){
			final Rect bbox = osmv.getProjection().toPixels(this.mBoundingBox);

			c.drawRect(bbox, this.mPaint);
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
