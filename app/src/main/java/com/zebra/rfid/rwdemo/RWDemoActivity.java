/**
 * Copyright (C) 2019 Zebra Technologies Corporation and/or its affiliates.
 * All rights reserved.
 */
package com.zebra.rfid.rwdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.zebra.rfid.rwdemo.RWDemoIntentParams.GET_ACTIVE_PROFILE;
import static com.zebra.rfid.rwdemo.RWDemoIntentParams.RESULT_ACTION;
import static com.zebra.rfid.rwdemo.RWDemoIntentParams.RESULT_GET_ACTIVE_PROFILE;


/**
 * RWDemoActivity is  a demo application that designed to read RFID Tags.
 * To read RFID tags RWDemoActivity uses intent API of a DataWedge Application.
 */
public class RWDemoActivity extends Activity implements OnClickListener,    OnMenuItemClickListener {

    private static final String TAG = RWDemoActivity.class.getSimpleName();

    private static final String SOURCE_TAG = "com.symbol.datawedge.source";
    private static final String DATA_STRING_TAG = "com.symbol.datawedge.data_string";
    private static final String MSR_DATA_TAG = "com.symbol.datawedge.msr_data";

    private static final String DWAPI_START_SCANNING = "START_SCANNING";
    private static final String DWAPI_STOP_SCANNING = "STOP_SCANNING";
    private static final String DWAPI_TOGGLE_SCANNING = "TOGGLE_SCANNING";

    private static final String DATA_INTENT = "StartingIntent";
    private static final String DATA_TXT = "UIText";
    private final static String SCROLL_VIEW_UPDATE_THREAD_NAME = "RWDenoscollViewUpdateThread";
    private static final int VIEW_UPDATE_TIME_IN_MILLISECONDS = 100;
    private static final int BACKGROUND_GREY_COLOR_CODE = 0xffbfc7cc;

    private static final String SOURCE_SCANNER = "scanner";
    private static final String SOURCE_MSR = "msr";
    private static final String DATA_MSR = "[encrypted MSR data]";
    private static final String ENDLINE_CHAR = "\n";

    private static final String COMMAND_ID = "COMMAND_IDENTIFIER";
    private static boolean scanState = false;


    public static boolean DEBUG = false;

    private static float disabledButtonAlphaVlue = 0.2f;
    private static float enabledButtonAlphaVlue = 1f;
    private static Context thisContext = null;
    private static boolean rwDemoProfileActivated = false;
    private static ProgressBar progressBar;
    private static final int PROGRESS_BAR_WIDTH = 50;
    private static final int PROGRESS_BAR_HIGHT = 50;

    private String tvText = "";
    private ImageButton softScanTrigger;
    private ImageButton actionBtnClear;
    private ImageButton actionBtnReaderSelection;
    private ImageButton actionBtnFriendlyProfiles;
    private ImageButton actionBtnSettings;

    private static int DW_DEMO_POPUP_MENU_SETTINGS = 1;

    private Intent mDataIntent = null;
    private boolean onNewIntentToOnResume;
    private long clickTime = 0;


