package com.example.jelelight.clinicqueuing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GenQueueActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_gen_queue);
    }
}
