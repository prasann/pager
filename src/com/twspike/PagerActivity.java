package com.twspike;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.*;

public class PagerActivity extends Activity {

    private ViewPager awesomePager;
    private static int NUM_AWESOME_VIEWS = 0;
    private Context cxt;
    private AwesomePagerAdapter awesomeAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        cxt = this;
        pushDummyImagesInSDCard(R.drawable.qr);
        pushDummyImagesInSDCard(R.drawable.random);
        awesomeAdapter = new AwesomePagerAdapter(this, getStoredImages());
        awesomePager = (ViewPager) findViewById(R.id.awesomepager);
        awesomePager.setAdapter(awesomeAdapter);
    }

    private void pushDummyImagesInSDCard(int image) {
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(path, image + ".jpg");
        try {
            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            InputStream is = getResources().openRawResource(image);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(this,
                    new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        } catch (IOException e) {
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }

    private Bitmap[] getStoredImages() {
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] files = path.listFiles();
        Bitmap[] bitmaps = new Bitmap[files.length];
        for (int i = 0; i < files.length; i++) {
            bitmaps[i] = BitmapFactory.decodeFile(files[i].getAbsolutePath());
        }
        NUM_AWESOME_VIEWS = files.length;
        return bitmaps;
    }

    private class AwesomePagerAdapter extends PagerAdapter {

        private Context context;
        private Bitmap[] bitmaps;

        private AwesomePagerAdapter(Context context, Bitmap[] bitmaps) {
            this.context = context;
            this.bitmaps = bitmaps;
        }

        public int getCount() {
            return NUM_AWESOME_VIEWS;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(this.bitmaps[position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            collection.addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            ((ViewPager) collection).removeView((ImageView) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == ((ImageView) o);
        }
    }


}
