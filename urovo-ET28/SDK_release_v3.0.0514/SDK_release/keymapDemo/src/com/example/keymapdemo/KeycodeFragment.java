package com.example.keymapdemo;



import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

/**
 * KeycodeFragment is used by RemapActivity, to remap into Keycodes.
 * 
 * 
 */
public class KeycodeFragment extends Fragment {

	/**
	 * The handler in RemapActivity to send notification this fragment is ready
	 * to use. One parent activity is assumed.
	 */
	private static Handler handler = null;

	/**
	 * @param handler
	 *            The handler in RemapActivity to send notification this
	 *            fragment is ready to use. One parent activity is assumed.
	 * @return An instance of KeycodeFragment.
	 */
	public static KeycodeFragment getInstance(Handler handler) {
		KeycodeFragment.handler = handler;
		return new KeycodeFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_keycode, container, false);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Spinner spinToKey = (Spinner) getView().findViewById(R.id.spinToKey);

		// Notify view is ready only if child views are available
		if (spinToKey != null ) {
			Message msg = handler.obtainMessage();
			msg.what = RemapActivity.HANDLE_KEY_VIEWS;
			msg.obj = spinToKey;
			handler.sendMessage(msg);
		}
	}

}
