/**
 * Copyright (C) 2019 Zebra Technologies Corporation and/or its affiliates.
 * All rights reserved.
 */

package com.zebra.rfid.rwdemo;

import android.content.Intent;
import android.os.Parcelable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Shows the FriendlyProfiles view of the RWDemo application and when the profile
 * selected this will sent a broadcast intent to change RFID setting of the DataWedge app
 * accordingly the selected profile.
 */

public class FriendlyProfilesActivity extends AppCompatActivity {

    private static final String TAG = "FriendlyProfilesActivity";

    private LinearLayout fastestRead, cycleCount, denseReader, optimalBattery, balancedPerformance;

    private final static String FASTEST_READ_SESSION = "0";
    private final static String FASTEST_READ_ANTENNA_TRANSMIT_POWER = "30";
    private final static String FASTEST_READ_LINK_PROFILE = "1";
    private final static String FASTEST_READ_DYNAMIC_POWER = "false";

    private final static String CYCLE_COUNT_SESSION = "2";
    private final static String CYCLE_COUNT_ANTENNA_TRANSMIT_POWER = "30";
    private final static String CYCLE_COUNT_LINK_PROFILE = "0";
    private final static String CYCLE_COUNT_DYNAMIC_POWER = "false";

    private final static String DENSE_READER_SESSION = "1";
    private final static String DENSE_READER_ANTENNA_TRANSMIT_POWER = "30";
    private final static String DENSE_READER_LINK_PROFILE = "5";
    private final static String DENSE_READER_DYNAMIC_POWER = "false";

    private final static String OPTIMAL_BATTERY_SESSION = "1";
    private final static String OPTIMAL_BATTERY_ANTENNA_TRANSMIT_POWER = "24";
    private final static String OPTIMAL_BATTERY_LINK_PROFILE = "0";
    private final static String OPTIMAL_BATTERY_DYNAMIC_POWER = "true";

    private final static String BALANCED_PERFORMANCE_SESSION = "1";
    private final static String BALANCED_PERFORMANCE_ANTENNA_TRANSMIT_POWER = "27";
    private final static String BALANCED_PERFORMANCE_LINK_PROFILE = "0";
    private final static String BALANCED_PERFORMANCE_DYNAMIC_POWER = "true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendly_profiles);

        this.setTitle(R.string.friendly_profile_title);

        fastestRead = findViewById(R.id.fastest_read);
        cycleCount = findViewById(R.id.cycle_count);
        denseReader = findViewById(R.id.dense_reader);
        optimalBattery = findViewById(R.id.optimal_battery);
        balancedPerformance = findViewById(R.id.balanced_performance);

        fastestRead.setOnClickListener(v -> {
            createProfileWithPreset(FASTEST_READ_SESSION, FASTEST_READ_ANTENNA_TRANSMIT_POWER, FASTEST_READ_LINK_PROFILE, FASTEST_READ_DYNAMIC_POWER);
            goBack();
        });

        cycleCount.setOnClickListener(v -> {
            createProfileWithPreset(CYCLE_COUNT_SESSION, CYCLE_COUNT_ANTENNA_TRANSMIT_POWER, CYCLE_COUNT_LINK_PROFILE, CYCLE_COUNT_DYNAMIC_POWER);
            goBack();
        });

        denseReader.setOnClickListener(v -> {
            createProfileWithPreset(DENSE_READER_SESSION, DENSE_READER_ANTENNA_TRANSMIT_POWER, DENSE_READER_LINK_PROFILE, DENSE_READER_DYNAMIC_POWER);
            goBack();
        });

        optimalBattery.setOnClickListener(v -> {
            createProfileWithPreset(OPTIMAL_BATTERY_SESSION, OPTIMAL_BATTERY_ANTENNA_TRANSMIT_POWER, OPTIMAL_BATTERY_LINK_PROFILE, OPTIMAL_BATTERY_DYNAMIC_POWER);
            goBack();
        });

