package com.example.demo.gank;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gank);
        this.getSupportFragmentManager().beginTransaction().replace(android.R.id.content,new GankFragment()).commit();

    }
}
