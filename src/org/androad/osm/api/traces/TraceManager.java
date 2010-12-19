package org.androad.osm.api.traces;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.andnav.osm.contributor.OSMUploader;
import org.andnav.osm.contributor.util.RecordedGeoPoint;

import org.androad.R;
import org.androad.Splash;
import org.androad.osm.api.traces.util.Util;
import org.androad.osm.util.constants.OSMConstants;
import org.androad.preferences.Preferences;
import org.androad.ui.common.CommonCallback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class TraceManager implements OSMConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int FALLBACK_NOTIFICATION_ID = 1234;

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

	/**
	 * @param pContext can be null, when aOSMContributionPolicy is not UPLOADTOOWNOSMACCOUNT
	 */
	public static void contributeAsync(final Context pContext, final ArrayList<RecordedGeoPoint> pRecordedGeoPoints){
		try{
			if(Preferences.getTracePolicyExternal(pContext)) {
				doSaveToExternal(pContext, pRecordedGeoPoints);
			}

			if(Preferences.getTracePolicyOSM(pContext)) {
				doUploadToOSMAccount(pContext, pRecordedGeoPoints);
			}

			if(Preferences.getTracePolicyTrailmapping(pContext)) {
				doUploadToTrailmappingAccount(pContext, pRecordedGeoPoints);
			}
		}catch(final Throwable t){
			/* Ensure nothing fails in here! */
			Log.e(DEBUGTAG, "Trace-Error", t);
		}
	}

	private static void doSaveToExternal(final Context pContext, final ArrayList<RecordedGeoPoint> pRecordedGeoPoints) {
		// Check if External Media is mounted.
		if(pContext != null && android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			if(Preferences.getMinimalTraceFilteringEnabled(pContext)){
				if(Util.isSufficienDataForUpload(pRecordedGeoPoints)) {
					GPXToFileWriter.writeToFileAsync(pRecordedGeoPoints);
				}
			}else{
				GPXToFileWriter.writeToFileAsync(pRecordedGeoPoints);
			}
		}
	}

	private static void doUploadToOSMAccount(final Context pContext, final ArrayList<RecordedGeoPoint> pRecordedGeoPoints) {
        String user = Preferences.getOSMAccountUsername(pContext);
        String password = Preferences.getOSMAccountPassword(pContext);
        String description = "AndRoad - automatically created route.";
        String tags = "AndRoad";

		if(pContext != null){
			if(Preferences.getMinimalTraceFilteringEnabled(pContext)){
				if(Util.isSufficienDataForUpload(pRecordedGeoPoints)) {
					OSMUploader.uploadAsync(user, password, description, tags, true, pRecordedGeoPoints, OSMUploader.pseudoFileNameFormat.format(new GregorianCalendar().getTime()) + "_" + user + ".gpx");
				}
			}else{
				OSMUploader.uploadAsync(pRecordedGeoPoints);
			}
		}
	}


	private static void doUploadToTrailmappingAccount(final Context pContext, final ArrayList<RecordedGeoPoint> pRecordedGeoPoints) {
		if(pContext != null){
			/* Prepare the callback. */
			final CommonCallback<Void> callback = new CommonCallback<Void>(){
				@Override
				public void onFailure(final Throwable t) {
					// look up the notification manager service
					final NotificationManager nm = (NotificationManager)pContext.getSystemService(Context.NOTIFICATION_SERVICE);

					/* The intent to be launched when the notification was clicked. */
					final Intent contentIntent = new Intent(pContext, Splash.class);
					contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					/* The actual notification. */
					final Notification notification = new Notification(R.drawable.icon, pContext.getString(R.string.notif_settings_tracepolicy_osmcontribution_failed_fallback_used_title), System.currentTimeMillis());

					/* The PendingIntent used to invoke the contentIntent. */
					final PendingIntent appIntent = PendingIntent.getActivity(pContext, 0, contentIntent, 0);

					/* Set the textual notification-description and the PendingIntent to the Notification. */
					notification.setLatestEventInfo(pContext, pContext.getText(R.string.notif_settings_tracepolicy_osmcontribution_failed_fallback_used_title), pContext.getText(R.string.notif_settings_tracepolicy_osmcontribution_failed_fallback_used_message), appIntent);

					/* Fire the notification. */
					nm.notify(FALLBACK_NOTIFICATION_ID, notification);

					/* Write to SD-Card as a fallback. */
					doSaveToExternal(pContext, pRecordedGeoPoints);
				}

				@Override
				public void onSuccess(final Void result) {
					/* Nothing. */
				}
			};

			if(Preferences.getMinimalTraceFilteringEnabled(pContext)){
				if(Util.isSufficienDataForUpload(pRecordedGeoPoints)) {
					TrailmappingUploader.uploadAsync(pRecordedGeoPoints, Preferences.getTrailmappingUsername(pContext), Preferences.getTrailmappingPassword(pContext), callback);
				}
			}else{
				TrailmappingUploader.uploadAsync(pRecordedGeoPoints, Preferences.getTrailmappingUsername(pContext), Preferences.getTrailmappingPassword(pContext), callback);
			}
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
