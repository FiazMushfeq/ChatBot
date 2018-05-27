package com.example.fiazm.textmessageapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.TextView;

import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    BroadcastReceiver broadcastReceiver;
    TextView textView;
    int state = 0;
    String body;
    String address;
    String[] greeting = {"Hey, what's up :)", "Hey babe, what's up :)", "Hi, what do you need to tell me? :)", "Hi, what's happening? :)"};
    String[] breakUp = {"FR? It was almost one month!", "No! Are you kidding me?", "STOP! Are you serious?", ":( Are you fr?"};
    String[] end = {"YOU ARE DONE", "I AM COMING AFTER YOU", "AFTER ALL I HAVE DONE!?", "YOU ARE A TERRIBLE PERSON"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_CODE);
        }
        textView = findViewById(R.id.textView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastReceiver = new SMS();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    public class SMS extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Object[] objects = (Object[])(bundle.get("pdus"));
            SmsMessage[] smsMessageArray = new SmsMessage[objects.length];
            for(int i = 0; i < objects.length; i++) {
                smsMessageArray[i].createFromPdu((byte[])(objects[i]), "format");

                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])(objects[i]));
                body = smsMessage.getMessageBody();
                address = smsMessage.getOriginatingAddress();
            }

            Handler handler = new Handler();

            if(state == 0) {
                if(body.toUpperCase().contains("HEY")) {
                    int rand = ThreadLocalRandom.current().nextInt(0, 4);
                    handler.postDelayed(runnableMethod(greeting[rand]), 3000);
                    state++;
                }
                else {
                    handler.postDelayed(runnableMethod("Wdym?"), 3000);
                }
            }
            else if(state == 1) {
                if(body.toUpperCase().contains("WE NEED TO BREAK UP")) {
                    int rand = ThreadLocalRandom.current().nextInt(0, 4);
                    handler.postDelayed(runnableMethod(breakUp[rand]), 3000);
                    state++;
                }
                else {
                    handler.postDelayed(runnableMethod("Wdym?"), 3000);
                }
            }
            else if(state == 2) {
                if(body.toUpperCase().contains("YES")) {
                    int rand = ThreadLocalRandom.current().nextInt(0, 4);
                    handler.postDelayed(runnableMethod(end[rand]), 3000);
                    state = 0;
                }
                else {
                    handler.postDelayed(runnableMethod("Wdym?"), 3000);
                }
            }
        }
    }

    public Runnable runnableMethod(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(state == 0)
                    textView.setText("State: Greeting");
                if(state == 1)
                    textView.setText("State: Break Up");
                if(state == 2)
                    textView.setText("State: Ending");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(address, null, message, null, null);
            }
        };
        return  runnable;
    }
}
