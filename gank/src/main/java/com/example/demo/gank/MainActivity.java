package com.example.demo.gank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mFuli;
    private Button mLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFuli = (Button)findViewById(R.id.fuli);
        mLocal = (Button)findViewById(R.id.local);

        mFuli.setOnClickListener(this);
        mLocal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fuli: {
                Intent intent = new Intent();
                intent.setClass(this, GankActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.local:{
                Intent intent = new Intent();
                intent.setClass(this,LocalAlbumActivity.class);
                startActivity(intent);
            }
                break;
        }
    }
}
