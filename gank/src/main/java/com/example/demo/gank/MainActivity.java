package com.example.demo.gank;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.demo.gank.provider.Settings;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mFuli;
    private Button mLocal;
    private Button mDb;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFuli = (Button)findViewById(R.id.fuli);
        mLocal = (Button)findViewById(R.id.local);
        mDb = (Button)findViewById(R.id.dbop);

        mFuli.setOnClickListener(this);
        mLocal.setOnClickListener(this);
        mDb.setOnClickListener(this);
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
            case R.id.dbop:
                /*ContentValues values = new ContentValues();
                values.put(Settings.COLUMN_NAME_TITLE,"settings");
                values.put(Settings.COLUMN_NAME_VALUE,"value");
                Uri uri  = getContentResolver().insert(Settings.CONTENT_URI,values);
                String id  = uri.getPathSegments().get(1);*/
                Cursor c = getContentResolver().query(Settings.CONTENT_URI,new String[]{Settings.COLUMN_NAME_TITLE,Settings.COLUMN_NAME_VALUE},Settings.COLUMN_NAME_TITLE+" =? ",
                        new String[]{"settings"},null);
                if(c != null && c.moveToFirst()){
                    String value = c.getString(c.getColumnIndex(Settings.COLUMN_NAME_VALUE));
                    Log.e(TAG,"=================query value: "+value);
                }
                //Log.e(TAG,"================insert id: "+id);
                break;
        }
    }
}
