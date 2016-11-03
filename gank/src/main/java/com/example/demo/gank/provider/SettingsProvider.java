package com.example.demo.gank.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;

public class SettingsProvider extends ContentProvider{

    private static final String DB_NAME = "settings.db";
    private static final int DB_VERSION = 1;

    private static final String[] PROJECTION = new String[]{
            Settings._ID,
            Settings.COLUMN_NAME_TITLE,
            Settings.COLUMN_NAME_VALUE
    };
    private static final int SETTINGS = 1;
    private static final int SETTINGS_ID = 2;

    private static final UriMatcher sUriMatcher;
    private static HashMap<String,String> sProjectionMap;

    private DatabaseHelper mOPenHelper;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(Settings.AUTHORITY,"settings",SETTINGS);
        sUriMatcher.addURI(Settings.AUTHORITY,"settings/#",SETTINGS_ID);

        sProjectionMap = new HashMap<>();
        sProjectionMap.put(Settings.COLUMN_NAME_TITLE,Settings.COLUMN_NAME_TITLE);
        sProjectionMap.put(Settings.COLUMN_NAME_VALUE,Settings.COLUMN_NAME_VALUE);
        sProjectionMap.put(Settings._ID,Settings._ID);
    }



    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if(sUriMatcher.match(uri) != SETTINGS)
            throw new IllegalArgumentException("Unknown uri: "+uri);
        SQLiteDatabase db = mOPenHelper.getWritableDatabase();
        long id = db.insert(Settings.TABLE_NAME,Settings._ID,values);
        if(id < 0){
            throw new SQLiteException("Unable to insert "+values+" for: "+uri);
        }
        Uri newUri = ContentUris.withAppendedId(uri,id);
        getContext().getContentResolver().notifyChange(uri,null);
        return newUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOPenHelper.getWritableDatabase();
        int count ;
        String finalWhere;

        switch (sUriMatcher.match(uri)){
            case SETTINGS:
                count = db.delete(Settings.TABLE_NAME,selection,selectionArgs);
                break;
            case SETTINGS_ID:
                String id = uri.getPathSegments().get(1);
                finalWhere = Settings._ID +" = "+id;
                if(selection != null){
                    finalWhere += " AND " +selection;
                }
                count = db.delete(Settings.TABLE_NAME,finalWhere,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOPenHelper.getWritableDatabase();
        int count;
        String finalWhere;

        switch (sUriMatcher.match(uri)){
            case SETTINGS:
                count = db.update(
                        Settings.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            case SETTINGS_ID:
                String id = uri.getPathSegments().get(1);
                finalWhere = Settings._ID +" = "+id;
                if(selection != null){
                    finalWhere += " AND " +selection;
                }

                count = db.update(Settings.TABLE_NAME,
                        values,
                        finalWhere,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Settings.TABLE_NAME);

        switch (sUriMatcher.match(uri)){
            case SETTINGS:
                qb.setProjectionMap(sProjectionMap);
                break;
            case SETTINGS_ID:
                qb.setProjectionMap(sProjectionMap);
                qb.appendWhere(
                        Settings._ID +"="+uri.getPathSegments().get(1)
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: "+uri);
        }

        SQLiteDatabase db = mOPenHelper.getReadableDatabase();
        Cursor c = qb.query(db,projection,selection,selectionArgs,null,null,sortOrder);
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case SETTINGS:
                return Settings.CONTENT_TYPE;
            case SETTINGS_ID:
                return Settings.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri: "+uri);
        }
    }

    @Override
    public boolean onCreate() {
        mOPenHelper = new DatabaseHelper(getContext());
        return true;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{
        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS settings");
            onCreate(db);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = " CREATE TABLE "+Settings.TABLE_NAME +" ("
                    + Settings._ID +" INTEGER PRIMARY KEY,"
                    + Settings.COLUMN_NAME_TITLE +" TEXT,"
                    + Settings.COLUMN_NAME_VALUE +" TEXT"
                    + ");";
            db.execSQL(sql);
        }
    }
}