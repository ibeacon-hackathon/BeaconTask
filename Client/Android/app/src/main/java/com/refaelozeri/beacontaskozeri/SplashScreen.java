package com.beacontask.android;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;


public class SplashScreen extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 20000;
    ImageView img;

    String SENDER_ID = "461638338862";
    Context context;
    GoogleCloudMessaging gcm;
    String TAG = "Beacon_GCM";
    String regid;
    SharedPreferences prefs;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //private String[] uuids = {"",""};
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
    private static final int REQUEST_ENABLE_BT = 1234;
    BeaconManager beaconManager;
    private Vector<String> uuids;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        img = (ImageView) findViewById (R.id.imgLogo);
        img.startAnimation(FadeIn(2750));
        beaconManager = new BeaconManager(this);
        uuids = new Vector<String>();
        Log.d(TAG, "FIRST OBSSSSSSSSSSSSSSSSSSS");




        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
        Log.d(TAG, "FIRST OBSSSSSSSSSSSSSSSSSSS");

        context = getApplicationContext();
        gcm = GoogleCloudMessaging.getInstance(this);
        regid = getRegistrationId(context);

        if (regid.isEmpty())
        {
            Log.i(TAG, "GOING TO REGISTER");
            registerInBackground();
        }

//        new Handler().postDelayed(new Runnable() {
//            /*
//             * Showing splash screen with a timer. This will be useful when you
//             * want to show case your app logo / company
//             */
//            @Override
//            public void run() {
//                // This method will be executed once the timer is over
//                // Start your app main activity
//                Intent i = new Intent(SplashScreen.this, MainActivity.class);
//                startActivity(i);
//                Log.d(TAG, "FIRST OBSSSSSSSSSSSSSSSSSSS");
//                // close this activity
//                finish();
//            }
//        }, SPLASH_TIME_OUT);
    }
    private Animation FadeIn(int t)
    {
        Animation fade;
        fade = new AlphaAnimation(0.0f,1.0f);
        fade.setDuration(t);
        fade.setInterpolator(new AccelerateInterpolator());
        return fade;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(SplashScreen.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */


    private class RegisterGCMTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String o) {
            Log.i(TAG, o);
        }

        @Override
        protected String doInBackground(String... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regid;

                Log.i(TAG, msg);
                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.
                sendRegistrationIdToBackend();

                // For this demo: we don't need to send it because the device
                // will send upstream messages to a server that echo back the
                // message using the 'from' address in the message.

                // Persist the regID - no need to register again.
                storeRegistrationId(context, regid);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                Log.i(TAG, msg);
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
            return msg;

        }
    }

    private void registerInBackground() {
        new RegisterGCMTask().execute(new String[] {""});
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    private boolean mIntentInProgress;


    protected void onStart() {
        super.onStart();
        Log.d(TAG, "FIRST ON STAARTTTTTTTTTTTTTT");
        mGoogleApiClient.connect();
        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
            return;
        }
        // If Bluetooth is not enabled, let user enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToService();
        }
    }

    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "CONNECTION FAILED");
        if (!mIntentInProgress && result.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(result.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private class API_CALL_Get_Task_List extends CallAPI
    {
        public API_CALL_Get_Task_List (String URL, JSONObject jsonObjSend) {
            super(URL,jsonObjSend);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            Toast.makeText(SplashScreen.this, "WE GOT THE UUIDS!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            intent.putExtra("JSONObject", result.toString());
            startActivity(intent);
        }
    }
    public void onConnected(Bundle connectionHint) {
        // Configure BeaconManager.
        Log.d(TAG, "CONNECTION NUMBER 1");

        Log.d(TAG, "CONNECTION NUMBER 11");
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {

            @Override
            public void onBeaconsDiscovered(final Region region, final List<Beacon> beacons) {
                Log.d(TAG, "CONNECTION NUMBER 111");
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Note that beacons reported here are already sorted by estimated
                        // distance between device and beacon.
                        //adapter.replaceWith(beacons);
                        Log.d(TAG, "CONNECTION NUMBER 2");
                        for (int i = 0 ; i < beacons.size() ; i++) {
                            uuids.add(beacons.get(i).getMacAddress());
                            Log.d(TAG, "UUIDSS " + beacons.get(i).getMacAddress());
                        }
                        try {
                            Log.d(TAG, "WE GOT HEREEEEEEEEEEEEEE 1");
                            beaconManager.stopRanging(region);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("authToken","yechiel");
                            JSONArray arr = new JSONArray();
                            for (int i = 0; i < uuids.size(); i++) {
                                arr.put(uuids.get(i));
                            }
                            obj.put("beacons",arr);
                            new API_CALL_Get_Task_List("http://54.149.188.204/rest_api/task/list",obj).execute();
                            Log.d(TAG, "API JSON:" + obj.toString());
                            Toast.makeText(SplashScreen.this, "User is connected, Getting Task beacons!", Toast.LENGTH_LONG).show();

                        }
                        catch (Exception e) {

                            Toast.makeText(SplashScreen.this, "INTERNAL ERROR!" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        Log.d(TAG, "CONNECTION NUMBER 1114");

    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }


    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }
    private void connectToService() {

        //adapter.replaceWith(Collections.<Beacon>emptyList());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    Toast.makeText(SplashScreen.this, "Cannot start ranging, something terrible happened",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

}