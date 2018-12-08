package com.example.grant.trumpcat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.telephony.SmsManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

// For permissions.
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("SetJavascriptEnabled")
    //@Override
    private RequestQueue requestQueue;
    private String catLink = null;
    private String trumpQuote = null;
    private String number;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.SEND_SMS};
    private TextView tv1 = null;
    private static final String TAG = "TrumpCat";
  
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_main);
        tv1 = findViewById(R.id.statusText);
        //tv1.setText("");

        final Button button = findViewById(R.id.sendButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                tv1.setText("Sending...");
                EditText getNumber = findViewById(R.id.phoneNumber);
                number = getNumber.getText().toString();
                Log.d(TAG, "onClick: number entered is " + number);
                // Thank you StackOverflow!
                if (!(Patterns.PHONE.matcher(number).matches())) {
                    Log.w(TAG, "onClick: number entered is invalid");
                    tv1.setText("Invalid phone number :(");
                    return;
                }
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
        SmsManager smsManager = SmsManager.getDefault();
        try {
            // Send a text based SMS
            ArrayList<String> divided = smsManager.divideMessage(trumpQuote);
            for (String mes : divided) {
                smsManager.sendTextMessage(
                        number,
                        null,
                        mes,
                        null,
                        null);
            }
            smsManager.sendTextMessage(number, null, catLink, null,
                    null);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            tv1.setText("Content Sending Failed :(");
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
                            Log.d(TAG, "trumpAPI onResponse" + response.toString());
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
                    Log.w(TAG, "trumpAPI onErrorResponse" + error.toString());
                    catAPI();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
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
                            Log.d(TAG, "catAPI onResponse" + response.toString());
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
                    Log.w(TAG, "catAPI onErrorResponse" + error.toString());
                    sendSMS();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Checks the dynamically-controlled permissions and requests missing permissions from end user.
     * Thank you https://developer.here.com/documentation/android-starter/dev_guide/topics/request-android-permissions.html
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted;
                break;
        }
    }
}
