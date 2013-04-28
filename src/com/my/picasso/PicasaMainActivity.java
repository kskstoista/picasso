package com.my.picasso;

import java.io.FileOutputStream;

import android.os.Bundle;
import android.os.Environment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import android.widget.Toast;


import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.my.picasso.AlbumListDownloader;

public class PicasaMainActivity extends SherlockFragmentActivity 
      implements PicassoMainFragment.OnAlbumClickedListener{

	private String              googleKey;
	private int                 runingPictureNumber = 0;
	private MenuItem            loginButton;
	private PicassoMainFragment albumListFragment;
	private boolean             albumsdisplayed = true;
	PicasaListAdapter           albumAdapter;
	PicasaListAdapter           picturesAdapter;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picasa_main);
		//Start album Fragment
		if (findViewById(R.id.main_fragment) != null) {
            if (savedInstanceState != null) {
                return;
            }
            albumListFragment = new PicassoMainFragment();
            albumListFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment, albumListFragment).commit();
        }
		//Read out saved authentication string
		googleKey =  getAuthString();

		SharedPreferences sp = getPreferences(0);
		runingPictureNumber = sp.getInt("runningPicNum",0);

	    if(googleKey == ""){
	        Toast t = Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT);
	    	t.show();
	    }else{
            //If there is saved auth string start downloading albums
	        albumAdapter = new PicasaListAdapter(this);
			Log.d("Main","before setadapter"+albumListFragment+" adapter:"+albumAdapter);
	        albumListFragment.setListAdapter(albumAdapter);                                    

	        AlbumListDownloader loadPicData = new AlbumListDownloader(albumAdapter,
	                             "https://picasaweb.google.com/data/feed/api/user/default/",
	                             googleKey);
	        loadPicData.execute();   
	    }
    }
    
	private void startAlbumlistView(String gauth){
		albumAdapter = new PicasaListAdapter(this);
        albumListFragment.setListAdapter(albumAdapter);                                    

        AlbumListDownloader loadPicData = new AlbumListDownloader(albumAdapter,
                             "https://picasaweb.google.com/data/feed/api/user/default/",
                             gauth);
         loadPicData.execute();   
         Log.d("PicasaAlbum","Displayed");
        Log.d("Main","Oncreate finish");
	}
	
	public String getAuthString(){
	    String googleKey = "";
	    SharedPreferences sp = getPreferences(MODE_PRIVATE);
	    googleKey = sp.getString("gauth","");
		return googleKey;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Log.d("Activityresult","Start");
	    if (resultCode != RESULT_OK) {
	    	return;
	    }
	    switch(requestCode){
	    /*Got auth String from login Activity
	     * change caption on login button to logout
	     * and start downloading albums*/    
	    case 1:
            if(data.getExtras().containsKey("AUTH")){
                googleKey = data.getStringExtra("AUTH");
                Log.d("Activityresult","Auth got");
                if(googleKey !=""){
                    SharedPreferences sp = getPreferences(MODE_PRIVATE);
    	            sp.edit().putString("gauth",googleKey).commit();  
                    startAlbumlistView(googleKey);
                    loginButton.setTitle("Logout");
                }
            }
            break;
        //Got photo from camera    
        case 2:  
        	Bitmap photo = (Bitmap) data.getExtras().get("data");

            try {
                FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory()+String.valueOf(runingPictureNumber));
                photo.compress(Bitmap.CompressFormat.JPEG, 90, out);
                SharedPreferences sp = getPreferences(MODE_PRIVATE);
                runingPictureNumber+=1;
    	        sp.edit().putInt("runningPicNum",runingPictureNumber).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
         default:
        	break;
        }
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 menu.add("Photo").setIcon(android.R.drawable.ic_menu_camera)
	     .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		 menu.add("Gallery").setIcon(android.R.drawable.ic_menu_gallery)
	     .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		 
		 if(getPreferences(MODE_PRIVATE).getString("gauth","") == ""){
	        menu.add("Login").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		 }else{
			menu.add("Logout").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		 }
	     return true;
	 }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	     Log.d("click","itemid:"+item.getItemId());
	     Log.d("click","title:"+item.getTitle());
		 switch(item.getItemId()){    
		 case 0:
		    if(item.getTitle() == "Login") {
	        	 Intent intent = new Intent(this, PicasaLoginActivity.class);
	        	 startActivityForResult(intent,1);
	        	 loginButton = item;
	             return true;
	         }else if(item.getTitle() == "Logout"){
	        	 googleKey = "";
	        	 item.setTitle("Login");
	        	 PicasaListAdapter adapter = new PicasaListAdapter(this);
	        	 SharedPreferences sp = getPreferences(MODE_PRIVATE);
	     	     sp.edit().putString("gauth","").commit();
	             albumListFragment.setListAdapter(adapter); 
	        	 return true;
	         }else if(item.getTitle() == "Gallery"){
	        	 Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
	        	 photoPickerIntent.setType("image/*");
	        	 startActivityForResult(photoPickerIntent, 3); 
	         }
		 default:	 
	         return super.onOptionsItemSelected(item);
		 }        
	 }

	/*Albums and Album list uses the same fragment 
	 * only adapter is changed behind the scenes*/
	@Override
	public void OnAlbumClicked(PicasaEntry itemSelected) {
		if (albumsdisplayed){
		    albumsdisplayed = false;
		    picturesAdapter = new PicasaListAdapter(this);
            albumListFragment.setListAdapter(picturesAdapter); 
            AlbumPicturesDownloader loadPicData = new AlbumPicturesDownloader(picturesAdapter,
                       "https://picasaweb.google.com/data/feed/api/user/default/albumid/"+itemSelected.getSummary(),
                       googleKey);
            loadPicData.execute();   
	    }else{
	    	Intent startImageView = new Intent(this,PicasaImageViewActivity.class);
	        startImageView.putExtra("IMAGE_URL",itemSelected.getUrl() );
	        startImageView.putExtra("IMAGE_NAME",itemSelected.getTitle());
	        startImageView.putExtra("IMAGE_SUMMARY",itemSelected.getSummary());
	        startActivity(startImageView);
	    }
	}
	
	/*Because of same fragment handle back button*/
	@Override
	public void onBackPressed() {
	    if(!albumsdisplayed){
	    	albumsdisplayed = true;
	    	albumListFragment.setListAdapter(albumAdapter);
	    	return;
	    }
	    super.onBackPressed();
	}
}
