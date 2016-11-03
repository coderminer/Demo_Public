package com.example.demo.gank.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Settings implements BaseColumns{
    private Settings(){}

    public static final String AUTHORITY = "com.example.demo.gank.provider";
    public static final String TABLE_NAME = "settings";
    public static final String SCHEME = "content://";
    public static final String COLUMN_NAME_TITLE = "name";
    public static final String COLUMN_NAME_VALUE = "value";

    private static final String PATH_SETTINGS = "/settings";
    private static final String PATH_SETTINGS_ID = "/settings/";

    public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_SETTINGS);
    public static final Uri CONTENT_ID_URI = Uri.parse(SCHEME + AUTHORITY + PATH_SETTINGS_ID);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.example.demo.gank.settings";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.example.demo.gank.settings";
}