package com.my.picasso;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;


public class PicasaImageViewActivity extends Activity {

	
	private String imageUrl;
	private String imageName;
	private String imageSummary;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picasa_image_view);
		
		Intent intent   = getIntent();
		imageUrl  = intent.getStringExtra("IMAGE_URL");
		imageName  = intent.getStringExtra("IMAGE_NAME");
		imageSummary  = intent.getStringExtra("IMAGE_SUMMARY");
		
		ImageDownloader id = new ImageDownloader();
		id.download(imageUrl,(ImageView) findViewById(R.id.picasa_image));
		Toast t = Toast.makeText(this, "Picature "+imageName+" Summary:"+imageSummary, Toast.LENGTH_SHORT);
		t.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_picasa_image_view, menu);
		return true;
	}

}
