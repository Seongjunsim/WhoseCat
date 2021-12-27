package com.jmsmart.whosecat.util;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ZZQYU on 2019-02-22.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    View view = null;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }
    public DownloadImageTask(ImageView bmImage, View view) {
        this.bmImage = bmImage;
        this.view = view;
    }

    @Override
    protected void onPreExecute() {
        Log.i("!!!SSSS", "start");
        super.onPreExecute();

    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        HttpURLConnection connection = null;
        InputStream is = null;
        Bitmap mIcon11 = null;

        Log.i("!!!EEEEEE","");
        try {
            Log.i("!!!EEEEEE","");
            URL imgUrl = new URL(urls[0]);
            //InputStream in = new java.net.URL(urldisplay).openStream();
            //mIcon11 = BitmapFactory.decodeStream(in);
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            connection.getInputStream();
            mIcon11 = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        Log.i("!!!ENDEND","");
        bmImage.setImageBitmap(result);
        if(view!=null)view.setBackground(new BitmapDrawable(view.getResources(), result));
    }
}