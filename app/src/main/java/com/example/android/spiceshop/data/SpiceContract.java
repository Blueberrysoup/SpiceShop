package com.example.android.spiceshop.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Carina on 2018-01-10.
 */

public class SpiceContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.spiceshop";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SPICE = "spices";

    private SpiceContract() {
    }

    public static final class Spices implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SPICE);

        public static final String TABLE_NAME = "spices";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SPICE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SPICE;

        //Column names
        public static final String _ID = "_id";
        public static final String COLUMN_SPICE_NAME = "name";
        public static final String COLUMN_SPICE_DESC = "description";
        public static final String COLUMN_SPICE_PRICE = "price";
        public static final String COLUMN_SPICE_QUANTITY = "quantity";
        public static final String COLUMN_SPICE_SUPPLIER = "supplier";
        public static final String COLUMN_SPICE_IMAGE = "image";

    }

}
