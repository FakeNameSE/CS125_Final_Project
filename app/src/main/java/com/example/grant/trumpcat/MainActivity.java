package com.example.grant.trumpcat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONStringer;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;


public class MainActivity extends AppCompatActivity {
    @SuppressLint("SetJavascriptEnabled")
    //@Override
    private static RequestQueue requestQueue;
    private static final String TAG = "TrumpCat";
    private JSONObject resp;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView tv1 = (TextView)findViewById(R.id.statusText);
        //tv1.setText("");

        final Button button = findViewById(R.id.sendButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                EditText getNumber= (EditText) findViewById(R.id.phoneNumber);
                String number = getNumber.getText().toString();
                Log.d(TAG, "onClick: number entered is " + number);
                try {
                    startAPICall("https://api.tronalddump.io/random/quote");
                    String quote = JsonParse(resp, "value");
                    Log.d(TAG, quote + " downloaded correctly");
                    startAPICall("https://aws.random.cat/meow");
                    String imageLink = JsonParse(resp, "file");
                    Log.d(TAG, imageLink + " downloaded correctly");

                } catch(Exception e) {
                    Log.d(TAG, e.toString());
                    tv1.setText("Content Download Failed :(");
                    return;
                }
                tv1.setText("Sent!");
            }
        });

    }
    void startAPICall(String setUrl) throws Exception {
        try {
            resp = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                setUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.d(TAG, "startAPI onResponse" + response.toString());
                        resp = response;
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.w(TAG, "startAPI onErrorResponse" + error.toString());
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Catch block in startAPICall");
            throw e;
        }
    }
    String JsonParse(JSONObject jason, String key) throws JSONException {
       // JsonParser parser = new JsonParser();
        try {
            String extracted = jason.getString(key);
            return extracted;
        }
        catch (JSONException e) {
            throw e;
        }
    }
}
