package org.androad.sys.ors.util;

import org.androad.sys.ors.util.constants.ORSXMLConstants;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * @since 2008-04-06 19:03:54
 * @author Nicolas 'plusminus' Gramlich
 * License:
 * @see Creative Commons Attribution-Noncommercial-Share Alike 2.0 Germany License .
 * Permissions beyond the scope of this license may be requested at plusminus {at} anddev {dot} org
 */
public class Util implements ORSXMLConstants{

	// ===========================================================
	// Final Fields
	// ===========================================================

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

	public static String getORSClientName(final Context ctx){
		return CLIENTNAME_ANDNAV_PREFIX + CLIENTNAME_SPACER + org.androad.util.Util.getVersionName(ctx) + CLIENTNAME_SPACER + org.androad.util.Util.getDeviceIDHashed(ctx);
	}

	public static String removeHtmlTags(final String pInput) {
		return pInput.replaceAll("\\<.*?\\>", "");
	}

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
