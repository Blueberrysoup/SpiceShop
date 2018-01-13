package com.example.android.spiceshop;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.android.spiceshop.data.SpiceContract.Spices.COLUMN_SPICE_NAME;
import static com.example.android.spiceshop.data.SpiceContract.Spices.COLUMN_SPICE_PRICE;
import static com.example.android.spiceshop.data.SpiceContract.Spices.COLUMN_SPICE_QUANTITY;
import static com.example.android.spiceshop.data.SpiceContract.Spices.CONTENT_URI;
import static com.example.android.spiceshop.data.SpiceContract.Spices._ID;

/**
 * Created by Carina on 2018-01-10.
 */

public class SpiceCursorAdapter extends CursorAdapter {

    public SpiceCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        //ImageView productImageView = (ImageView) view.findViewById(R.id.spice_image);

        int idColumnIndex = cursor.getColumnIndex(_ID);
        int nameColumnIndex = cursor.getColumnIndex(COLUMN_SPICE_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(COLUMN_SPICE_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(COLUMN_SPICE_PRICE);
        //int imageColumnIndex = cursor.getColumnIndex(COLUMN_SPICE_IMAGE);

        final int spiceId = cursor.getInt(idColumnIndex);
        String spiceName = cursor.getString(nameColumnIndex);
        final int spiceQuantity = cursor.getInt(quantityColumnIndex);
        float spicePrice = cursor.getFloat(priceColumnIndex);
        //String spiceImageString = cursor.getString(imageColumnIndex);

        nameTextView.setText(spiceName);
        quantityTextView.setText(String.valueOf(spiceQuantity));
        priceTextView.setText(String.format("%.2f", spicePrice));
        //productImageView.setImageBitmap(getBitmapFromUri(Uri.parse(spiceImageString)));

        Button saleButton = (Button) view.findViewById(R.id.sale);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spiceQuantity > 0) {
                    int updatedSpiceQuantity = spiceQuantity-1;
                    Uri uri = ContentUris.withAppendedId(CONTENT_URI, spiceId);
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_SPICE_QUANTITY, updatedSpiceQuantity);
                    context.getContentResolver().update(uri, values, null, null);
                } else {
                    Toast.makeText(context, R.string.outofstock, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
