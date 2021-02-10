package com.gasparaiciukas.owntrainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: fix step counter bug (previous total steps, look logs)
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onStepActivityClicked(View view) {
        Intent intent = new Intent(view.getContext(), StepActivity.class);
        view.getContext().startActivity(intent);
    }
}