
package com.example.keymapdemo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import android.os.Build;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.device.KeyMapManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextWatcher;
import android.text.Editable;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.ActivityInfo;

/**
 * RemapActivity remaps scanCodes to keyCodes, unicodes, or intents.
 * KeycodeFragment, UnicodeFragment, and StartActivityFragment are used to remap
 * to one specific type.
 */
public class RemapActivity extends Activity {
    private final static String TAG = "#RemapActivity#";

    public final static int HANDLE_KEY_VIEWS = 1;

    public final static int HANDLE_UNICODE_VIEWS = 2;

    public final static int HANDLE_INTENT_VIEWS = 3;

    public final static int HANDLE_REMOVAL = 4;

    private static final int REMAP_ACTIVITY_PICKER = 1;

    private boolean isRemap = true;

    private boolean sortingDone = false;

    private KeyEvent currentKeyEvent = null;

    private SorterTask mTask = null;

    boolean isInterception = true;

    private Spinner spinMapType;

    private Spinner spinMapScancode;

    private Button mRemapBtn;

    private Button mBackBtn;

    private TextView txtRemapScansInput = null;

    private TextView txtRemapKeyInput = null;

    // One instance of each fragment will generally exist through the life of
    // this activity.
    private KeycodeFragment kf = null;

    private StartActivityFragment af = null;

    private Handler handler = null;

    /**
     * Used to invalidate remapping until a fragment is usable.
     */
    private Boolean isViewViewable = false;

    /**
     * Used to remap to a different Android keycode
     */
    private Spinner spinToKey = null;

    /**
     * Used to remap to a specific Unicode char
     */
    private EditText txtUnicode = null;

    /**
     * Used to remap to a specific application using intents
     */
    private Button remapActivitySelect = null;

    private TextView remapActivityInfo = null;

    private ImageView remapActivityIcon = null;

    Intent startActivityIntent = null;

    /* keyNames and metaNames have 1-1 association with their given values */
    private ArrayList<String> keyNames = null;

    private KeyMapManager mKeyMap = null;

    ArrayAdapter<String> keyAdapter;

    HashMap<String, Integer> keyMap;

    private Context mContext = null;

    String[] scanCodeArray = {
            "KEYCODE_UNKNOWN", "KEYCODE_BACK", "KEYCODE_HOME", "KEYCODE_1"
    };

    int[] keycode = new int[] {
            KeyEvent.KEYCODE_UNKNOWN, KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_HOME,
            KeyEvent.KEYCODE_1
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remap);

