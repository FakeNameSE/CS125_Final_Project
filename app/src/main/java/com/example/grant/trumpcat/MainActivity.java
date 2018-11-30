package com.example.grant.trumpcat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    @SuppressLint("SetJavascriptEnabled")
    //@Override
    private static final String TAG = "TrumpCat";
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
                tv1.setText("Sent!");

            }
        });
    }
}
