package com.example.android.spiceshop;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import static com.example.android.spiceshop.data.SpiceContract.Spices.COLUMN_SPICE_NAME;
import static com.example.android.spiceshop.data.SpiceContract.Spices.COLUMN_SPICE_PRICE;
import static com.example.android.spiceshop.data.SpiceContract.Spices.COLUMN_SPICE_QUANTITY;
import static com.example.android.spiceshop.data.SpiceContract.Spices.CONTENT_URI;
import static com.example.android.spiceshop.data.SpiceContract.Spices._ID;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SPICE_LOADER = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    SpiceCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestImagePermissions();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView spiceListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        spiceListView.setEmptyView(emptyView);

        mCursorAdapter = new SpiceCursorAdapter(this, null);
        spiceListView.setAdapter(mCursorAdapter);

        spiceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentSpiceUri = ContentUris.withAppendedId(CONTENT_URI, id);
                intent.setData(currentSpiceUri);
                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(SPICE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { _ID, COLUMN_SPICE_NAME, COLUMN_SPICE_QUANTITY, COLUMN_SPICE_PRICE };

        return new CursorLoader(this, CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    // Copied from example at developer.android.com:
    public void requestImagePermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.MANAGE_DOCUMENTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.MANAGE_DOCUMENTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // I GET HERE - BUT NO REQUEST SCREEN IS SHOWING
            }
        }
    }

    // Copied from example at developer.android.com:
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
