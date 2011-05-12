package org.androad.sys.ors.views.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.androad.R;
import org.androad.adt.Favorite;
import org.androad.util.constants.Constants;

public class FavoritePoint extends BitmapItem {

	// ===========================================================
	// Constants
	// ===========================================================

    public final int MAX_DISTANCE = 200;
    protected final Favorite fCenter;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public FavoritePoint(final Favorite aCenter, final Context ctx) {
        super(aCenter, ctx, R.drawable.favorites, aCenter.getName());
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

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
