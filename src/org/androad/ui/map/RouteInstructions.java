package org.androad.ui.map;

import java.util.ArrayList;
import java.util.List;

import org.androad.R;
import org.androad.adt.UnitSystem;
import org.androad.preferences.Preferences;
import org.androad.sys.ors.adt.rs.Route;
import org.androad.sys.ors.adt.rs.RouteInstruction;
import org.androad.ui.AndNavBaseActivity;
import org.androad.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RouteInstructions extends AndNavBaseActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

    private Route mRoute;
	private ListView mRouteInstructionsList;
	private UnitSystem mUnitSystem;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);

		this.setContentView(R.layout.map_routeinstructions_list);
		this.mUnitSystem = Preferences.getUnitSystem(this);

        final Intent intent = this.getIntent();
        final Bundle b = intent.getBundleExtra(RouteInstructions.class.getName());
        this.mRoute = b.getParcelable(RouteInstructions.class.getName());
		this.mRouteInstructionsList = (ListView) this.findViewById(R.id.list_map_routeinstructions_list);

		/* Set Listener for Close-Button. */
		new OnClickOnFocusChangedListenerAdapter(this.findViewById(R.id.ibtn_map_routeinstructions_list_close)) {

			@Override
			public void onClicked(final View me) {
				RouteInstructions.this.finish();
			}
		};

        final RouteInstructionsListAdapter rla = new RouteInstructionsListAdapter(this, mUnitSystem);
        for (RouteInstruction ri : this.mRoute.getRouteInstructions()) {
            rla.addItem(new RouteInstructionItem(this, ri, mUnitSystem));
        }
        this.mRouteInstructionsList.setAdapter(rla);
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

	private static class RouteInstructionItem implements Parcelable {
		protected final String mDistance;
		protected final String mTime;
		protected final String mDescription;
        protected final Bitmap mTurnAngle;

        public RouteInstructionItem(final String aDistance, final String aTime, final String aDescription, final Bitmap aTurnAngle) {
            this.mDistance = aDistance;
            this.mTime = aTime;
            this.mDescription = aDescription;
            this.mTurnAngle = aTurnAngle;
        }

		public RouteInstructionItem(final Context context, final RouteInstruction ri, final UnitSystem mUnitSystem) {

			final String[] distStringParts = mUnitSystem.getDistanceString(ri.getLengthMeters(), null);
			final String distString = distStringParts[UnitSystem.DISTSTRINGS_DIST_ID] + distStringParts[UnitSystem.DISTSTRINGS_UNIT_ID];
			this.mDistance = distString;

            if (ri.getDurationSeconds() > 0) {
                this.mTime = ri.getDurationSeconds() + "sec";
            } else {
                this.mTime = "";
            }

            this.mDescription = ri.getDescription();

            final int turnAngle = (int)ri.getAngle();
            if(turnAngle > 60) {
                this.mTurnAngle = BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_left_90);
            } else if(turnAngle > 35) {
                this.mTurnAngle = BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_left_45);
            } else if(turnAngle > 15) {
                this.mTurnAngle = BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_left_25);
            } else if(turnAngle <= 15 && turnAngle >= -15) {
                this.mTurnAngle = BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_straight);
            } else if(turnAngle > -35) {
                this.mTurnAngle = BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_right_25);
            } else if(turnAngle > -60) {
                this.mTurnAngle = BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_right_45);
            } else {
                this.mTurnAngle = BitmapFactory.decodeResource(context.getResources(), R.drawable.turn_right_90);
            }
		}

        public void recycle() {
            this.mTurnAngle.recycle();
        }

		// ===========================================================
		// Parcelable
		// ===========================================================

		public static final Parcelable.Creator<RouteInstructionItem> CREATOR = new Parcelable.Creator<RouteInstructionItem>() {
			public RouteInstructionItem createFromParcel(final Parcel in) {
				return readFromParcel(in);
			}

			public RouteInstructionItem[] newArray(final int size) {
				return new RouteInstructionItem[size];
			}
		};

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(final Parcel out, final int flags) {
			out.writeString(this.mDistance);
			out.writeString(this.mTime);
			out.writeString(this.mDescription);
            out.writeParcelable(this.mTurnAngle, 0);
		}

		private static RouteInstructionItem readFromParcel(final Parcel in){
            final String mDistance = in.readString();
            final String mTime = in.readString();
            final String mDescription = in.readString();
			final Bitmap mTurnAngle = in.readParcelable(null);

            return new RouteInstructionItem(mDistance, mTime, mDescription, mTurnAngle);
		}
	}

	private class RouteInstructionListItemView extends LinearLayout{

        private final UnitSystem mUnitSystem;

		private final TextView mTVDescription;
		private final TextView mTVDistance;
		private final TextView mTVTime;

		public RouteInstructionListItemView(final Context context, final RouteInstructionItem aRI, final UnitSystem unitSystem) {
			super(context);
            this.mUnitSystem = unitSystem;

			this.setOrientation(HORIZONTAL);


			this.mTVDescription = new TextView(context);
			this.mTVDescription.setText(aRI.mDescription);
			this.mTVDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);

			addView(this.mTVDescription, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

			this.mTVDistance = new TextView(context);
			this.mTVDistance.setText(aRI.mDistance);
			this.mTVDistance.setTextSize(TypedValue.COMPLEX_UNIT_PX, 16);
			this.mTVDistance.setPadding(10,0,20,0);

			addView(this.mTVDistance, new LinearLayout.LayoutParams(90, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

			this.mTVTime = new TextView(context);
			this.mTVTime.setText(aRI.mTime);
			this.mTVTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, 16);
			this.mTVTime.setPadding(10,0,20,0);

			addView(this.mTVTime, new LinearLayout.LayoutParams(90, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		}

	}

	private class RouteInstructionsListAdapter extends BaseAdapter{

		/** Remember our context so we can use it when constructing views. */
		private final Context mContext;
        private final UnitSystem mUnitSystem;

		private List<RouteInstructionItem> mItems = new ArrayList<RouteInstructionItem>();

		public RouteInstructionsListAdapter(final Context context, final UnitSystem unitSystem) {
			this.mContext = context;
            this.mUnitSystem = unitSystem;
		}

		public void addItem(final RouteInstructionItem it) {
			this.mItems.add(it);
		}

		@Override
		public boolean isEmpty() {
			return this.mItems == null || this.mItems.size() == 0;
		}

		public void setListItems(final List<RouteInstructionItem> lit) {
			this.mItems = lit;
		}

		/** @return The number of items in the */
		public int getCount() { return this.mItems.size(); }

		public Object getItem(final int position) { return this.mItems.get(position); }

		@Override
		public long getItemId(final int position) { return position; }

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			RouteInstructionListItemView btv;
			if (convertView == null) {
				btv = new RouteInstructionListItemView(this.mContext, this.mItems.get(position), mUnitSystem);
			} else { // Reuse/Overwrite the View passed
				// We are assuming(!) that it is castable!
				btv = (RouteInstructionListItemView) convertView;
				btv.mTVDescription.setText(this.mItems.get(position).mDescription);
				btv.mTVDistance.setText(this.mItems.get(position).mDistance);
				btv.mTVTime.setText(this.mItems.get(position).mTime);
			}
			return btv;
		}

	}

}
