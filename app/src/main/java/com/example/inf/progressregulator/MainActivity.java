package com.example.inf.progressregulator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    ProgressRegulator progressRegulator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressRegulator = (ProgressRegulator) findViewById(R.id.abc);
        progressRegulator.setListener(new ProgressRegulator.ProgressChange() {
            @Override
            public void onProgressChange(int progress) {
                Log.i("aaa", "onProgressChange: "+progress);
            }
        });

    }
}
