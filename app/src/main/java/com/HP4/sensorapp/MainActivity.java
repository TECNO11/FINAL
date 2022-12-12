package com.HP4.sensorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.HP4.sensorapp.Reco.RecoActivity;

public class MainActivity extends AppCompatActivity {

    Button btnReco, btnNFC, btnBruju;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnReco = findViewById(R.id.btnReco);
        btnNFC = findViewById(R.id.btnNFC);
        btnBruju = findViewById(R.id.btnBruju);

        btnNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_Reco = new Intent(getApplicationContext(), Sensor_NFC.class);
                startActivity(intent_Reco);
            }
        });
        btnReco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_Reco = new Intent(getApplicationContext(), RecoActivity.class);
                startActivity(intent_Reco);
            }
        });
    }
}