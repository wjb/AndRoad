package org.andnav2.sys.ors.views.overlay;

import junit.framework.Assert;

import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView.OpenStreetMapViewProjection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

public class BitmapItem {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final Paint mPaint = new Paint();
	protected final GeoPoint mCenter;
    protected final Context ctx;

	protected Bitmap icon;

	// ===========================================================
	// Constructors
	// ===========================================================

	public BitmapItem(final GeoPoint aCenter, final Context ctx, final int bitmap) {
		Assert.assertNotNull(aCenter);
		Assert.assertNotNull(ctx);

		this.mPaint.setARGB(120,255,0,0); // LookThrough-RED

		this.mCenter = aCenter;
        this.ctx = ctx;

        this.icon = BitmapFactory.decodeResource(ctx.getResources(), bitmap);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Paint getPaint(){
		return this.mPaint;
	}

	public GeoPoint getCenter() {
		return this.mCenter;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void drawToCanvas(final Canvas c, final OpenStreetMapViewProjection pj) {
        final Point screenCoords = new Point();
        pj.toMapPixels(this.mCenter, screenCoords);
        c.drawBitmap(this.icon, screenCoords.x, screenCoords.y, this.mPaint);
	}

    public boolean onSingleTapUp(final MotionEvent e, final OpenStreetMapViewProjection pj) {
        return false;
    }

	// ===========================================================
	// Abstract Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
