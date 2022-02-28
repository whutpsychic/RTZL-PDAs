package com.example.keymapdemo;

import com.example.keymapdemo.R;

import android.content.Intent;
import android.device.KeyMapManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class KeyboardSettings extends PreferenceActivity {
	private CheckBoxPreference mEnableIntercept;
	private Preference mkey_remapping;
	private Preference mviewer_remapped_keys;
	private KeyMapManager mKeyMap = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.keyboard_settings);
		mEnableIntercept = (CheckBoxPreference) getPreferenceScreen().findPreference(
				"disable_intercept");
		mkey_remapping = getPreferenceScreen().findPreference("key_remapping");
		mviewer_remapped_keys = getPreferenceScreen().findPreference(
				"viewer_remapped_keys");
		mKeyMap = new KeyMapManager(getApplicationContext());
	}
	@Override
	public void onResume() {
		super.onResume();
		mEnableIntercept.setChecked(mKeyMap.isInterception());
	}
	@Override
	@Deprecated
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		if (preference == mkey_remapping) {
			Intent intent = new Intent("android.Intent.action.KEY_REMAP_DEMO");
			startActivity(intent);
		} else if (mEnableIntercept == preference) {
			mKeyMap.disableInterception(mEnableIntercept.isChecked());
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);

	}
}
