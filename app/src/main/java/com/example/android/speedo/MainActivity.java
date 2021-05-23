package com.example.android.speedo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {


    private int cMaxSpeed;
    private int cDistance;
    private int cAverageSpeed;
    private int cSteps;
    private int cSpeed;
    private float cTime;

    private TextView maxSpeed;
    private TextView distance;
    private float beforeSpeed = 0;
    private TextView steps;
    private TextView speed;
    private TextView res;

    static final int TIMER_DONE = 2;
    static final int START = 3;

    // StartCatcher mStartListener;
    private XYZAccelerometer xyzAcc;
    private SensorManager mSensorManager;
    private static final long UPDATE_INTERVAL = 500;
    private static final long MEASURE_TIMES = 20;
    private Timer timer;
    private String result = "";
    private TextView tv;
    private Button testBtn;
    private EditText dx;
    private EditText dy;
    private EditText dz;
    private  float ddx;
    private  float ddy;
    private float ddz;
    int counter;
    int tpcounter = 0;
    private MeasureData mdXYZ;

    Handler hRefresh = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIMER_DONE:

                    onMeasureDone();
                    String es1 = Float.toString(Math.round(mdXYZ.getLastSpeedKm() * 100) / 100f);
                    String es2 = Float.toString(Math.round(mdXYZ.getDistanceM() * 100) / 100f);
                    tv.setText("" + es1);
                    distance.setText("" + es2);
                    enableButtons();
                    break;
                case START:
                    tv.setText("вычисление...");
                    distance.setText("вычисление...");
                    timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dumpSensor();
                                }
                            });
                        }
                    };
                    timer.schedule(task, 0, UPDATE_INTERVAL);

                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        distance = (TextView)findViewById(R.id.distance);
                tv = (TextView)findViewById(R.id.speed);
        res = (TextView)findViewById(R.id.result);

        tv = (TextView) findViewById(R.id.speed);
        testBtn = (Button) findViewById(R.id.start);
        tv.setText("...");
        distance.setText("...");
        dx = (EditText)findViewById((R.id.dx));
        dy = (EditText)findViewById((R.id.dy));
        dz = (EditText)findViewById((R.id.dz));

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        setAccelerometer();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(xyzAcc);
        super.onPause();
    }

    // вызывается после нажатия на start
    public void onButtonTest(View v) {
        xyzAcc.setdX(Float.valueOf(dx.getText().toString()));
        xyzAcc.setdY(Float.valueOf(dy.getText().toString()));
        xyzAcc.setdZ(Float.valueOf(dz.getText().toString()));
        disableButtons();
        mdXYZ = new MeasureData(UPDATE_INTERVAL);
        counter = 0;
        hRefresh.sendEmptyMessage(START);
    }

    // метод вызывается таймером каждые 500 мс
    void dumpSensor() {
        ++counter;
        float interval = (float)UPDATE_INTERVAL;

        mdXYZ.addPoint(xyzAcc.getPoint());
        result += "Time: " + String.format("%1$.3f", cTime) + " ACC: " + mdXYZ.getAcc() +"\n";
        cTime+= interval/1000;
        //если набралось 20 точек с данными то заканчиваем
        if (counter > MEASURE_TIMES) {
            timer.cancel();
            hRefresh.sendEmptyMessage(TIMER_DONE);
        }

    }

    private void enableButtons() {
        testBtn.setEnabled(true); //разблокировка кнопки

    }

    //добавление слушателя xyzAcc на акселерометр
    private void setAccelerometer() {
        xyzAcc = new XYZAccelerometer();
        mSensorManager.registerListener(xyzAcc,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    // метод блокирует кнопку start
    private void disableButtons() {

        testBtn.setEnabled(false); // блокировка кнопки
    }

    private void onMeasureDone() {
        mdXYZ.process();
    }


    public void refresh(View view){
        result = "";
        cDistance = 0; cTime = 0;
        distance.setText("...");
        //res.setText("");
        tv.setText("...");
    }
    public void results(View view){
        tpcounter++;
        if (tpcounter%2 == 1)
        res.setText(result);
        else res.setText("");
    }
    public void back(View view){
        setContentView(R.layout.activity_main);
    }
}
