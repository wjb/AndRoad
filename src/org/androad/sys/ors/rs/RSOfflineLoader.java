package org.androad.sys.ors.rs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.androad.osm.exceptions.ExternalStorageNotMountedException;
import org.androad.osm.util.Util;
import org.androad.osm.util.constants.OSMConstants;
import org.androad.sys.ors.adt.Error;
import org.androad.sys.ors.adt.rs.Route;
import org.androad.sys.ors.exceptions.ORSException;

import android.content.Context;
import android.util.Log;

/**
 * Class capable of loading Routes from the SD-Card.
 * @author plusminus
 */
public class RSOfflineLoader implements OSMConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	protected static String EXTERNAL_STORAGE_BASEDIRECTORY;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static Route load(final Context ctx, final String aFileName) throws ExternalStorageNotMountedException, ORSException, IOException{
		try {
			if(!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
				throw new ExternalStorageNotMountedException();
			}else{
				EXTERNAL_STORAGE_BASEDIRECTORY = Util.getAndNavExternalStoragePath();

				// Ensure the routes-directory exists.
				new File(EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_SAVEDROUTES_PATH).mkdirs();
			}

			final File f = new File(EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_SAVEDROUTES_PATH + aFileName);
			final ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(f));
            final Route r = (Route) fileIn.readObject();
            fileIn.close();

            return r;
		} catch (final ClassNotFoundException e) {
			Log.e(DEBUGTAG, "Error", e);
			throw new ORSException(new Error(Error.ERRORCODE_UNKNOWN, Error.SEVERITY_ERROR, "org.androad.ors.rs.RSOfflineLoader.load(...)", "Class Route Not Found Exception"));
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
