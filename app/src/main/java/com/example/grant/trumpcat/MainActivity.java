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

import java.util.regex.*;
import android.net.Uri;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("SetJavascriptEnabled")
    //@Override
    private RequestQueue requestQueue;
    private String catLink = null;
    private String trumpQuote = null;

    private TextView tv1 = null;
    private static final String TAG = "TrumpCat";
    private JSONObject resp = new JSONObject();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_main);
        tv1 = findViewById(R.id.statusText);
        //tv1.setText("");

        final Button button = findViewById(R.id.sendButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                EditText getNumber= findViewById(R.id.phoneNumber);
                String number = getNumber.getText().toString();
                Log.d(TAG, "onClick: number entered is " + number);
                // Thank you Geeks for geeks!
                // Find matching between given number and regular expression, to detect obviously
                // wrong numbers.
                /*
                Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");
                Matcher m = p.matcher(number);
                if (!(m.find() && m.group().equals(number))) {
                    Log.w(TAG, "onClick: number entered is invalid");
                    tv1.setText("Invalid phone number :(");
                    return;
                }
                */
                startAPICalls();
            }
        });

    }

    private void sendSMS() {
        if (trumpQuote == null || catLink == null) {
            Log.d(TAG, "Content Download Failed :(");
            tv1.setText("Content Download Failed :(");
            return;
        }
        tv1.setText("Sent!");
    }

    private void startAPICalls() {
        trumpAPI();
    }

    private void trumpAPI() {
        try {
            //JSONObject resp = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://api.tronalddump.io/random/quote",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.d(TAG, "startAPI onResponse" + response.toString());
                            try {
                                trumpQuote = JsonParse(response, "value");
                            } catch (JSONException e) {
                                Log.e(TAG, e.toString());
                            }
                            catAPI();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.w(TAG, "startAPI onErrorResponse" + error.toString());
                    catAPI();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Catch block in startAPICall");
            throw e;
        }

    }

    private void catAPI() {
        try {
            //JSONObject resp = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://aws.random.cat/meow",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.d(TAG, "startAPI onResponse" + response.toString());
                            try {
                                catLink = JsonParse(response, "file");
                            } catch (JSONException e) {
                                Log.e(TAG, e.toString());
                            }
                            sendSMS();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.w(TAG, "startAPI onErrorResponse" + error.toString());
                    sendSMS();
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
            Log.d(TAG, extracted);
            return extracted;
        }
        catch (JSONException e) {
            throw e;
        }
    }
}
