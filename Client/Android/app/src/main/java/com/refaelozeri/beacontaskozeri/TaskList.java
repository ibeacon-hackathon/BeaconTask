package com.beacontask.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class TaskList extends ActionBarActivity {

    private ListView listView;
    private ArrayList<Task> tasksArray;
    private MainAdapter adapter;
    protected Uri mMediaUri;

    public static final String TAG = TaskList.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int MEDIA_TYPE_IMAGE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        tasksArray = new ArrayList<Task>();
        Bundle extras = getIntent().getExtras();
        if (extras == null) { return; }
        try {
            String value = extras.getString("TASK_LIST");
            JSONArray jsonArray = new JSONArray(value);
            for (int i=0; i<jsonArray.length();i++)
            {
                JSONObject k = jsonArray.getJSONObject(i);
                tasksArray.add(new Task(k.getString("name"),
                        k.getString("description"),
                        k.getInt("points"),
                        (k.getString("status") != "open"),(k.getString("status") != "complete")));
            }
        }
        catch (Exception e)
        {}



            // Configure device list.

        // Creating array of Task objects and populating it with Task objects



        // Implementing the Custom adapter we made to handle the objects
        adapter = new MainAdapter(this, R.layout.task_row, tasksArray);

        // Setting the ListView to be populated by the Custom adapter we made
        listView = (ListView) findViewById(R.id.tasksListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(createOnItemClickListener());

    }

    private AdapterView.OnItemClickListener createOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskList.this);
                builder.setTitle("Task - " + tasksArray.get(position).getName());
                builder.setMessage(tasksArray.get(position).getDescription() + ". \n\nThe total credits for this assignment is " + tasksArray.get(position).getCredit());
                builder.setCancelable(true);
                if (tasksArray.get(position).isAssigned()) {
                    builder.setPositiveButton("Mark as Complete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Take picture
                            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                            if (mMediaUri == null) {
                                // display an error
                                Toast.makeText(TaskList.this, "Error taking the picture",
                                        Toast.LENGTH_LONG).show();
                            }
                            else {

                                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                            }
                            dialog.cancel();
                        }
                    });
                    builder.setNeutralButton("Unassign Task", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            tasksArray.get(position).setIs_assigned(false);
                            Log.d(TAG, "" +tasksArray.get(position).isAssigned());
                            adapter.notifyDataSetChanged();
                            dialog.cancel();
                        }
                    });
                } else {
                    builder.setPositiveButton("Assign to Me", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            tasksArray.get(position).setIs_assigned(true);
                            adapter.notifyDataSetChanged();
                            dialog.cancel();
                        }
                    });
                }
                builder.setNegativeButton("Close", null);
                builder.create();
                builder.show();
                //Intent intent = new Intent(MainActivity.this, TaskList.class);
                //startActivity(intent);

            }
        };
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
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

    private Uri getOutputMediaFileUri(int mediaType) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (isExternalStorageAvailable()) {
            // get the URI

            // 1. Get the external storage directory
            String appName = TaskList.this.getString(R.string.app_name);
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    appName);

            // 2. Create our subdirectory
            if (! mediaStorageDir.exists()) {
                if (! mediaStorageDir.mkdirs()) {
                    Log.e(TAG, "Failed to create directory.");
                    return null;
                }
            }

            // 3. Create a file name
            // 4. Create the file
            File mediaFile;
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

            String path = mediaStorageDir.getPath() + File.separator;
            if (mediaType == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
            }
//            else if (mediaType == MEDIA_TYPE_VIDEO) {
//                mediaFile = new File(path + "VID_" + timestamp + ".mp4");
//            }
            else {
                return null;
            }

            Log.d(TAG, "File: " + Uri.fromFile(mediaFile));

            // 5. Return the file's URI
            return Uri.fromFile(mediaFile);
        }
        else {
            return null;
        }
    }
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();

        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        else {
            return false;
        }
    }
}
