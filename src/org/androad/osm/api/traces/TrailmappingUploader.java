package org.androad.osm.api.traces;

/**
 * Copyright by Fabien Carrion
 * This program is free software and licensed under GPL.
 * 
 */


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import org.andnav.osm.contributor.util.RecordedGeoPoint;
import org.andnav.osm.contributor.util.RecordedRouteGPXFormatter;

import org.androad.osm.api.util.constants.OSMTraceAPIConstants;
import org.androad.osm.util.constants.OSMConstants;
import org.androad.ui.common.CommonCallback;
import org.openstreetmap.api.exceptions.OSMAPIException;



/**
 * Small java class that allows to upload gpx files to trailmapping.com via its api call.
 * 
 * @author Fabien Carrion
 */
public class TrailmappingUploader implements OSMConstants, OSMTraceAPIConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String GPX_UPLOAD_URL = "http://trailmapping.com/api/trips/create/";

	private static final int BUFFER_SIZE = 65535;
	private static final String BASE64_ENC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	private static final String BOUNDARY = "----------------------------d10f7aa230e8";
	private static final String LINE_END = "\r\n";

	private static final String DEFAULT_TITLE = OSM_CREATOR_INFO;
	private static final String DEFAULT_BODY = OSM_CREATOR_INFO;

	public static final SimpleDateFormat pseudoFileNameFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss'_'SSS");

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * Title will be <code>DEFAULT_TITLE</code>
	 * NOTE: This method is not blocking!
	 * @param recordedGeoPointslist of GeoPoints.
	 */
	public static void uploadAsync(final List<RecordedGeoPoint> recordedGeoPoints, final String username, final String password, final CommonCallback<Void> pCallback) {
		uploadAsync(recordedGeoPoints, username, password, DEFAULT_TITLE, DEFAULT_BODY, pseudoFileNameFormat.format(new GregorianCalendar().getTime()) + "_" + username + ".gpx", pCallback);
	}

	/**
	 * Title will be <code>DEFAULT_TITLE</code>
	 * NOTE: This method is blocking!
	 * @param recordedGeoPointslist of GeoPoints.
	 */
	public static void upload(final List<RecordedGeoPoint> recordedGeoPoints, final String username, final String password) throws OSMAPIException {
		upload(recordedGeoPoints, username, password, DEFAULT_TITLE, DEFAULT_BODY, pseudoFileNameFormat.format(new GregorianCalendar().getTime()) + "_" + username + ".gpx");
	}

	/**
	 * NOTE: This method is not blocking! (Code runs in thread)
	 * @param username <code>not null</code> and <code>not empty</code>. Valid Trailmapping-username
	 * @param password <code>not null</code> and <code>not empty</code>. Valid password to the Trailmapping-username.
	 * @param title <code>not null</code>
	 * @param body if <code>not null</code>
	 * @param pseudoFileName ending with "<code>.gpx</code>"
	 * @param recordedGeoPointslist of GeoPoints.
	 */
	public static void uploadAsync(final List<RecordedGeoPoint> recordedGeoPoints, final String username, final String password, final String title, final String body, final String pseudoFileName, final CommonCallback<Void> pCallback) {
		new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					upload(recordedGeoPoints, username, password, title, body, pseudoFileName);
				}catch(final Exception e){
					pCallback.onFailure(e);
				}
			}
		}, "TrailmappingUpload-Thread").start();
	}

	public static void upload(final List<RecordedGeoPoint> recordedGeoPoints, final String username, final String password, final String title, final String body, final String pseudoFileName) throws OSMAPIException {
		if(username == null || username.length() == 0) {
			return;
		}
		if(password == null || password.length() == 0) {
			return;
		}
		if(title == null || title.length() == 0) {
			return;
		}
		if(body == null || body.length() == 0) {
			return;
		}
		if(pseudoFileName == null || !pseudoFileName.endsWith(".gpx")) {
			return;
		}

		final InputStream gpxInputStream = new ByteArrayInputStream(RecordedRouteGPXFormatter.create(recordedGeoPoints).getBytes());

		//		Log.d(DEBUGTAG, "Uploading " + pseudoFileName + " to openstreetmap.org");
		try {
			final String urlTitle = (title == null) ? DEFAULT_TITLE : title.replaceAll("\\.;&?,/","_");
			final String urlBody = (body == null) ? DEFAULT_BODY : body.replaceAll("\\\\.;&?,/","_");
			final URL url = new URL(GPX_UPLOAD_URL);
			//			Log.d(DEBUGTAG, "Destination Url: " + url);
			final HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(15000);
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.addRequestProperty("Authorization", "Basic " + encodeBase64(username + ":" + password));
			con.addRequestProperty("Content-Type", "multipart/form-data; boundary="+BOUNDARY);
			con.addRequestProperty("Connection", "close"); // counterpart of keep-alive
			con.addRequestProperty("Expect", "");

			con.connect();
			final DataOutputStream out  = new DataOutputStream(new BufferedOutputStream(con.getOutputStream()));
			//            DataOutputStream out  = new DataOutputStream(System.out);

			writeContentDispositionFile(out, "gpx_file", gpxInputStream, pseudoFileName);
			writeContentDisposition(out, "title", urlTitle);
			writeContentDisposition(out, "body", urlBody);

			writeContentDisposition(out, "public", "1");

			out.writeBytes("--" + BOUNDARY + "--" + LINE_END);
			out.flush();

			final int retCode = con.getResponseCode();
			String retMsg = con.getResponseMessage();
			//			Log.d(DEBUGTAG, "\nreturn code: "+retCode + " " + retMsg);
			if (retCode != 200) {
				// Look for a detailed error message from the server
				if (con.getHeaderField("Error") != null) {
					retMsg += "\n" + con.getHeaderField("Error");
				}
				con.disconnect();
				throw new RuntimeException(retCode+" "+retMsg);
			}
			out.close();
			con.disconnect();
		} catch(final Exception e) {
			throw new OSMAPIException(e);
		}
	}

	/**
	 * @param out
	 * @param string
	 * @param gpxFile
	 * @throws IOException
	 */
	private static void writeContentDispositionFile(final DataOutputStream out, final String name, final InputStream gpxInputStream, final String pseudoFileName) throws IOException {
		out.writeBytes("--" + BOUNDARY + LINE_END);
		out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + pseudoFileName + "\"" + LINE_END);
		out.writeBytes("Content-Type: application/octet-stream" + LINE_END);
		out.writeBytes(LINE_END);

		final byte[] buffer = new byte[BUFFER_SIZE];
		//int fileLen = (int)gpxFile.length();
		int read;
		int sumread = 0;
		final InputStream in = new BufferedInputStream(gpxInputStream);
		//		Log.d(DEBUGTAG, "Transferring data to server");
		while((read = in.read(buffer)) >= 0) {
			out.write(buffer, 0, read);
			out.flush();
			sumread += read;
		}
		in.close();
		out.writeBytes(LINE_END);
	}

	/**
	 * @param string
	 * @throws IOException
	 */
	private static void writeContentDisposition(final DataOutputStream out, final String name, final String value) throws IOException {
		out.writeBytes("--" + BOUNDARY + LINE_END);
		out.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"" + LINE_END);
		out.writeBytes(LINE_END);
		out.writeBytes(value + LINE_END);
	}

	private static String encodeBase64(final String s) {
		final StringBuilder out = new StringBuilder();
		for (int i = 0; i < (s.length()+2)/3; ++i) {
			final int l = Math.min(3, s.length()-i*3);
			final String buf = s.substring(i*3, i*3+l);
			out.append(BASE64_ENC.charAt(buf.charAt(0)>>2));
			out.append(BASE64_ENC.charAt((buf.charAt(0) & 0x03) << 4 | (l==1?0:(buf.charAt(1) & 0xf0) >> 4)));
			out.append(l>1 ? BASE64_ENC.charAt((buf.charAt(1) & 0x0f) << 2 | (l==2 ? 0 : (buf.charAt(2) & 0xc0) >> 6)) : '=');
			out.append(l>2 ? BASE64_ENC.charAt(buf.charAt(2) & 0x3f) : '=');
		}
		return out.toString();
	}
}