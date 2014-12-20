package com.beacontask.android;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yechiel on 19/12/14.
 */
public class CallAPI extends AsyncTask<Void, Void, JSONObject>
{
    private String TAG = "Beacon_HTTP";
    private String URL;
    private JSONObject jsonObjSend;

    public CallAPI(String URL, JSONObject jsonObjSend) {
        this.URL = URL;
        this.jsonObjSend = jsonObjSend;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject jsonObjRecv = new JSONObject();
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPostRequest = new HttpPost(URL);

            StringEntity se;
            se = new StringEntity(jsonObjSend.toString());

            // Set HTTP parameters
            httpPostRequest.setEntity(se);
            httpPostRequest.setHeader("Accept", "application/json");
            httpPostRequest.setHeader("Content-type", "application/json");

            long t = System.currentTimeMillis();
            HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
            Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis() - t) + "ms]");

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content stream
                InputStream instream = entity.getContent();

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStrBuilder.append(inputStr);
                    Log.i(TAG, ":Z:" + inputStr);
                }
                jsonObjRecv= new JSONObject(responseStrBuilder.toString());
                Log.i(TAG, ":Z:" + responseStrBuilder.toString());
                instream.close();


                // Raw DEBUG output of our received JSON object:
                Log.i(TAG, "<JSONObject>\n" + jsonObjRecv.toString() + "\n</JSONObject>");


            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return jsonObjRecv;
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    protected void onPostExecute(JSONObject result) {
    }
}