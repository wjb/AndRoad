package org.androad.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import microsoft.mappoint.TileSystem;

import org.androad.adt.DBPOI;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;

public class POIDBManager {
	// ===========================================================
	// Final Fields
	// ===========================================================

    private final String TABLE = "poi";
    private final int VERSION = 1;

	private final String[] COLUMNS = new String[]{"id", "x", "y", "name", "name_en", "type", "subtype", "opening_hours", "phone", "site"};

	// ===========================================================
	// Fields
	// ===========================================================

    private String dbfile;
    private BoundingBoxE6 limits;
    private SQLiteDatabase db;

	// ===========================================================
	// Constructors
	// ===========================================================

    POIDBManager(final Context context, final String dbfile) {
        this.dbfile = dbfile;

		db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);

        // Get This code from Osmand project
        if(db.getVersion() != VERSION){
            db.close();
            db = null;
            return;
        }

		String metaTable = "loc_meta_" + TABLE;
		Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+metaTable+"'", null);
		boolean dbExist = cursor.moveToFirst();
		cursor.close();
		boolean found = false;
		boolean write = true;
		if(dbExist){
			cursor = db.rawQuery("SELECT MAX_LAT, MAX_LON, MIN_LAT, MIN_LON  FROM " +metaTable, null);
			if(cursor.moveToFirst()){
                limits = new BoundingBoxE6(cursor.getDouble(0), cursor.getDouble(1),
                                           cursor.getDouble(2), cursor.getDouble(3));
				found = true;
			} else {
				found = false;
			}
			cursor.close();
		} else {
			try {
				db.execSQL("CREATE TABLE " + metaTable + " (MAX_LAT DOUBLE, MAX_LON DOUBLE, MIN_LAT DOUBLE, MIN_LON DOUBLE)");
			} catch (RuntimeException e) {
				// case when database is in readonly mode
				write = false;
			}
		}
		
		if (!found) {
            Cursor query = db.query(TABLE,
				new String[] { "MIN(y)", "MAX(x)", "MAX(y)", "MIN(x)" }, null, null, null, null, null);
            if (query.moveToFirst()) {
                GeoPoint topright = new GeoPoint(get31LatitudeY(query.getInt(0)), get31LongitudeX(query.getInt(1)));
                GeoPoint bottomleft = new GeoPoint(get31LatitudeY(query.getInt(2)), get31LongitudeX(query.getInt(3)));
                limits = new BoundingBoxE6((int) (topright.getLatitudeE6() + 1 * 1E6),
                                           (int) (topright.getLongitudeE6() + 1.5 * 1E6),
                                           (int) (bottomleft.getLatitudeE6() - 1 * 1E6),
                                           (int) (bottomleft.getLongitudeE6() - 1.5 * 1E6));
            }
            query.close();

			if (write) {
				db.execSQL("INSERT INTO " + metaTable + " VALUES (?, ?, ? ,?)", new Double[]{limits.getLatNorthE6() / 1E6,
                                                                                             limits.getLonEastE6() / 1E6,
                                                                                             limits.getLatSouthE6() / 1E6,
                                                                                             limits.getLonWestE6() / 1E6});
			}
		}


    }

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

    @Override
    public String toString() {
        return dbfile;
    }

	// ===========================================================
	// Methods
	// ===========================================================

	public static double get31LongitudeX(int tileX){
        double x = tileX / 1024f;
		return x / getPowZoom(21) * 360.0 - 180.0;
	}
	
	public static double get31LatitudeY(int tileY){
        double y = tileY / 1024f;
		int sign = y < 0 ? -1 : 1;
		return Math.atan(sign*Math.sinh(Math.PI * (1 - 2 * y / getPowZoom(21)))) * 180d / Math.PI;
	}
	
	public static double getPowZoom(float zoom){
		if(zoom >= 0 && zoom - Math.floor(zoom) < 0.05f){
			return 1 << ((int)zoom); 
		} else {
			return Math.pow(2, zoom);
		}
	}

	public static int get31TileNumberX(double longitude){
		while (longitude < -180 || longitude > 180) {
			if (longitude < 0) {
				longitude += 360;
			} else {
				longitude -= 360;
			}
		}
		long l = 1l << 31;
		return (int)((longitude + 180d)/360d * l);
	}

	public static int get31TileNumberY(double latitude){
		while (latitude < -90 || latitude > 90) {
			if (latitude < 0) {
				latitude += 180;
			} else {
				latitude -= 180;
			}
		}
		double eval = Math.log( Math.tan(Math.toRadians(latitude)) + 1/Math.cos(Math.toRadians(latitude)) );
		long l = 1l << 31;
		if(eval > Math.PI){
			eval = Math.PI;
		}
		return  (int) ((1 - eval / Math.PI) / 2 * l);
	}

    public boolean contains(BoundingBoxE6 view) {

        if (limits == null)
            return false;

        if (limits.contains(view.getLatNorthE6(), view.getLonEastE6())) {
            return true;
        } else if (limits.contains(view.getLatNorthE6(), view.getLonWestE6())) {
            return true;
        } else if (limits.contains(view.getLatSouthE6(), view.getLonEastE6())) {
            return true;
        } else if (limits.contains(view.getLatSouthE6(), view.getLonWestE6())) {
            return true;
        }
        return false;
    }

    public List<DBPOI> getPOIs(BoundingBoxE6 view) {
        final ArrayList<DBPOI> list = new ArrayList<DBPOI>();

        if (db == null) return list;

        String squery = "? < y AND y < ? AND ? < x AND x < ?";

		Cursor query = db.query(TABLE, COLUMNS, squery, 
                                new String[]{get31TileNumberY(view.getLatNorthE6() / 1E6) + "",
                                             get31TileNumberY(view.getLatSouthE6() / 1E6) + "",
                                             get31TileNumberX(view.getLonWestE6() / 1E6) + "",
                                             get31TileNumberX(view.getLonEastE6() / 1E6) + ""},
                                null, null, null);
		if(query.moveToFirst()){
			do {
                final GeoPoint c = new GeoPoint(get31LatitudeY(query.getInt(2)), get31LongitudeX(query.getInt(1)));
				final DBPOI poi = new DBPOI(query.getString(3), c);
				poi.setId(query.getLong(0));
				poi.setEnName(query.getString(4));
				if(poi.getEnName().length() == 0){
					poi.setEnName(poi.getName());
				}
				poi.setType(query.getString(5));
				poi.setSubType(query.getString(6));
				poi.setOpeningHours(query.getString(7));
				poi.setPhone(query.getString(8));
				poi.setSite(query.getString(9));
				list.add(poi);
			} while(query.moveToNext());
		}
		query.close();

        Log.d("Fabien", "count dbpoi = " + list.size());

        return list;
    }

}
