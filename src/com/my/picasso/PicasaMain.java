package com.my.picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import org.json.*;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.my.picasso.AlbumListDownloader;

public class PicasaMain extends ListActivity {

	private AlbumListDownloader ad;
	private String[] albumNames;
	private String[] albumIds;
	private String googleKey;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picasa_main);
		 googleKey = getAuthString();
		
        if(googleKey != ""){
        	ad = new AlbumListDownloader(this);
        	ad.execute(googleKey);
        	
            Log.d("Main","Oncreate finish");
        }
}
	
	 public String[] getAlbumNames() {
		return albumNames;
	}

	public void setAlbumNames(String[] albumNames) {
		this.albumNames = albumNames;
	}

	public String[] getAlbumIds() {
		return albumIds;
	}

	public void setAlbumIds(String[] albumIds) {
		this.albumIds = albumIds;
	}

	@Override
	 protected void onListItemClick(ListView l, View v, int position, long id) {
		 Toast wrongToast = Toast.makeText(this, "Clicked "+albumNames[position]+" ID:"+albumIds[position], Toast.LENGTH_SHORT);
			wrongToast.show();
         Intent startAlbum = new Intent(this,PicasaAlbum.class);
         startAlbum.putExtra("ALBUM_NAME",albumNames[position] );
         startAlbum.putExtra("ALBUM_ID",albumIds[position] );
         startAlbum.putExtra("AUTH",googleKey );
         startActivity(startAlbum);
	 }
	
	public String getAuthString(){
	    String googleKey = new String();
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("https://www.google.com/accounts/ClientLogin");

        List<NameValuePair> loginValues = new ArrayList<NameValuePair>(2);
        loginValues.add(new BasicNameValuePair("accountType", "GOOGLE"));
        loginValues.add(new BasicNameValuePair("Email", "ferenc.muskatal@gmail.com"));
        loginValues.add(new BasicNameValuePair("Passwd", "evogsnx1.pac"));
        loginValues.add(new BasicNameValuePair("service", "lh2"));
        loginValues.add(new BasicNameValuePair("source", "Picasso"));
        try{
            httppost.setEntity(new UrlEncodedFormEntity(loginValues));
		    HttpResponse response = httpclient.execute(httppost);
    	
	        InputStream responseStream = null;
    	    BufferedReader responseReader = null;
			
	        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK ){
                try {
	   		        responseStream = response.getEntity().getContent();
	    	        responseReader = new BufferedReader(new InputStreamReader(responseStream));
	    		   	String responseLine = "";
	    	        while ((responseLine = responseReader.readLine())!=null) {
                        if(responseLine.contains("Auth")) googleKey=responseLine.split("=")[1];
                    }
	            } catch (IllegalStateException e) {
	    		    e.printStackTrace();
	            } catch (IOException e) {
    	    		e.printStackTrace();
	            }
	    }
		Log.d("main", "authentiated");
    	}catch (Exception e){
    		e.printStackTrace();	
    	}
        return googleKey;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_picasa_main, menu);
		return true;
	}

}
