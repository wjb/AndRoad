package org.androad.sys.ors.views.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView.OpenStreetMapViewProjection;

import org.androad.R;
import org.androad.adt.Favorite;
import org.androad.osm.views.util.Util;
import org.androad.util.constants.Constants;

public class FavoritePoint extends BitmapItem {

	// ===========================================================
	// Constants
	// ===========================================================

    public final int MAX_DISTANCE = 80;
    protected final Favorite fCenter;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public FavoritePoint(final Favorite aCenter, final Context ctx) {
        super(aCenter, ctx, R.drawable.favorites);
        this.fCenter = aCenter;

        // Load favorite image if there is one
        final String filename = aCenter.getPhotoFilename();
        final Bitmap photo = BitmapFactory.decodeFile(filename);
        if (photo != null)
            icon = Bitmap.createScaledBitmap(photo, 45, 45, true);
        else
            Log.d(Constants.DEBUGTAG, "No Photo on path " + filename);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

    @Override
    public boolean onSingleTapUp(final MotionEvent e, final OpenStreetMapViewProjection pj) {
        final Point screenCoords = new Point();
        pj.toMapPixels(this.fCenter, screenCoords);
        float distance = Util.calculateDistance(screenCoords.x, (int)e.getX(),
                                                screenCoords.y, (int)e.getY());

        if (distance > MAX_DISTANCE) {
            return false;
        }

        Toast.makeText(ctx, fCenter.getName(), Toast.LENGTH_LONG).show();
        return true;
    }


	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
