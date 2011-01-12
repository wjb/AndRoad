// Created by plusminus on 21:42:59 - 16.10.2008
package org.androad.sys.ors.adt.aoi;

import java.util.Formatter;

import junit.framework.Assert;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView.Projection;

import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

public class CircleByCenterPoint extends AreaOfInterest{

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final int mRadiusMeters;
	protected final GeoPoint mCenter;

	// ===========================================================
	// Constructors
	// ===========================================================

	public CircleByCenterPoint(final GeoPoint aCenter, final int aRadiusMeters) {
		Assert.assertNotNull(aCenter);
		Assert.assertTrue(aRadiusMeters > 0);

		this.mCenter = aCenter;
		this.mRadiusMeters = aRadiusMeters;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public GeoPoint getCenter() {
		return this.mCenter;
	}

	public int getRadiusMeters() {
		return this.mRadiusMeters;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	/**
	 * Output similar to:
	 * <pre> &lt;xls:AOI&gt;
	 *   &lt;gml:CircleByCenterPoint numArc=&quot;1&quot;&gt;
	 *     &lt;gml:pos&gt;-134.2567801 38.6721569&lt;/gml:pos&gt;
	 *     &lt;gml:radius uom=&quot;m&quot;&gt;3000&lt;/gml:radius&gt;
	 *   &lt;/gml:CircleByCenterPoint&gt;
	 * &lt;/xls:AOI&gt;</pre>
	 */
	@Override
	public void appendToStringBuilder(final StringBuilder sb, final Formatter f) {
		sb.append(XLS_AREAOFINTEREST_TAG_OPEN)
		.append(GML_CIRCLEBYCENTERPOINT_TAG_OPEN);

		f.format(GML_POS_TAG, this.mCenter.getLongitudeE6() / 1E6, this.mCenter.getLatitudeE6() / 1E6);
		f.format(GML_RADIUS_TAG, this.mRadiusMeters);

		sb.append(GML_CIRCLEBYCENTERPOINT_TAG_CLOSE)
		.append(XLS_AREAOFINTEREST_TAG_CLOSE);
	}

	@Override
	public void drawToCanvas(final Canvas c, final Projection pj) {
		final Point p = pj.toMapPixels(this.mCenter, null);
		c.drawCircle(p.x, p.y, pj.metersToEquatorPixels(this.mRadiusMeters), this.mPaint);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// ===========================================================
	// Parcelable
	// ===========================================================

	public static final Parcelable.Creator<CircleByCenterPoint> CREATOR = new Parcelable.Creator<CircleByCenterPoint>() {
		public CircleByCenterPoint createFromParcel(final Parcel in) {
			return readFromParcel(in);
		}

		public CircleByCenterPoint[] newArray(final int size) {
			return new CircleByCenterPoint[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel out, final int arg1) {
		out.writeInt(this.mRadiusMeters);
		out.writeParcelable(this.mCenter, 0);
	}

	private static CircleByCenterPoint readFromParcel(final Parcel in){
		final int radius = in.readInt();
		final GeoPoint center = in.readParcelable(null);
		return new CircleByCenterPoint(center, radius);
	}
}
