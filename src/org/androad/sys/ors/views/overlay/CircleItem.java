package org.androad.sys.ors.views.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.widget.Toast;

import junit.framework.Assert;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView.Projection;

public class CircleItem {
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
    protected String descr = "Description";

	// ===========================================================
	// Constructors
	// ===========================================================

	public CircleItem(final GeoPoint aCenter, final Context ctx, final int color, final String descr) {
		Assert.assertNotNull(ctx);

		this.mPaint.setColor(color);
        this.mPaint.setStyle(Paint.Style.FILL);

		this.mCenter = aCenter;
        this.ctx = ctx;
        this.descr = descr;
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

	public void drawToCanvas(final Canvas c, final Projection pj) {
        if (this.mCenter == null) return;

        final Point screenCoords = new Point();
        pj.toMapPixels(this.mCenter, screenCoords);
        int radius = 10;
        int xpos = screenCoords.x - radius;
        int ypos = screenCoords.y - radius;
        c.drawCircle(xpos, ypos, radius, this.mPaint);
	}

    public boolean onSingleTapUp(final MotionEvent e, final Projection pj) {
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
