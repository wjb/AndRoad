package org.androad.sys.ors.views.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.widget.Toast;

import junit.framework.Assert;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView.Projection;

public class BitmapItem {
	// ===========================================================
	// Constants
	// ===========================================================

    public final int MAX_DISTANCE = 200;

	// ===========================================================
	// Fields
	// ===========================================================

	protected final Paint mPaint = new Paint();
	protected GeoPoint mCenter;
    protected final Context ctx;
	private final Point mHotSpot;
    protected String descr = null;

	protected Bitmap icon;

	// ===========================================================
	// Constructors
	// ===========================================================

	public BitmapItem(final GeoPoint aCenter, final Context ctx, final int bitmap, final String descr) {
        this(aCenter, ctx, bitmap, descr, null);
    }

	public BitmapItem(final GeoPoint aCenter, final Context ctx, final int bitmap, final String descr, final Point pHotSpot) {
		Assert.assertNotNull(ctx);

		this.mPaint.setARGB(120,255,0,0); // LookThrough-RED

		this.mCenter = aCenter;
        this.ctx = ctx;
        this.descr = descr;

        this.icon = BitmapFactory.decodeResource(ctx.getResources(), bitmap);

        if (pHotSpot == null) {
            this.mHotSpot = new Point(this.icon.getWidth() / 2, this.icon.getHeight() / 2);
        } else {
            this.mHotSpot = pHotSpot;
        }
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

	public void setCenter(GeoPoint c) {
		this.mCenter = c;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void release() {
		this.icon.recycle();
	}

	public void drawToCanvas(final Canvas c, final Projection pj) {
        if (this.mCenter == null) return;

        final Point screenCoords = new Point();
        pj.toMapPixels(this.mCenter, screenCoords);
        int xpos = screenCoords.x - this.mHotSpot.x;
        int ypos = screenCoords.y - this.mHotSpot.y;
        c.drawBitmap(this.icon, xpos, ypos, this.mPaint);
	}

    public boolean onSingleTapUp(final MotionEvent e, final Projection pj) {
        if (descr == null) return false;

        GeoPoint tap = pj.fromPixels((int)e.getX(), (int)e.getY());
        float distance = mCenter.distanceTo(tap);

        if (distance > MAX_DISTANCE) {
            return false;
        }

        Toast.makeText(ctx, descr, Toast.LENGTH_LONG).show();
        return true;
    }

	// ===========================================================
	// Abstract Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
