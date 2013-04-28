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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PicasaLoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picasa_login);
		
		
	}

	public void onLoginClick(View v){
		AuthDownloader ad = new AuthDownloader("");
	    ad.execute();
	}

	
	public void returnAuth(String auth) {
		Log.d("Login","return Auth");
		if( auth != ""){
			Log.d("Login","return auth Gou auth string");
        	Intent i = getIntent();
       	    i.putExtra("AUTH", auth);
        	setResult(RESULT_OK, i);
        	finish();
        }else{
    		Toast t = Toast.makeText(this, "Authentication failed!", Toast.LENGTH_SHORT);
    		t.show();
        }
	}
	
	private class AuthDownloader extends AsyncTask<Void, Void, String> {                          

	    private String googleKey ="";

	    public AuthDownloader(String auth) {           
	       googleKey    = auth;
	    }

	    @Override
	    protected String doInBackground(Void... params) {
	    	HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("https://www.google.com/accounts/ClientLogin");

	        List<NameValuePair> loginValues = new ArrayList<NameValuePair>(2);
	        loginValues.add(new BasicNameValuePair("accountType", "GOOGLE"));
	        loginValues.add(new BasicNameValuePair("Email",((TextView)findViewById(R.id.emailEditText)).getText().toString()));
	        loginValues.add(new BasicNameValuePair("Passwd", ((TextView)findViewById(R.id.passwordEditText)).getText().toString()));
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
		        }else{
		    		googleKey = "";
		    		((TextView)findViewById(R.id.passwordEditText)).setText("");
		        }
			    Log.d("main", "authentication");
	    	}catch (Exception e){
	    		e.printStackTrace();	
	    	}
	        Log.d("Picasalogin",googleKey);
	        return googleKey;
	    }

	    protected void onPostExecute(String googleKey) {
	    	Log.d("Login","Onpostexecute");
	    	returnAuth(googleKey);                        
	    }
	    
		
	}

}
