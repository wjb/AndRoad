package org.androad.ui.sd;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;

import org.androad.R;
import org.androad.osm.util.constants.OSMConstants;
import org.androad.preferences.Preferences;
import org.androad.ui.AndNavBaseActivity;
import org.androad.ui.common.OnClickOnFocusChangedListenerAdapter;
import org.androad.ui.common.views.FastScrollView;
import org.androad.util.UserTask;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class SDPoi extends AndNavBaseActivity {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final String STATE_POI_ITEMS_ID = "state_poi_items_id";

    private static final String poiFolderPath = org.androad.osm.util.Util.getAndRoadExternalStoragePath() + OSMConstants.SDCARD_SAVEDPOI_PATH;

    private static final int BUFFER_SIZE = 5120;

	// ===========================================================
	// Fields
	// ===========================================================

	private Bundle bundleCreatedWith;
	private ListView mPoiList;

	private ArrayList<PoiItem> mPoi = new ArrayList<PoiItem>();

	private boolean mPoiInitFinished = false;

    private ProgressDialog pd;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Preferences.applySharedSettings(this);
		this.setContentView(R.layout.sd_poi);

		this.bundleCreatedWith = this.getIntent().getExtras();
		this.mPoiList = (ListView) this.findViewById(R.id.list_poi);

		final TextView empty = new TextView(this);
		empty.setText(R.string.list_empty);
		this.mPoiList.setEmptyView(empty);

		initListView();

		this.applyTopMenuButtonListeners();

		if(savedInstanceState == null) {
			updatePoiListItems();
		}

        new File(poiFolderPath).mkdirs();
	}

	@Override
	protected void onPause() {
		super.onPause();
        if (pd != null && pd.isShowing()) pd.dismiss();
	}

    private void downloadFile(final URL url, final OutputStream out) {
        int tries = 3;
        int read = 0;
        int length = 0;
        byte[] buffer = new byte[BUFFER_SIZE];

        while (tries > 0) {
            if (read > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }

            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(30000);
                conn.setConnectTimeout(30000);
                if (read > 0) {
                    conn.setRequestProperty("Range", "bytes=" + read + "-" + (length -1));
                }
                switch (conn.getResponseCode()) {
                case HttpURLConnection.HTTP_PARTIAL:
                    break;
                case HttpURLConnection.HTTP_OK:
                    length = conn.getContentLength();
                    break;
                default:
                    conn.disconnect();
                    tries--;
                    continue;
                }
                final InputStream is = conn.getInputStream();
                int r = 0;

                while ((r = is.read(buffer)) != -1) {
                    out.write(buffer, 0, r);
                    read += r;
                }
                is.close();
                out.flush();
            } catch (IOException e) {
                tries--;
            }

            if (length <= read) {
                return;
            }
        }
    }

    private void downloadPoiItem(final PoiItem p) {
		pd = ProgressDialog.show(this, getString(R.string.sd_poi_item_loading_title), getString(R.string.please_wait_a_moment), false); // TODO Make determinate, when SDK supports this.

		final String progressBaseString = getString(R.string.sd_poi_item_loading_progress);

        final String fileToDownload = poiFolderPath + p.mName;

		new UserTask<Void, Integer, Void>() {
			@Override
			public Void doInBackground(final Void... params) {
				try {
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(fileToDownload));
                    if (p.mParts < 2) {
                        URL url = new URL("http://download.osmand.net/download?file=" + p.mName);
                        downloadFile(url, out);
                    } else {
                        for(int i = 1; i <= p.mParts; i++) {
                            URL url = new URL("http://download.osmand.net/download?file=" + p.mName + "-" + i);
                            downloadFile(url, out);
                        }
                    }
                    out.close();
                    if (p.mName.endsWith(".zip")) {
                        final ZipInputStream zipIn = new ZipInputStream(new FileInputStream(fileToDownload));
                        ZipEntry entry = null;
                        while ((entry = zipIn.getNextEntry()) != null) {
                            final File fs = new File(poiFolderPath, entry.getName());
                            out = new BufferedOutputStream(new FileOutputStream(fs));
                            int read;
                            byte[] buffer = new byte[BUFFER_SIZE];
                            while ((read = zipIn.read(buffer)) != -1) {
                                out.write(buffer, 0, read);
                            }
                            out.close();
                        }
                        zipIn.close();
                        (new File(fileToDownload)).delete();
                    }
				} catch (Exception e) {
                    Log.e(OSMConstants.DEBUGTAG, "Downloading poi index error", e);
				}
				return null;
			}

			@Override
			public void onProgressUpdate(final Integer... progress) {
				pd.setMessage(String.format(progressBaseString, (int)(100*((float)progress[0] / progress[1])), progress[0], progress[1]));
			}

			@Override
			public void onPostExecute(final Void result) {
				try{
					pd.dismiss();
                    pd = null;
				}catch(final IllegalArgumentException ia){
					// Nothing
				}
			}
		}.execute();
    }

	private void updatePoiListItems() {
		final PoiListAdapter pla = new PoiListAdapter(this);

		pd = ProgressDialog.show(this, getString(R.string.sd_poi_loading_title), getString(R.string.please_wait_a_moment), false); // TODO Make determinate, when SDK supports this.

		final String progressBaseString = getString(R.string.sd_poi_loading_progress);

		new UserTask<Void, Integer, Void>(){
			@Override
			public Void doInBackground(final Void... params) {
				try {
					URL url = new URL("http://download.osmand.net/get_indexes");
					XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
					parser.setInput(url.openStream(), "UTF-8");
					int next;
					while((next = parser.next()) != XmlPullParser.END_DOCUMENT) {
						if(next == XmlPullParser.START_TAG && (parser.getName().equals("region") ||
								parser.getName().equals("multiregion"))) {
							String name = parser.getAttributeValue(null, "name");
							String size = parser.getAttributeValue(null, "size");
							String date = parser.getAttributeValue(null, "date");
							String description = parser.getAttributeValue(null, "description");

                            if (!description.startsWith("POI index")) continue;

                            int parts = 0;
                            try {
                                parts = Integer.parseInt(parser.getAttributeValue(null, "parts"));
                            } catch (NumberFormatException e) {}
                            PoiItem poiItem = new PoiItem(name, description, date, size, parts);
                            SDPoi.this.mPoi.add(poiItem);
						}
					}
				} catch (Exception e) {
                    Log.e(OSMConstants.DEBUGTAG, "Downloading poi index error", e);
				}

                /* Adapt the list to the Adapter. */
                pla.setListItems(SDPoi.this.mPoi);/* Orders by name, ascending. */
                SDPoi.this.mPoiInitFinished = true;
				return null;
			}

			@Override
			public void onProgressUpdate(final Integer... progress) {
				pd.setMessage(String.format(progressBaseString, (int)(100*((float)progress[0] / progress[1])), progress[0], progress[1]));
			}

			@Override
			public void onPostExecute(final Void result) {
				/* Adapt the Adapter to the ListView. */
				SDPoi.this.mPoiList.setAdapter(pla);
				try{
					pd.dismiss();
                    pd = null;
				}catch(final IllegalArgumentException ia){
					// Nothing
				}
			}
		}.execute();
	}

	protected void initListView() {
		this.mPoiList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
				final PoiItem p = (PoiItem)parent.getAdapter().getItem(position);
                downloadPoiItem(p);
			}
		});
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		final char c;
		switch(keyCode){
			case KeyEvent.KEYCODE_A: c = 'a'; break;
			case KeyEvent.KEYCODE_B: c = 'b'; break;
			case KeyEvent.KEYCODE_C: c = 'c'; break;
			case KeyEvent.KEYCODE_D: c = 'd'; break;
			case KeyEvent.KEYCODE_E: c = 'e'; break;
			case KeyEvent.KEYCODE_F: c = 'f'; break;
			case KeyEvent.KEYCODE_G: c = 'g'; break;
			case KeyEvent.KEYCODE_H: c = 'h'; break;
			case KeyEvent.KEYCODE_I: c = 'i'; break;
			case KeyEvent.KEYCODE_J: c = 'j'; break;
			case KeyEvent.KEYCODE_K: c = 'k'; break;
			case KeyEvent.KEYCODE_L: c = 'l'; break;
			case KeyEvent.KEYCODE_M: c = 'n'; break;
			case KeyEvent.KEYCODE_N: c = 'm'; break;
			case KeyEvent.KEYCODE_O: c = 'o'; break;
			case KeyEvent.KEYCODE_P: c = 'p'; break;
			case KeyEvent.KEYCODE_Q: c = 'q'; break;
			case KeyEvent.KEYCODE_R: c = 'r'; break;
			case KeyEvent.KEYCODE_S: c = 's'; break;
			case KeyEvent.KEYCODE_T: c = 't'; break;
			case KeyEvent.KEYCODE_U: c = 'u'; break;
			case KeyEvent.KEYCODE_V: c = 'v'; break;
			case KeyEvent.KEYCODE_W: c = 'w'; break;
			case KeyEvent.KEYCODE_X: c = 'x'; break;
			case KeyEvent.KEYCODE_Y: c = 'y'; break;
			case KeyEvent.KEYCODE_Z: c = 'z'; break;
			default:
				return super.onKeyDown(keyCode, event);
		}
		int position = Collections.binarySearch(this.mPoi, new PoiItem(String.valueOf(c), "", "", "", 0));

		if(position < 0){
			/* Negative result means the insertion-point.
			 * See definition of Collections.binarySearch */
			position = -(position + 1);
		}

		this.mPoiList.setSelectionFromTop(position, 0);
		return true;
	}

	@Override
	public void onSaveInstanceState(final Bundle out) {
		if(this.mPoiInitFinished) {
			out.putParcelableArrayList(STATE_POI_ITEMS_ID, this.mPoi);
		}
	}

	@Override
	protected void onRestoreInstanceState(final Bundle in) {
		final ArrayList<PoiItem> restoredItems = in.getParcelableArrayList(STATE_POI_ITEMS_ID);
		if(this.mPoi == null){
			updatePoiListItems();
		}else{
			this.mPoi = restoredItems;
			final PoiListAdapter pla = new PoiListAdapter(this);
			pla.setListItems(this.mPoi);
			this.mPoiList.setAdapter(pla);
			this.mPoiInitFinished = true;
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch(resultCode){
			case SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS:
				this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_SUCCESS, data);
				this.finish();
				break;
			case SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED:
				this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED, data);
				this.finish();
				break;
		}
		/* Finally call the super()-method. */
		super.onActivityResult(requestCode, resultCode, data);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected void applyTopMenuButtonListeners() {
		/* Set Listener for Back-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_poi_back)) {
			@Override
			public void onClicked(final View me) {
				if (SDPoi.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDPoi.this, R.raw.close).start();
				}

				/* Back one level. */
				SDPoi.this.setResult(SUBACTIVITY_RESULTCODE_UP_ONE_LEVEL);
				SDPoi.this.finish();
			}
		};

		/* Set Listener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_sd_poi_close)) {
			@Override
			public void onClicked(final View me) {
				if (SDPoi.super.mMenuVoiceEnabled) {
					MediaPlayer.create(SDPoi.this, R.raw.close).start();
				}
				/*
				 * Set ResultCode that the calling activity knows that we want
				 * to go back to the Base-Menu
				 */
				SDPoi.this.setResult(SUBACTIVITY_RESULTCODE_CHAINCLOSE_QUITTED);
				SDPoi.this.finish();
			}
		};
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private static class PoiItemToResolve{
		protected final String mAddressDescription;
		protected final String mAddressTypeAppendix;

		private PoiItemToResolve(final String addressDescription, final int personID, final String addressTypeAppendix) {
			this.mAddressDescription = addressDescription;
			this.mAddressTypeAppendix = addressTypeAppendix;
		}
	}

	static class PoiItem implements Comparable<PoiItem>, Parcelable{
		protected final String mName;
		protected final String mDescription;
        protected final String mDate;
        protected final String mSize;
        protected final int mParts;

		private PoiItem(final String pName, final String pDescription, final String pDate, final String pSize, final int pParts) {
			this.mName = pName;
			this.mDescription = pDescription;
			this.mDate = pDate;
			this.mSize = pSize;
			this.mParts = pParts;
		}

        public boolean exists() {
            final String filename;
            if (this.mName.endsWith(".zip")) {
                filename = this.mName.substring(0, this.mName.length() - 3);
            } else {
                filename = this.mName;
            }
            final File f = new File(poiFolderPath, filename);
            return f.exists();
        }

		@Override
		public int compareTo(final PoiItem another) {
			return this.mName.compareToIgnoreCase(another.mName);
		}

		// ===========================================================
		// Parcelable
		// ===========================================================

		public static final Parcelable.Creator<PoiItem> CREATOR = new Parcelable.Creator<PoiItem>() {
			public PoiItem createFromParcel(final Parcel in) {
				return readFromParcel(in);
			}

			public PoiItem[] newArray(final int size) {
				return new PoiItem[size];
			}
		};

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(final Parcel out, final int flags) {
			out.writeString(this.mName);
			out.writeString(this.mDescription);
			out.writeString(this.mDate);
			out.writeString(this.mSize);
			out.writeInt(this.mParts);
		}

		private static PoiItem readFromParcel(final Parcel in){
			final String name = in.readString();
			final String description = in.readString();
			final String date = in.readString();
			final String size = in.readString();
			final int parts = in.readInt();

			return new PoiItem(name, description, date, size, parts);
		}
	}

	private class POIListItemView extends LinearLayout{

		private final TextView mTVName;
		private final TextView mTVDescription;
		private final TextView mTVDate;
		private final TextView mTVSize;

		public POIListItemView(final Context context, final PoiItem aPOIItem) {
			super(context);

			this.setOrientation(VERTICAL);

			this.mTVName = new TextView(context);
			this.mTVName.setText(aPOIItem.mName);
			this.mTVName.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
			this.mTVName.setPadding(10,0,20,0);
            if (aPOIItem.exists()) {
                this.mTVName.setTextColor(Color.GREEN);
            }

			addView(this.mTVName, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

			this.mTVDescription = new TextView(context);
			this.mTVDescription.setText(aPOIItem.mDescription);
			this.mTVDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12);

			addView(this.mTVDescription, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

			this.mTVDate = new TextView(context);
			this.mTVDate.setText(aPOIItem.mDate);
			this.mTVDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12);

			addView(this.mTVDate, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

			this.mTVSize = new TextView(context);
			this.mTVSize.setText(aPOIItem.mSize + "M");
			this.mTVSize.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12);

			addView(this.mTVSize, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		}

	}

	class PoiListAdapter extends BaseAdapter implements FastScrollView.SectionIndexer{

		/** Remember our context so we can use it when constructing views. */
		private final Context mContext;

		private List<PoiItem> mItems = new ArrayList<PoiItem>();

		private String[] mAlphabet;

		public PoiListAdapter(final Context context) {
			this.mContext = context;
			initAlphabet(context);
		}

		public void addItem(final PoiItem it) {
			this.mItems.add(it);
			Collections.sort(this.mItems);
		}

		public void setListItems(final List<PoiItem> lit) {
            if (lit == null) return;
			this.mItems = lit;
			Collections.sort(this.mItems);
		}

		/** @return The number of items in the */
		public int getCount() { return this.mItems.size(); }

		public Object getItem(final int position) { return this.mItems.get(position); }

		@Override
		public long getItemId(final int position) { return position; }

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			POIListItemView btv;
			if (convertView == null) {
				btv = new POIListItemView(this.mContext, this.mItems.get(position));
			} else { // Reuse/Overwrite the View passed
				// We are assuming(!) that it is castable!
				btv = (POIListItemView) convertView;
				btv.mTVName.setText( this.mItems.get(position).mName);
				btv.mTVDescription.setText(this.mItems.get(position).mDescription);
				btv.mTVDate.setText(this.mItems.get(position).mDate);
				btv.mTVSize.setText(this.mItems.get(position).mSize + "M");
			}
			return btv;
		}

		// ===========================================================
		// FastScrollView-Methods
		// ===========================================================

		@Override
		public int getPositionForSection(final int section) {

			final String firstChar = this.mAlphabet[section];

			/* Find the index, of the firstchar within the Poi-Items */
			int position = Collections.binarySearch(this.mItems, new PoiItem(firstChar, null, null, null, 0));

			if(position < 0){
				/* Negative result means the insertion-point.
				 * See definition of Collections.binarySearch */
				position = -(position + 1);
			}

			return position;
		}

		@Override
		public int getSectionForPosition(final int position) {
			return 0;
		}

		@Override
		public Object[] getSections() {
			return this.mAlphabet;
		}

		private void initAlphabet(final Context context) {
			final String alphabetString = context.getResources().getString(R.string.alphabet); // TODO Use Systems Alphabet!
			this.mAlphabet = new String[alphabetString.length()];

			for (int i = 0; i < this.mAlphabet.length; i++) {
				this.mAlphabet[i] = String.valueOf(alphabetString.charAt(i));
			}
		}
	}

}
