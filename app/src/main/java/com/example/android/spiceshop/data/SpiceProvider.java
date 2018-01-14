package com.example.android.spiceshop.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import static com.example.android.spiceshop.data.SpiceContract.Spices.COLUMN_SPICE_NAME;
import static com.example.android.spiceshop.data.SpiceContract.Spices.COLUMN_SPICE_PRICE;
import static com.example.android.spiceshop.data.SpiceContract.Spices.COLUMN_SPICE_QUANTITY;
import static com.example.android.spiceshop.data.SpiceContract.Spices.CONTENT_ITEM_TYPE;
import static com.example.android.spiceshop.data.SpiceContract.Spices.CONTENT_LIST_TYPE;
import static com.example.android.spiceshop.data.SpiceContract.Spices.TABLE_NAME;
import static com.example.android.spiceshop.data.SpiceContract.Spices._ID;

/**
 * Created by Carina on 2018-01-10.
 */

public class SpiceProvider extends ContentProvider {
    public static final String LOG_TAG = SpiceProvider.class.getSimpleName();
    private static final int SPICES = 100;
    private static final int SPICE_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(SpiceContract.CONTENT_AUTHORITY, "spices", SPICES);
        sUriMatcher.addURI(SpiceContract.CONTENT_AUTHORITY, "spices/#", SPICE_ID);
    }

    private SpiceDBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new SpiceDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case SPICES:
                cursor = database.query(TABLE_NAME, projection, null, null,
                        null, null, sortOrder);
                break;
            case SPICE_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SPICES:
                return insertSpice(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertSpice(Uri uri, ContentValues values) {

        String name = values.getAsString(COLUMN_SPICE_NAME);
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Spices requires a name");
        }

        Float price = values.getAsFloat(COLUMN_SPICE_PRICE);
        if (price != null && price < 0.00) {
            throw new IllegalArgumentException("Price can't be negative");
        }

        Integer quantity = values.getAsInteger(COLUMN_SPICE_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Quantity can't be negative");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SPICES:
                return updateSpice(uri, contentValues, selection, selectionArgs);
            case SPICE_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateSpice(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateSpice(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(COLUMN_SPICE_NAME)) {
            String name = values.getAsString(COLUMN_SPICE_NAME);
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Spices requires a name");
            }
        }
        if (values.containsKey(COLUMN_SPICE_PRICE)) {
            Float price = values.getAsFloat(COLUMN_SPICE_PRICE);
            if (price != null && price < 0.00) {
                throw new IllegalArgumentException("Price can't be negative");
            }
        }

        if (values.containsKey(COLUMN_SPICE_QUANTITY)) {
            Integer quantity = values.getAsInteger(COLUMN_SPICE_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Quantity can't be negative");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SPICES:
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0)
                    getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            case SPICE_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0)
                    getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SPICES:
                return CONTENT_LIST_TYPE;
            case SPICE_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
