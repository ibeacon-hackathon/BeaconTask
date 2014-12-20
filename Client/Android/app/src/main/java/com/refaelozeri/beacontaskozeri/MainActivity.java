package com.beacontask.android;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.utils.L;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getName();

    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);

    public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
    public static final String EXTRAS_BEACON = "extrasBeacon";

    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    private BeaconManager beaconManager = new BeaconManager(this);
//    private ListView taskLists;
//    private ArrayList myArray;
//    private ArrayAdapter adapter;

    private LeDeviceListAdapter adapter;
    private JSONObject json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            String value = getIntent().getStringExtra("JSONObject");
            Log.d(TAG, "VALUE:" + value);
            json = new JSONObject(value);

            // Configure device list.
            adapter = new LeDeviceListAdapter(this);
            ArrayList<Beacon> beacons = new ArrayList<Beacon>();
            JSONArray array =  json.getJSONArray("result");

            Log.d(TAG, "Array" + array.toString());
            Log.d(TAG, "size: " + array.length());
            for (int i =0 ; i < array.length();i++)
            {

                JSONObject k = array.getJSONObject(i);

                String id = k.getString("id");
                String name = k.getString("name");
                Log.d(TAG, "AFTE3");
                Beacon temp = new Beacon("b9407f30-f5f8-466e-aff9-25556b57fe6d",name,id,0,0,0,0);
                beacons.add(temp);
                Log.d(TAG, "BABABA");
            }

            Log.d(TAG, "WEHOOOOO");
            adapter.replaceWith(beacons);
            ListView list = (ListView) findViewById(R.id.taskListview);
            list.setAdapter(adapter);
            list.setOnItemClickListener(createOnItemClickListener());

        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION" + e.getMessage());

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
            Log.d(TAG, "Error while stopping ranging", e);
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                connectToService();
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectToService() {

        adapter.replaceWith(Collections.<Beacon>emptyList());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    Toast.makeText(MainActivity.this, "Cannot start ranging, something terrible happened",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

    private AdapterView.OnItemClickListener createOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Pressed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, TaskList.class);
                Log.d(TAG, "AAAA");
                try {
                    JSONArray array = json.getJSONArray("result");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject k = array.getJSONObject(i);
                        if (k.getString("id") == adapter.getItem(position).getMacAddress()) {
                            intent.putExtra("TASK_LIST",k.getJSONArray("tasks").toString());
                        }
                    }

                    startActivity(intent);
                }
                catch(Exception e)
                {}
    //                if (getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY) != null) {
//                    try {
//                        Class<?> clazz = Class.forName(getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY));
//                        Intent intent = new Intent(MainActivity.this, clazz);
//                        intent.putExtra(EXTRAS_BEACON, adapter.getItem(position));
//                        startActivity(intent);
//                    } catch (ClassNotFoundException e) {
//                        Log.e(TAG, "Finding class by name failed", e);
//                    }
//                }
            }
        };
    }
}
