package com.utility.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.utilityar.app.R;

public class FinalObservationActivity extends AppCompatActivity {
    private Button btn_next, btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_observation);
        btn_next = findViewById(R.id.btn_next);
        btn_back = findViewById(R.id.btn_back);
        btn_next.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CompleteScreenActivity.class);
            startActivity(intent);
        });
        btn_back.setOnClickListener(v -> {
            finish();
        });
    }
}