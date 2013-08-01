package com.yapalexei.contactapp;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class DetailActivity extends Activity {

    TextView textView, pNumber;
    ImageView photoContainer;
    Cursor cur;
    ContentResolver cr;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Bitmap image = null;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // For the main activity, make sure the app icon in the action bar
            // does not behave as a button
            ActionBar actionBar = getActionBar();
            actionBar.setHomeButtonEnabled(true);
        }

        ArrayList<String> extra = getIntent().getStringArrayListExtra(MainActivity.EXTRA_MESSAGE);

        // Create the text, number and image view
        textView = (TextView) findViewById(R.id.detailText);
        pNumber = (TextView) findViewById(R.id.pnumber);
        photoContainer = (ImageView) findViewById(R.id.imageView);

        // position them out of view
        textView.setTranslationY(-100);
        textView.setText(extra.get(0).toString());
        pNumber.setTranslationX(-200);
        pNumber.setText(extra.get(1).toString());

        // initialize the contact table, check it and load image
        try{
            if(extra.size() > 2){
                if(extra.get(2) != null)
                    readContact(Long.valueOf(extra.get(2)));
                Log.e("IMAGE", "ID -- " + String.valueOf(extra.get(2)));
                if(extra.get(2) != null)
                    image = openDisplayPhoto(Long.valueOf(extra.get(2)));
                if(image == null)
                    image = openPhotoByID(Long.valueOf(extra.get(2)));

                //photoContainer.setRotation(10);
                if(image != null){
                    Log.e("IMAGE SIZE", String.valueOf(image.getByteCount()));
                    try{
                        photoContainer.setImageBitmap(getResizedBitmap(image,300,300));
//                        photoContainer.setImageBitmap(Bitmap.createScaledBitmap(image,width,width,false));
                        Log.e("IMAGE:", "Image should be loaded!");
                    }catch (NullPointerException e){
                        Log.e("photoContainer", "Can't assign image to photoContainer!! -- " + e.toString());
                    }
                }else{
                    Log.e("IMAGE", " -- image is NULL!!!!");
                }
            }

        }catch (NullPointerException e){
            Log.e("image problem:", "couldn't create image for some reason -- " + e.toString());
        }

        // move into view
        textView.animate().setDuration(750).translationY(0);
        pNumber.animate().setDuration(750).translationX(0);
        photoContainer.animate().setDuration(1100).alpha(1);
    }

    
    private Bitmap openPhotoByID(Long contactId){

            Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
            Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            Cursor cursor = getContentResolver().query(photoUri, new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
            if (cursor == null) {
                return null;
            }
            try {
                if (cursor.moveToFirst()) {
                    byte[] data = cursor.getBlob(0);
                    if (data != null) {
                        return BitmapFactory.decodeByteArray(data,0,data.length);
                    }
                }
            } finally {
                cursor.close();
            }
            return null;
        }

    public Bitmap openDisplayPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd =
                    getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            return BitmapFactory.decodeStream(fd.createInputStream());
        } catch (IOException e) {
            return null;
        }
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void readContact(Long id){

        cr = getContentResolver();
        cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] {
                        ContactsContract.CommonDataKinds.Phone._ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Photo.PHOTO},
                        ContactsContract.Data.CONTACT_ID
                        + "="
                        + id
                        + " AND "
                        + ContactsContract.Data.MIMETYPE
                        + "='"
                        + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                        + "'",
                null,
                null);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }



}