        balancedPerformance.setOnClickListener(v -> {
            createProfileWithPreset(BALANCED_PERFORMANCE_SESSION, BALANCED_PERFORMANCE_ANTENNA_TRANSMIT_POWER, BALANCED_PERFORMANCE_LINK_PROFILE, BALANCED_PERFORMANCE_DYNAMIC_POWER);
            goBack();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    private void goBack() {
        Intent i = new Intent(FriendlyProfilesActivity.this, RWDemoActivity.class);
        startActivity(i);
        finish();
    }


    /**
     * This method will update the DataWedge's RWDemo profile with below provided values
     * @param session session that has to associate to the RWDemo profile
     * @param antennaTransmitPower Transmit power that has to associate to the RWDemo profile
     * @param linkProfile Link profile that has to associate to the RWDemo profile
     * @param dynamicPower whether the dynamic power on or off
     */
    private void createProfileWithPreset(String session, String antennaTransmitPower, String linkProfile, String dynamicPower) {

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

        // Barcode Input configurations (Disabled)
        Bundle barcodeConfigParamList = new Bundle();
        barcodeConfigParamList.putString(RWDemoIntentParams.BARCODE_ENABLED_KEY, RWDemoIntentParams.BARCODE_ENABLED_VALUE);

        Bundle barcodeConfigBundle = new Bundle();
        barcodeConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_PLUGIN_NAME, RWDemoIntentParams.PLUGIN_NAME_BARCODE);
        barcodeConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_KEY, RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_VAL);
        barcodeConfigBundle.putBundle(RWDemoIntentParams.BUNDLE_EXTRA_PARAM_LIST, barcodeConfigParamList);

        // RFID Input configurations
        Bundle rfidConigParamList = new Bundle();
        rfidConigParamList.putString(RWDemoIntentParams.PLUGIN_ENABLE_PARAM_KEY, RWDemoIntentParams.PLUGIN_ENABLE_PARAM_VAL);
        rfidConigParamList.putString(RWDemoIntentParams.INTENT_KEY_SESSION, session);
        rfidConigParamList.putString(RWDemoIntentParams.INTENT_KEY_POWER_LEVEL, antennaTransmitPower);
        rfidConigParamList.putString(RWDemoIntentParams.INTENT_KEY_RFID_LINK_PROFILE_PARAM, linkProfile);
        rfidConigParamList.putString(RWDemoIntentParams.INTENT_KEY_RFID_DYNAMIC_POWER_MODE_ENABLED_PARAM, dynamicPower);

        Bundle rfidConfigBundle = new Bundle();
        rfidConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_PLUGIN_NAME, RWDemoIntentParams.PLUGIN_NAME_RFID);
        rfidConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_KEY, RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_VAL);
        rfidConfigBundle.putBundle(RWDemoIntentParams.BUNDLE_EXTRA_PARAM_LIST, rfidConigParamList);

        Bundle rfidFormattingConfigBundle = new Bundle();
        rfidFormattingConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_PLUGIN_NAME, RWDemoIntentParams.PLUGIN_NAME_RFID_F);
        rfidFormattingConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_OUTPUT_PLUGIN_NAME, RWDemoIntentParams.PLUGIN_NAME_INTENT);
        rfidFormattingConfigBundle.putString(RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_KEY, RWDemoIntentParams.BUNDLE_EXTRA_RESET_CONFIG_VAL);
        Bundle rfidFormattingParamList = new Bundle();
        rfidFormattingParamList.putString(RWDemoIntentParams.PLUGIN_RFID_F_ENABLE_PARAM, RWDemoIntentParams.PLUGIN_RFID_F_ENABLE_VALUE);
        rfidFormattingConfigBundle.putBundle(RWDemoIntentParams.BUNDLE_EXTRA_PARAM_LIST, rfidFormattingParamList);

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


        ArrayList<Parcelable> configBundles = new ArrayList<>();
        configBundles.add(barcodeConfigBundle);
        configBundles.add(rfidConfigBundle);
        configBundles.add(rfidFormattingConfigBundle);
        configBundles.add(intentConfig);

        setConfigBundle.putParcelableArrayList(RWDemoIntentParams.BUNDLE_EXTRA_PLUGIN_CONFIG, configBundles);

        Intent profileSettingBroadcastIntent = new Intent();
        profileSettingBroadcastIntent.setAction(RWDemoIntentParams.ACTION);
        profileSettingBroadcastIntent.putExtra(RWDemoIntentParams.ACTION_EXTRA_SET_CONFIG, setConfigBundle);
        sendBroadcast(profileSettingBroadcastIntent);
    }

}
