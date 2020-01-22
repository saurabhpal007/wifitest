package com.pal.thirstymission.wifitest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.*;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    Double ax;
    private static final String TAG="MAinActivity";
    private SensorManager sensorManager;
    Sensor accelerometer;
    private float last_x, last_y, last_z;
    private long lastUpdate = 0;
    TextView range,speedy,acc,distance;
    Button press;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        range=findViewById(R.id.text1);
        speedy=findViewById(R.id.text3);
        acc=findViewById(R.id.text2);
        distance=findViewById(R.id.text4);
        press=findViewById(R.id.press);
        sensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        Log.i("ppppp","Conneted");

        press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWifi();


            }
        });


    }
    private void checkWifi(){
        WifiManager wifiManager;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo Info = cm.getActiveNetworkInfo();
        if (Info == null || !Info.isConnectedOrConnecting()) {
            Log.i("WIFI CONNECTION", "No connection");
        } else {
            int netType = Info.getType();
            int netSubtype = Info.getSubtype();

            if (netType == ConnectivityManager.TYPE_WIFI) {
                wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                int linkSpeed = wifiManager.getConnectionInfo().getLinkSpeed();
                int rssi = wifiManager.getConnectionInfo().getRssi();
                Log.i("WIFI CONNECTION", "Wifi connection speed: "+linkSpeed + " rssi: "+rssi);
                range.setText(String.valueOf(rssi));

                ax= GetDistanceFromRssiAndTxPowerOn1m(rssi,-45);
                Log.i("qqqqqqq",""+ax);
                distance.setText(String.valueOf(ax));
                //Need to get wifi strength
            }
        }
    }



    public double GetDistanceFromRssiAndTxPowerOn1m(double rssi, int txPower)
    {
        /*
         * RSSI = TxPower - 10 * n * lg(d)
         * n = 2 (in free space)
         *
         * d = 10 ^ ((TxPower - RSSI) / (10 * n))
         */
        return Math.pow(10, ((double)txPower - rssi) / (10 * 2));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.i("Acceletrometer output","X="+event.values[0]+"  Y="+event.values[1]+"  Z="+event.values[2]);
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        acc.setText("X="+event.values[0]+"  Y="+event.values[1]+"  Z="+event.values[2]);
        float speed=0;

        long curTime = System.currentTimeMillis();


        if ((curTime - lastUpdate) > 100) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
        }
        last_x = x;
        last_y = y;
        last_z = z;

        Log.i("speed",""+speed);

        speedy.setText(String.valueOf(speed));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
