package com.example.keymapdemo;



import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;


/**
 * KeycodeFragment is used by RemapActivity, to remap into Intents.
 * 
 */
public class StartActivityFragment extends Fragment {

	/**
	 * The handler in RemapActivity to send notification this fragment is ready
	 * to use.  One parent activity is assumed.
	 */
	private static Handler handler;

	/**
	 * @param handler
	 *            The handler in RemapActivity to send notification this
	 *            fragment is ready to use. One parent activity is assumed.
	 * @return An instance of StartActivityFragment.
	 */
	public static StartActivityFragment getInstance(Handler handler) {
		StartActivityFragment.handler = handler;
		return new StartActivityFragment();
	}

	public StartActivityFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_start_activity, container,
				false);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Button remapActivitySelect = (Button) getView().findViewById(
				R.id.remapActivitySelect);
		TextView remapActivityInfo = (TextView) getView().findViewById(
				R.id.remapActivityInfo);
		ImageView remapActivityIcon = (ImageView) getView().findViewById(
				R.id.remapActivityIcon);

		// Notify view is ready only if child views are available
		if ((remapActivitySelect != null) && (remapActivityInfo != null) && (remapActivityIcon != null)) {
			Message msg = handler
					.obtainMessage(RemapActivity.HANDLE_INTENT_VIEWS);
			Object[] objs = {remapActivitySelect, remapActivityInfo, remapActivityIcon};
			msg.obj = objs;
			handler.sendMessage(msg);
		}
	}
}