        mContext = this.getApplicationContext();
        mKeyMap = new KeyMapManager(getApplicationContext());
        spinMapScancode = (Spinner) findViewById(R.id.spinMapScancode);
        ArrayAdapter<String> scanCodeAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_list_item, scanCodeArray) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = null;

                // If this is the initial dummy entry, make it hidden
                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    return tv;
                }

                // Pass convertView as null to prevent reuse of special case
                // views
                return super.getDropDownView(position, null, parent);
            }
        };
        spinMapScancode.setAdapter(scanCodeAdapter);
        spinMapScancode.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (position > 0) {
                    long now = SystemClock.uptimeMillis();
                    interceptionKeyEvent(keycode[position], new KeyEvent(now, now,
                            KeyEvent.ACTION_DOWN, keycode[position], 0, 0,
                            KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, InputDevice.SOURCE_KEYBOARD));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
        spinMapType = (Spinner) findViewById(R.id.spinMapType);
        String[] typeArray = {
                "Remap type", "Keycode", "Start activity"
        };
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_list_item, typeArray) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = null;

                // If this is the initial dummy entry, make it hidden
                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    return tv;
                }

                // Pass convertView as null to prevent reuse of special case
                // views
                return super.getDropDownView(position, null, parent);
            }
        };
        spinMapType.setAdapter(typeAdapter);
        spinMapType.setVisibility(View.GONE);

        handler = new Handler(new ViewCreatedHandler());
        kf = KeycodeFragment.getInstance(handler);

        af = StartActivityFragment.getInstance(handler);

        spinMapType.setOnItemSelectedListener(new MapTypeListener());

        txtRemapScansInput = (TextView) findViewById(R.id.txtRemapScansInput);
        txtRemapKeyInput = (TextView) findViewById(R.id.txtRemapKeyInput);
        txtRemapKeyInput.setVisibility(View.GONE);

        mRemapBtn = (Button) findViewById(R.id.btnRemap);
        mRemapBtn.setEnabled(false);
        mRemapBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                remapping();
            }
        });

        mBackBtn = (Button) findViewById(R.id.btnBack);
        mBackBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isInterception == true) {
                    mKeyMap.disableInterception(true);
                }
                RemapActivity.this.finish();
            }
        });
    }

    public void finish() {
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mTask == null) {
            mTask = new SorterTask();
            mTask.execute();
        }
        // Views inside fragments are changing.
        synchronized (isViewViewable) {
            isViewViewable = true;
        }
        isInterception = mKeyMap.isInterception();
        if (isInterception == true) {
            mKeyMap.disableInterception(false);
        }

    }

    @Override
    protected void onPause() {
        if (isInterception == true) {
            mKeyMap.disableInterception(true);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Initializes keyNames, metaNames, keyAdapter, and metaAdapter. Keys are
     * taken from KeyEvent fields starting with KEYCODE, and metas are taken
     * from KeyEvent fields starting with META. --Note--keyPopulate is assumed
     * to be called only once, so this method is not optimized for multiple
     * calls.--
     */
    private void keyPopulate() {
        Field[] keyFields = KeyEvent.class.getDeclaredFields();
        keyMap = new HashMap<String, Integer>();

        String tmpName = null;
        try {
            for (int i = 0; i < keyFields.length; i++) {
                tmpName = keyFields[i].getName();
                int modi = keyFields[i].getModifiers();
                if (Modifier.isStatic(modi) && Modifier.isPublic(modi)) {
                    if (tmpName.startsWith("KEYCODE_")) {
                        int keycode = (Integer) keyFields[i].get(null);
                        if (keycode > 0 && keycode <= KeyEvent.KEYCODE_F12) {
                            tmpName = tmpName.replace("KEYCODE_", "");
                            keyMap.put(tmpName, (Integer) keyFields[i].get(null));
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "Non-static field : " + tmpName);
        } catch (IllegalArgumentException e1) {
            Log.w(TAG, "Type mismatch : " + tmpName);
        } catch (IllegalAccessException e2) {
            Log.w(TAG, "Non-public field : " + tmpName);
        }

        keyNames = new ArrayList<String>(keyMap.keySet());
        Collections.sort(keyNames);

        keyAdapter = new ArrayAdapter<String>(RemapActivity.this, R.layout.spinner_list_item,
                keyNames);

    }

    /**
     * Sets the adapter for new/current spinToKey, and spinToMeta.
     */
    private void adaptKeys() {
        spinToKey.setAdapter(keyAdapter);
    }

    /**
     * Activated by btnRemap. Only usable if a fragment has declared
     * isViewViewable to be true. Remapping from scanCode and modifiersState
     * taken from scanFields and modMap ( spinFromScan and spinFromMod selected
     * items are indices ). What is mapped to depends on spinMapType selection,
     * and the viewable fragment.
     */
    public void remapping() {
        synchronized (isViewViewable) {
            if (!isViewViewable) {
                Toast.makeText(this, R.string.totast_do_remap_msg, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (isRemap) {

            boolean remapSuccess = false;

            switch (spinMapType.getSelectedItemPosition()) {
                case 1: {
                    int key = keyMap.get((String) spinToKey.getSelectedItem());
                    Log.v("Remap", "keycode " + key);
                    if (currentKeyEvent.getKeyCode() == key) {
                        Toast.makeText(getApplicationContext(), R.string.txt_remap_keycode_same,
                                Toast.LENGTH_SHORT).show();
                        remapSuccess = false;
                        break;
                    }

                    mKeyMap.mapKeyEntry(currentKeyEvent, KeyMapManager.KEY_TYPE_KEYCODE,
                            Integer.toString(key));
                    remapSuccess = true;
                    break;
                }
                case 3: {
                    break;
                }
                case 2: {
                    if (startActivityIntent != null) {
                        mKeyMap.mapKeyEntry(currentKeyEvent, KeyMapManager.KEY_TYPE_STARTAC,
                                startActivityIntent.getComponent().getPackageName());
                        remapSuccess = true;
                    }
                }
                default: {
                    Log.e(TAG, "Invalid map type selection.");
                    break;
                }
            }
            if (remapSuccess) {
                Toast.makeText(getApplicationContext(), "Success!!!", Toast.LENGTH_SHORT)
                .show();
                }
        } else {
            mKeyMap.delKeyEntry(currentKeyEvent.getScanCode());
            Toast.makeText(getApplicationContext(), R.string.txt_clear_mapping, Toast.LENGTH_SHORT)
                    .show();
            onKeyDown(currentKeyEvent.getKeyCode(), currentKeyEvent);

        }
    }

    /**
     * Changes the viewable fragment when spinMapType selection is changed.
     */
    public class MapTypeListener implements OnItemSelectedListener {
        /*
         * String[] typeArray = { "", "Keycode", "Unicode", "Start activity" };
         */
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            FragmentManager fm = getFragmentManager();

            switch (position) {
                case 0:
                    break;
                case 1: {
                    fm.beginTransaction().replace(R.id.fragment_container, kf).commit();
                    break;
                }
                case 3: {
                    break;
                }
                case 2: {
                    fm.beginTransaction().replace(R.id.fragment_container, af).commit();
                    break;
                }
                default: {
                    onNothingSelected(parent);
                    break;
                }
            }
            synchronized (isViewViewable) {
                isViewViewable = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            parent.setSelection(0);
        }
    }

    /**
     * Handle a fragment becoming viewable. handleMessage initializes references
     * to child views, which are passed as the object in the message. In the
     * case of mapping to keycodes, the object is Spinner[2], where obj[0] is
     * spinToKey, and obj[1] is spinToMeta.
     */
    public class ViewCreatedHandler implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            synchronized (mTask) {
                while (!sortingDone) {
                    try {
                        mTask.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

            synchronized (isViewViewable) {

                switch (msg.what) {
                    case HANDLE_KEY_VIEWS: {
                        Spinner views = (Spinner) msg.obj;
                        spinToKey = views;
                        isRemap = true;
                        mRemapBtn.setText(R.string.input_remap);
                        mRemapBtn.setEnabled(true);
                        isViewViewable = true;
                        adaptKeys();
                        break;
                    }
                    case HANDLE_UNICODE_VIEWS: {
                        txtUnicode = (EditText) msg.obj;
                        isRemap = true;
                        mRemapBtn.setText(R.string.input_remap);
                        mRemapBtn.setEnabled(txtUnicode.getText().length() == 1);
                        txtUnicode.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before,
                                    int count) {
                                if (s.length() == 1) {
                                    mRemapBtn.setEnabled(true);
                                } else {
                                    mRemapBtn.setEnabled(false);
                                }
                            }

                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count,
                                    int after) {
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

                        isViewViewable = true;
                        break;
                    }
                    case HANDLE_INTENT_VIEWS: {
                        Object[] objs = (Object[]) msg.obj;
                        remapActivitySelect = (Button) objs[0];
                        remapActivityInfo = (TextView) objs[1];
                        remapActivityIcon = (ImageView) objs[2];

                        remapActivitySelect.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent pickerIntent = new Intent(
                                        "android.intent.action.PICK_ACTIVITY");
                                Intent remapActivity = new Intent("android.intent.action.MAIN",
                                        null);
                                remapActivity.addCategory("android.intent.category.LAUNCHER");
                                pickerIntent.putExtra("android.intent.extra.INTENT", remapActivity);
                                startActivityForResult(pickerIntent, REMAP_ACTIVITY_PICKER);
                            }
                        });
                        isRemap = true;
                        mRemapBtn.setText(R.string.input_remap);
                        mRemapBtn.setEnabled(startActivityIntent != null);
                        setActivityInfo();
                        isViewViewable = true;
                        break;
                    }
                    case HANDLE_REMOVAL: {
                        isRemap = false;
                        mRemapBtn.setEnabled(true);
                        mRemapBtn.setText(R.string.input_unmap);
                        isViewViewable = true;
                        break;
                    }
                    default: {
                        // isViewViewable has no change
                        // isViewViewable = isViewViewable ;
                        Log.w(TAG, "Unknown handle message type.");
                    }
                }
            }
            return true;
        }

    }

    private void setActivityInfo() {
        PackageManager packagemanager = getPackageManager();
        ApplicationInfo app = null;
        String packageName = null;

        if (startActivityIntent != null) {
            packageName = startActivityIntent.getComponent().getPackageName();

            try {
                app = packagemanager.getApplicationInfo(packageName, 0);
            } catch (final PackageManager.NameNotFoundException e) {
            }
        }

        if (app != null) {
            remapActivityInfo.setText(packageName);
            remapActivityIcon.setImageDrawable(packagemanager.getApplicationIcon(app));
        } else {
            remapActivityInfo.setText(getString(R.string.txt_start_activity_noapp));
            remapActivityIcon.setImageDrawable(getResources().getDrawable(R.drawable.empty_icon));
        }
    }

    private boolean interceptionKeyEvent(int keyCode, KeyEvent event) {
        currentKeyEvent = event;

        mRemapBtn.setText(R.string.input_remap);
        mRemapBtn.setEnabled(false);

        if (mKeyMap.hasKeyEntry(event.getScanCode())) {
            txtRemapScansInput.setText(getString(R.string.txt_remap_scans_input_used));
            txtRemapKeyInput.setVisibility(View.GONE);

            // provide a mechanism to remove the mapping
            Message msg = handler.obtainMessage();
            msg.what = RemapActivity.HANDLE_REMOVAL;
            handler.sendMessage(msg);
            mRemapBtn.setText(R.string.input_unmap);
            spinMapType.setVisibility(View.GONE);
            return true;
        } else {
            txtRemapScansInput.setText("Scancode: " + event.getScanCode());
            txtRemapKeyInput.setVisibility(View.VISIBLE);
            spinMapType.setVisibility(View.VISIBLE);
            return true;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // TODO Auto-generated method stub
        Log.v(TAG, "dispatchKeyEvent " + event);
        interceptionKeyEvent(event.getKeyCode(), event);
        return true;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v(TAG, "onKeyDown keycode " + keyCode);
        return interceptionKeyEvent(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.v(TAG, "onKeyUp keycode " + keyCode);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REMAP_ACTIVITY_PICKER:
                if (resultCode == RESULT_OK) {
                    startActivityIntent = data;
                    setActivityInfo();
                    mRemapBtn.setEnabled(true);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class SorterTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            keyPopulate();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            synchronized (mTask) {
                sortingDone = true;
                mTask.notifyAll();
            }
        }

    }

}
