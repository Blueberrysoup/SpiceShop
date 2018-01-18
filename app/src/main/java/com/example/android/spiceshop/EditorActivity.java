package com.example.android.spiceshop;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.example.android.spiceshop.data.SpiceContract.Spices.COLUMN_SPICE_DESC;
import static com.example.android.spiceshop.data.SpiceContract.Spices.COLUMN_SPICE_IMAGE;
import static com.example.android.spiceshop.data.SpiceContract.Spices.COLUMN_SPICE_NAME;
import static com.example.android.spiceshop.data.SpiceContract.Spices.COLUMN_SPICE_PRICE;
import static com.example.android.spiceshop.data.SpiceContract.Spices.COLUMN_SPICE_QUANTITY;
import static com.example.android.spiceshop.data.SpiceContract.Spices.COLUMN_SPICE_SUPPLIER;
import static com.example.android.spiceshop.data.SpiceContract.Spices.CONTENT_URI;
import static com.example.android.spiceshop.data.SpiceContract.Spices._ID;
import static com.example.android.spiceshop.data.SpiceProvider.LOG_TAG;

/**
 * Created by Carina on 2018-01-11.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_SPICE_LOADER = 0;
    private static final int PICK_IMAGE = 1;
    private boolean mSpiceHasChanged = false;
    private Uri mImageUri;

    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierEditText;
    private ImageView mProductImageView;

    private Uri mCurrentSpiceUri;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mSpiceHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentSpiceUri = intent.getData();

        mNameEditText = (EditText) findViewById(R.id.edit_spice_name);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_spice_description);
        mPriceEditText = (EditText) findViewById(R.id.edit_spice_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_spice_quantity);
        mSupplierEditText = (EditText) findViewById(R.id.edit_spice_supplier);
        mProductImageView = (ImageView) findViewById(R.id.productImageView);

        Button orderMoreButton = (Button) findViewById(R.id.button_order_more);
        Button increaseButton = (Button) findViewById(R.id.button_increase_quantity);
        Button decreaseButton = (Button) findViewById(R.id.button_decrease_quantity);
        Button selectImageButton = (Button) findViewById(R.id.button_image_picker);

        if (mCurrentSpiceUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_spice));
            orderMoreButton.setVisibility(View.INVISIBLE);
            mQuantityEditText.setText("0");
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_spice));
            getSupportLoaderManager().initLoader(EXISTING_SPICE_LOADER, null, this);
            orderMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String supplierEmail = mSupplierEditText.getText().toString().trim();
                    Intent email = new Intent(Intent.ACTION_SENDTO);
                    email.setData(Uri.parse("mailto:"));
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{supplierEmail});
                    email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                    String message = getString(R.string.email_message1) + " " + mNameEditText.getText().toString() + "\n\n" + getString(R.string.email_message2);
                    email.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(email);
                }
            });
        }
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = Integer.parseInt(mQuantityEditText.getText().toString());
                mQuantityEditText.setText(String.valueOf(++currentQuantity));
            }
        });
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = Integer.parseInt(mQuantityEditText.getText().toString());
                if (currentQuantity > 0)
                    mQuantityEditText.setText(String.valueOf(--currentQuantity));
            }
        });
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, getString(R.string.selectimage)), PICK_IMAGE);
            }
        });

        mNameEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                _ID,
                COLUMN_SPICE_NAME,
                COLUMN_SPICE_DESC,
                COLUMN_SPICE_PRICE,
                COLUMN_SPICE_QUANTITY,
                COLUMN_SPICE_SUPPLIER,
                COLUMN_SPICE_IMAGE};

        return new CursorLoader(this, mCurrentSpiceUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(COLUMN_SPICE_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(COLUMN_SPICE_DESC);
            int priceColumnIndex = cursor.getColumnIndex(COLUMN_SPICE_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(COLUMN_SPICE_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(COLUMN_SPICE_SUPPLIER);
            int productImageIndex = cursor.getColumnIndex(COLUMN_SPICE_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            final String supplier = cursor.getString(supplierColumnIndex);
            String imageUriString = cursor.getString(productImageIndex);
            mImageUri = Uri.parse(imageUriString);

            mNameEditText.setText(name);
            mDescriptionEditText.setText(description);
            mPriceEditText.setText(String.format("%.2f", price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierEditText.setText(supplier);
            mProductImageView.setImageBitmap(getBitmapFromUri(Uri.parse(imageUriString)));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mDescriptionEditText.setText("");
        mPriceEditText.setText(Float.toString(0));
        mQuantityEditText.setText(Integer.toString(0));
        mSupplierEditText.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentSpiceUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (saveSpice())
                    finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mSpiceHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Solution found here: https://discussions.udacity.com/t/inventory-app-image-permission-issue-need-help/305819/27
    // and here: https://stackoverflow.com/questions/25414352/how-to-persist-permission-in-android-api-19-kitkat:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && (resultCode == RESULT_OK)) {
            try {
                mImageUri = data.getData();

                int takeFlags = data.getFlags();
                takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                try {
                    getContentResolver().takePersistableUriPermission(mImageUri, takeFlags);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                mProductImageView.setImageBitmap(getBitmapFromUri(mImageUri));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Function borrowed from Udacity Forum post "[Unofficial] How to Pick an Image from the Gallery":
    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // I can't get getWidth/getHeight to return anything else than 0
        // so I use the same value as <dimen name="imageview_width">160dp</dimen>
        //int targetW = mProductImageView.getWidth();
        //int targetH = mProductImageView.getHeight();
        int targetW = 160;
        int targetH = 160;

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean saveSpice() {
        String nameString = mNameEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString();
        String quantityString = mQuantityEditText.getText().toString();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String imageString;

        // Price and quantity are allowed to be blank - if so, the default to 0.00 and 0
        float priceFloat = 0.00f;
        int quantityInt = 0;

        // If nothing has changed
        if (TextUtils.isEmpty(nameString) && TextUtils.isEmpty(descriptionString) && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierString) && TextUtils.isEmpty(mImageUri.toString()))
            return false;

        // Empty name is not allowed
        if (TextUtils.isEmpty(nameString)) {
            mNameEditText.setError("Enter a name for the spice");
            return false;
        }

        // Empty description is not allowed
        if (TextUtils.isEmpty(descriptionString)) {
            mDescriptionEditText.setError("Enter a description for the spice");
            return false;
        }

        // Empty supplier email is not allowed
        if (TextUtils.isEmpty(supplierString)) {
            mSupplierEditText.setError("Enter an email address for the supplier");
            return false;
        }

        // Uploading a product image is optional
        if (mImageUri == null)
            imageString = "";
        else
            imageString = mImageUri.toString();

        if (!TextUtils.isEmpty(priceString))
            priceFloat = Float.parseFloat(priceString.replace(",","."));

        if (!TextUtils.isEmpty(quantityString))
            quantityInt = Integer.parseInt(quantityString);

        ContentValues values = new ContentValues();
        values.put(COLUMN_SPICE_NAME, nameString);
        values.put(COLUMN_SPICE_DESC, descriptionString);
        values.put(COLUMN_SPICE_PRICE, priceFloat);
        values.put(COLUMN_SPICE_QUANTITY, quantityInt);
        values.put(COLUMN_SPICE_SUPPLIER, supplierString);
        values.put(COLUMN_SPICE_IMAGE, imageString);

        try {
            if (mCurrentSpiceUri == null) {
                Uri newUri = getContentResolver().insert(CONTENT_URI, values);
                if (newUri == null)
                    Toast.makeText(this, R.string.ErrorSavingSpices, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, R.string.SpicesSaved, Toast.LENGTH_SHORT).show();
            } else {
                int rowsUpdated = getContentResolver().update(mCurrentSpiceUri, values, null, null);
                if (rowsUpdated == -1)
                    Toast.makeText(this, R.string.ErrorSavingSpices, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, R.string.SpicesSaved, Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Check your input. " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteSpice();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteSpice() {
        if (mCurrentSpiceUri != null) {
            if (getContentResolver().delete(mCurrentSpiceUri, null, null) > 0) {
                Toast.makeText(this, R.string.SpicesDeleted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.ErrorDeletingSpices, Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mSpiceHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

}
