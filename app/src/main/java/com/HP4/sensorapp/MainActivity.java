package com.HP4.sensorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnReco, btnNFC, btnBruju;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnReco = findViewById(R.id.btnReco);
        btnNFC = findViewById(R.id.btnNFC);
        btnBruju = findViewById(R.id.btnBruju);

        btnReco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_Reco = new Intent(getApplicationContext(), Reco.class);
                startActivity(intent_Reco);
            }
        });
    }
}