    private void createProfile() {

        Bundle setConfigBundle = new Bundle();
        setConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_PROFILE_NAME_KEY, RWDemoIntentParams.BUNDLE_EXTRA_PROFILE_NAME_VAL);
        setConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_CONFIG_MODE_KEY, RWDemoIntentParams.BUNDLE_EXTRA_CONFIG_MODE_VAL);
        setConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_PROFILE_ENABLED_KEY, RWDemoIntentParams.BUNDLE_EXTRA_PROFILE_ENABLED_VAL);
        setConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_KEY, RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_VAL);

        // Associate profile with this app
        Bundle appConfig = new Bundle();
        appConfig.putString(RWDemoIntentParams.BUNDLE_EXTRA_PACKAGE_NAME_KEY, getPackageName());
        appConfig.putStringArray(RWDemoIntentParams.BUNDLE_EXTRA_ACTIVITY_LIST_KEY, RWDemoIntentParams.BUNDLE_EXTRA_ACTIVITY_LIST_VAL_ARR);
        setConfigBundle.putParcelableArray(RWDemoIntentParams.BUNDLE_EXTRA_APP_LIST_KEY, new Bundle[]{appConfig});
        setConfigBundle.remove(RWDemoIntentParams.BUNDLE_EXTRA_PLUGIN_CONFIG);

        // RFID Input configurations
        Bundle rfidConigParamList = new Bundle();
        rfidConigParamList.putString(RWDemoIntentParams.PLUGIN_ENABLE_PARAM_KEY, RWDemoIntentParams.PLUGIN_ENABLE_PARAM_VAL);

        Bundle rfidConfigBundle = new Bundle();
        rfidConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_PLUGIN_NAME, RWDemoIntentParams.PLUGIN_NAME_RFID);
        rfidConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_KEY, RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_VAL);
        rfidConfigBundle.putBundle(RWDemoIntentParams.BUNDLE_EXTRA_PARAM_LIST, rfidConigParamList);

        Bundle rfidFormattingConfigBundle = new Bundle();
        rfidFormattingConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_PLUGIN_NAME, RWDemoIntentParams.PLUGIN_NAME_RFID_F);
        rfidFormattingConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_OUTPUT_PLUGIN_NAME, RWDemoIntentParams.PLUGIN_NAME_INTENT);
        rfidFormattingConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_KEY, RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_VAL);

        // Configure intent output for captured data to be sent to this app
        Bundle intentConfig = new Bundle();
        intentConfig.putString(RWDemoIntentParams.BUNDLE_EXTRA_PLUGIN_NAME, RWDemoIntentParams.PLUGIN_NAME_INTENT);
        intentConfig.putString(RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_KEY, RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_VAL);
        Bundle intentProps = new Bundle();
        intentProps.putString(RWDemoIntentParams.INTENT_OUTPUT_ENABLED_KEY, RWDemoIntentParams.INTENT_OUTPUT_ENABLED_VALUE);
        intentProps.putString(RWDemoIntentParams.INTENT_ACTION_KEY, RWDemoIntentParams.INTENT_ACTION_VALUE);
        intentProps.putString(RWDemoIntentParams.INTENT_CATEGORY_KEY, RWDemoIntentParams.INTENT_CATEGORY_VALUE);
        intentProps.putString(RWDemoIntentParams.INTENT_DELIVERY_KEY, RWDemoIntentParams.INTENT_DELIVERY_VALUE);
        intentConfig.putBundle(RWDemoIntentParams.BUNDLE_EXTRA_PARAM_LIST, intentProps);

        // Configure KeyStroke for captured data to be sent to this app
        Bundle keyStrokeConfig = new Bundle();
        keyStrokeConfig.putString(RWDemoIntentParams.BUNDLE_EXTRA_PLUGIN_NAME, RWDemoIntentParams.PLUGIN_NAME_KEYSTROKE);
        keyStrokeConfig.putString(RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_KEY, RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_VAL);
        Bundle keyStrokeProps = new Bundle();
        keyStrokeProps.putString(RWDemoIntentParams.KEYSTROKE_ENABLED_KEY, RWDemoIntentParams.KEYSTROKE_ENABLED_VALUE);
        keyStrokeConfig.putBundle(RWDemoIntentParams.BUNDLE_EXTRA_PARAM_LIST, keyStrokeProps);


        ArrayList<Parcelable> configBundles = new ArrayList<>();
        configBundles.add(rfidConfigBundle);
        configBundles.add(rfidFormattingConfigBundle);
        configBundles.add(intentConfig);
        configBundles.add(keyStrokeConfig);

        setConfigBundle.putParcelableArrayList(RWDemoIntentParams.BUNDLE_EXTRA_PLUGIN_CONFIG, configBundles);

        Intent intent = new Intent();
        intent.setAction(RWDemoIntentParams.ACTION);
        intent.putExtra(RWDemoIntentParams.ACTION_EXTRA_SET_CONFIG, setConfigBundle);
        sendBroadcast(intent);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntentToOnResume = false;
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null) {//data intent
                String data = intent.getStringExtra(DATA_STRING_TAG);
                if (data != null)
                    mDataIntent = intent;
            }
            tvText = "";
        } else {
            mDataIntent = savedInstanceState.getParcelable(DATA_INTENT);
            tvText = savedInstanceState.getString(DATA_TXT);
        }

        setContentView(R.layout.dwdemo_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setMainBackground();

        TextView tv = (TextView) findViewById(R.id.output_view);
        tv.setText(tvText);
        scrollDown();


        softScanTrigger = (ImageButton) findViewById(R.id.softscanbutton);
        softScanTrigger.setOnClickListener(v -> {
            Log.d(TAG, "onClick softScanTrigger pressed");
            if ((SystemClock.elapsedRealtime() - clickTime) < 1000) {
                return;
            }
            clickTime = SystemClock.elapsedRealtime();
            if (v.getId() == softScanTrigger.getId()) {
                toggleSoftScanTrigger();
            }
        });

        actionBtnClear = (ImageButton) findViewById(R.id.actionButton1);
        actionBtnClear.setOnClickListener(v -> {
            if (v.getId() == actionBtnClear.getId()) {
                TextView tv1 = (TextView) findViewById(R.id.output_view);
                tv1.setText("");
                tvText = "";
            }

        });

        actionBtnReaderSelection = (ImageButton) findViewById(R.id.actionButton2);
        actionBtnReaderSelection.setImageResource(R.drawable.rfid_icon_s);
        actionBtnReaderSelection.setEnabled(false);

        actionBtnFriendlyProfiles = (ImageButton) findViewById(R.id.actionButton3);
        actionBtnFriendlyProfiles.setOnClickListener(this);

        actionBtnSettings = (ImageButton) findViewById(R.id.actionButton4);
        actionBtnSettings.setOnClickListener(v -> {
            if (v.getId() == actionBtnSettings.getId()) {
                showPopupMenu(v, RWDemoActivity.DW_DEMO_POPUP_MENU_SETTINGS);
            }

        });

        thisContext = this;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mDataIntent != null) {
            savedInstanceState.putParcelable(DATA_INTENT, mDataIntent);
        }
        savedInstanceState.putString(DATA_TXT, tvText);
        super.onSaveInstanceState(savedInstanceState);
        onNewIntentToOnResume = false;
    }


    @Override
    public void onResume() {
        super.onResume();
        rwDemoProfileActivated = false;

        if(!onNewIntentToOnResume){
            softScanTrigger.setEnabled(false);
            softScanTrigger.setAlpha(disabledButtonAlphaVlue);

            RelativeLayout progressBarLayout = findViewById(R.id.relativeLayout1);
            progressBarLayout.setVisibility(View.VISIBLE);
            progressBar = new ProgressBar(RWDemoActivity.this,null,android.R.attr.progressBarStyleSmall);
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(PROGRESS_BAR_WIDTH,PROGRESS_BAR_HIGHT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            progressBarLayout.addView(progressBar,params);
            setContentView(progressBarLayout);

        }
        registerReceivers();

        // Get available profiles via intent api
        Intent intentProfGet = new Intent();
        intentProfGet.setAction(RWDemoIntentParams.ACTION);
        intentProfGet.putExtra(RWDemoIntentParams.GET_AVAILABLE_PROFILE_LIST, "");
        this.sendBroadcast(intentProfGet);


        Intent i = getIntent();
        if (mDataIntent != null) {
            i = mDataIntent;
        }
        if (i != null) {
            handleDecodeData(i);
        }

        // Fail safe mechanism to find the profile is successfully
        // switched to the RWDemo profile.
        final int softButtonUnfreezeDelayInMillisecnds = 1000;

        if(!onNewIntentToOnResume){
            rwDemoProfileActivated = false;
            new Thread(() -> {
                while(!rwDemoProfileActivated) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(RWDemoIntentParams.ACTION);
                        intent.putExtra(GET_ACTIVE_PROFILE, "");
                        thisContext.sendBroadcast(intent);

                        Thread.sleep(softButtonUnfreezeDelayInMillisecnds);
                    } catch (Exception e) {
                        if (DEBUG)
                            e.printStackTrace();
                    }
                }
            }).start();
        }

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();

        // These things should happen regardless of unregistering success or not.
        rwDemoProfileActivated = true;
        progressBar.setVisibility(View.GONE);

        try {
            //Un-register broadcast receiver when you receive profile list broadcast
            unregisterReceiver(datawedgeBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            //if receiver already unregistered
            if (DEBUG) {
                Log.d(TAG, e.getMessage() + "");
            }
        } catch (Exception e){
            if (DEBUG) {
                Log.d(TAG, e.getMessage() + "");
            }
        }

        System.gc();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(scanState == true) {
            toggleSoftScanTrigger();
        }
        System.gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            AlertDialog builder;
            try {
                builder = AboutDialogBuilder.create(this);
                builder.show();
            } catch (Exception e) {
                if (DEBUG) {
                    Log.d(TAG, e.toString());
                }
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            View v = findViewById(R.id.actionButton4);
            showPopupMenu(v, RWDemoActivity.DW_DEMO_POPUP_MENU_SETTINGS);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        //ImageButton ib = (ImageButton) v;
    }

    public void showPopupMenu(View v, int type) {
        if (type == RWDemoActivity.DW_DEMO_POPUP_MENU_SETTINGS) {
            setTheme(R.style.DWDemoTheme);
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.dwdemo_menu);
            popup.show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            AlertDialog ad;
            try {
                ad = AboutDialogBuilder.create(this);
                ad.show();
            } catch (Exception e) {
                if (DEBUG) {
                    Log.d(TAG, e.toString());
                }
            }
            return true;
        }
        /*else if (id == R.id.action_friendly_profiles) {
            try {
                Intent friendlyProfileIntent = new Intent(RWDemoActivity.this, FriendlyProfilesActivity.class);
                startActivity(friendlyProfileIntent);
                finish();
            } catch (Exception e){
                if (DEBUG) {
                    Log.d(TAG, e.toString());
                }
            }
        }*/
        return false;
    }

    private void scrollDown() {
        final Handler handler = new Handler();
        Thread scollViewUpdateThread = new Thread(() -> {
            try {
                Thread.sleep(VIEW_UPDATE_TIME_IN_MILLISECONDS);
            } catch (Exception e) {
                if (DEBUG)
                    e.printStackTrace();
            }
            handler.post(() -> {
                ScrollView sv = (ScrollView) findViewById(R.id.scrollView1);
                sv.fullScroll(View.FOCUS_DOWN);
            });
        });
        scollViewUpdateThread.setName(SCROLL_VIEW_UPDATE_THREAD_NAME);
        scollViewUpdateThread.start();
    }

    private void setMainBackground() {
        int alphaValue = 20;

        Drawable[] layers = new Drawable[2];
        Resources res = getResources();

        ShapeDrawable rect = new ShapeDrawable();
        rect.getPaint().setColor(BACKGROUND_GREY_COLOR_CODE);
        layers[0] = rect;

        Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.bg_dw_linearpattern);
        BitmapDrawable bd = new BitmapDrawable(bmp);
        bd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        bd.setAlpha(alphaValue);

        layers[1] = bd;

        LayerDrawable ld = new LayerDrawable(layers);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout1);
        rl.setBackgroundDrawable(ld);
    }

    private void toggleSoftScanTrigger() {
         Log.d(TAG,"toggleSoftScanTrigger START scanState is:" +scanState);
        Intent i = new Intent();
        i.setAction(RWDemoIntentParams.ACTION);
        if (scanState == true) {
            i.putExtra(RWDemoIntentParams.ACTION_EXTRA_SOFT_RFID_TRIGGER, DWAPI_STOP_SCANNING);
            scanState = false;
        } else {
            i.putExtra(RWDemoIntentParams.ACTION_EXTRA_SOFT_RFID_TRIGGER, DWAPI_START_SCANNING);
            scanState = true;
        }
        Log.d(TAG, "toggleSoftScanTrigger END scanState is:" + scanState);
        this.sendBroadcast(i);


    }

    @Override
    public void onNewIntent(Intent i) {
        onNewIntentToOnResume = true;
        final Intent intent = i;
        handleDecodeData(intent);
    }

    private void handleDecodeData(Intent i) {
        if (i == null)
            return;

        String data = i.getStringExtra(DATA_STRING_TAG);

        String source = i.getStringExtra(SOURCE_TAG);
        if (source == null)
            source = SOURCE_SCANNER;

        int data_len = 0;
        if (data != null)
            data_len = data.length();

        if (data_len == 0) {
            if (source.equalsIgnoreCase(SOURCE_MSR)) {
                byte[] rawData = i.getByteArrayExtra(MSR_DATA_TAG);
                if (rawData != null) {
                    data = DATA_MSR;    //convert(rawData);
                    data_len = data.length();
                }
            }
        }

        if (data_len > 0) {
            TextView tv = (TextView) findViewById(R.id.output_view);
            if (tvText.length() > 0) {
                tvText += ENDLINE_CHAR;
            }
            tvText += data;

            tv.setText(tvText);
            ScrollView sv = (ScrollView) findViewById(R.id.scrollView1);
            sv.post(() -> {
                ScrollView svr = (ScrollView) findViewById(R.id.scrollView1);
                svr.fullScroll(View.FOCUS_DOWN);
            });
        }
        setIntent(null);
        mDataIntent = null;
    }

    /**
     * AboutDialogBuilder class uses for create a about dialog box view
     * which shows description about the application and the copyright
     * information
     */
    public static class AboutDialogBuilder {
        public static AlertDialog create(Context context) {

            // Try to load the a package matching the name of our own package
            PackageInfo pInfo;
            String versionInfo = "1.0.6.0";
            int msgPadding = 5;

            String aboutTitle = context.getString(R.string.dwdemo2_about_title);
            String versionString = String.format(context.getString(R.string.dwdemo2_about_version), versionInfo);

            String aboutText = context.getString(R.string.dwdemo2_about_text);
            String copyright = context.getString(R.string.dwdemo2_about_copyright);
            String allrights = context.getString(R.string.dwdemo2_about_allrights);

            // Set up the TextView
            final TextView message = new TextView(context);
            // We'll use a spannablestring to be able to make links clickable
            final SpannableString aboutString = new SpannableString(aboutText);

            // Set some padding
            message.setPadding(msgPadding, msgPadding, msgPadding, msgPadding);

            // Set up the final string
            StringBuilder messageText = new StringBuilder();
            messageText.append(versionString);
            messageText.append(ENDLINE_CHAR);
            messageText.append(ENDLINE_CHAR);
            messageText.append(aboutString);
            messageText.append(ENDLINE_CHAR);
            messageText.append(ENDLINE_CHAR);
            messageText.append(copyright);
            messageText.append(ENDLINE_CHAR);
            messageText.append(allrights);

            message.setText(messageText);
            message.setTextColor(context.getResources().getColor(R.color.white2));

            ScrollView scrollView = new ScrollView(context);
            scrollView.addView(message, 0);

            AlertDialog.Builder adb = new AlertDialog.Builder(context);
            adb.setTitle(aboutTitle);
            adb.setCancelable(true);
            adb.setIcon(R.drawable.rfid_icon_s);
            adb.setPositiveButton(context.getString(android.R.string.ok), null);
            adb.setView(scrollView);
            return adb.create();

        }
    }

    /**
     * Sets the specified image button to the given state,
     * while "graying-out" the icon as well
     *
     * @param enabled   The state of the item
     * @param item      The item to modify
     * @param iconResId The icon ID
     */
    public static void setImageButtonEnabled(Context ctxt, boolean enabled, ImageButton item, int iconResId) {
        item.setEnabled(enabled);
        Drawable originalIcon = ctxt.getResources().getDrawable(iconResId);
        Drawable icon = enabled ? originalIcon : convertDrawableToGrayScale(originalIcon);
        item.setImageDrawable(icon);
    }

    /**
     * Mutates and applies a filter that converts the given drawable to a
     * Gray image. This method may be used to simulate the colour of
     * disable icons in JB ActionBar.
     *
     * @return a mutated version of the given drawable with a color filter applied.
     */
    public static Drawable convertDrawableToGrayScale(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Drawable res = drawable.mutate();
        res.setColorFilter(Color.GRAY, Mode.SRC_IN);
        return res;
    }

    /**
     * Enables the soft button when the DW profile is switched to RWDemo.
     * Receiving the result for get the available profiles on datawedge
     * through intent API. If the RWDemo profile is not among them
     * create the RWDemo profile
     */
    private BroadcastReceiver datawedgeBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(RESULT_GET_ACTIVE_PROFILE)) {
                if(intent.getExtras().getString(RESULT_GET_ACTIVE_PROFILE).equals(RWDemoIntentParams.BUNDLE_EXTRA_PROFILE_NAME_VAL)) {
                    rwDemoProfileActivated = true;
                    softScanTrigger.setEnabled(true);
                    softScanTrigger.setAlpha(enabledButtonAlphaVlue);
                    progressBar.setVisibility(View.GONE);
                    scanState = false;
                }
            }

            if (intent.hasExtra(RWDemoIntentParams.GET_AVAILABLE_PROFILE_LIST_RESULT)) {
                String[] profiles = intent.getExtras().getStringArray(RWDemoIntentParams.GET_AVAILABLE_PROFILE_LIST_RESULT);

                // Converts array to string list for easy processing
                List<String> profileList = Arrays.asList(profiles);

                if(profiles == null || !profileList.contains(RWDemoIntentParams.BUNDLE_EXTRA_PROFILE_NAME_VAL)) {
                    // Creates profile if the RWDemo profile is not among the list
                    createProfile();
                }
            }
        }
    };

    /**
     * Dynamically register broadcast receiver
     */
    private void registerReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(RESULT_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(datawedgeBroadcastReceiver, filter);
    }

}
