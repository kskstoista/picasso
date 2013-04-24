package com.my.picasso;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


public class PicasaAlbumActivity extends ListActivity {
	
	private String albumName;
	private String albumId;
	private String googleKey;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picasa_album);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent ntent   = getIntent();
		albumName  = ntent.getStringExtra("ALBUM_NAME");
		albumId  = ntent.getStringExtra("ALBUM_ID");
		googleKey  = ntent.getStringExtra("AUTH");
				
		Toast t = Toast.makeText(this, "Album "+albumName+" ID:"+albumId, Toast.LENGTH_SHORT);
		t.show();
	    PicasaListAdapter adapter = new PicasaListAdapter(this);
        this.setListAdapter(adapter);                                    

        AlbumPicturesDownloader loadPicData = new AlbumPicturesDownloader(adapter,
                             "https://picasaweb.google.com/data/feed/api/user/default/albumid/"+albumId,
                             googleKey);
         loadPicData.execute();   
         Log.d("PicasaAlbum","Displayed");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_picasa_album, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			//NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	 protected void onListItemClick(ListView l, View v, int position, long id) {
		
	    Log.d("Album",((PicasaEntry)this.getListAdapter().getItem(position)).getUrl());
        Intent startImageView = new Intent(this,PicasaImageViewActivity.class);
        startImageView.putExtra("IMAGE_URL",((PicasaEntry)this.getListAdapter().getItem(position)).getUrl() );
        startImageView.putExtra("IMAGE_NAME",((PicasaEntry)this.getListAdapter().getItem(position)).getTitle());
        startImageView.putExtra("IMAGE_SUMMARY",((PicasaEntry)this.getListAdapter().getItem(position)).getTitle());
        startActivity(startImageView);
	 }

}
