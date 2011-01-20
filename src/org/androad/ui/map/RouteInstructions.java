package org.androad.ui.map;

import java.util.List;

import org.androad.R;
import org.androad.sys.ors.adt.rs.Route;
import org.androad.sys.ors.adt.rs.RouteInstruction;
import org.androad.ui.AndNavBaseActivity;
import org.androad.ui.common.OnClickOnFocusChangedListenerAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);

		this.setContentView(R.layout.map_routeinstructions_list);

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

        final RouteInstructionsListAdapter rla = new RouteInstructionsListAdapter(this);
        rla.setListItems(this.mRoute.getRouteInstructions());
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


	private class RouteInstructionListItemView extends LinearLayout{

		private final TextView mTVDescription;
		private final TextView mTVDistance;

		public RouteInstructionListItemView(final Context context, final RouteInstruction aRouteInstruction) {
			super(context);

			this.setOrientation(HORIZONTAL);

			this.mTVDistance = new TextView(context);
			this.mTVDistance.setText(aRouteInstruction.getLengthMeters() + "m");
			this.mTVDistance.setTextSize(TypedValue.COMPLEX_UNIT_PX, 16);
			this.mTVDistance.setPadding(10,0,20,0);

			addView(this.mTVDistance, new LinearLayout.LayoutParams(90, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));


			this.mTVDescription = new TextView(context);
			this.mTVDescription.setText(aRouteInstruction.getDescription());
			this.mTVDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);

			addView(this.mTVDescription, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		}

	}

	private class RouteInstructionsListAdapter extends BaseAdapter{

		/** Remember our context so we can use it when constructing views. */
		private final Context mContext;

		private List<RouteInstruction> mItems;

		public RouteInstructionsListAdapter(final Context context) {
			this.mContext = context;
		}

		@Override
		public boolean isEmpty() {
			return this.mItems == null || this.mItems.size() == 0;
		}

		public void setListItems(final List<RouteInstruction> lit) {
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
				btv = new RouteInstructionListItemView(this.mContext, this.mItems.get(position));
			} else { // Reuse/Overwrite the View passed
				// We are assuming(!) that it is castable!
				btv = (RouteInstructionListItemView) convertView;
				btv.mTVDescription.setText(this.mItems.get(position).getDescription());
				btv.mTVDistance.setText( this.mItems.get(position).getLengthMeters());
			}
			return btv;
		}

	}

}
