package com.my.picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class ImageDownloader extends AsyncTask<Void, Void, Bitmap>{
	private String downloadUrl;
	private ImageView containerToPut;
	
	public ImageDownloader(String url, ImageView iv){
		this.downloadUrl = url;
		this.containerToPut = iv;
	}
	
    @Override
    protected Bitmap doInBackground(Void... param) {
        return downloadBitmap(downloadUrl);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        Log.i("Async-Example", "onPostExecute Called");
        containerToPut.setImageBitmap(result);
    }

    private Bitmap downloadBitmap(String url) {
        final DefaultHttpClient client = new DefaultHttpClient();
        final HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode + 
                        " while retrieving bitmap from " + url);
                return null;
            }
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            getRequest.abort();
            Log.e("ImageDownloader", "error while retrieving image " + url + e.toString());
        } 
        return null;
    }
}