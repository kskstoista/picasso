package com.my.picasso;

import java.io.FileOutputStream;

import android.os.Bundle;
import android.os.Environment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.my.picasso.AlbumListDownloader;

public class PicasaMainActivity extends SherlockActivity {

	private String    googleKey;
	private ListView  albumList;
	private int       runingPictureNumber = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picasa_main);
		albumList = (ListView)findViewById(R.id.album_list);
        albumList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.d("Clicked","Item"+arg2);
				Intent startAlbum = new Intent(PicasaMainActivity.this,PicasaAlbumActivity.class);
	            startAlbum.putExtra("ALBUM_NAME",((PicasaEntry)albumList.getAdapter().getItem(arg2)).getTitle() );
	            startAlbum.putExtra("ALBUM_ID",((PicasaEntry)albumList.getAdapter().getItem(arg2)).getSummary() );
	            startAlbum.putExtra("AUTH",googleKey );
	            startActivity(startAlbum);
			}
          });
        
		googleKey = getAuthString();

		SharedPreferences sp = getPreferences(MODE_PRIVATE);
		runingPictureNumber = sp.getInt("runningPicNum",0);

        if(googleKey == ""){
        	Toast t = Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT);
    		t.show();
        }else{
        	PicasaListAdapter adapter = new PicasaListAdapter(this);
            albumList.setAdapter(adapter);                                    

            AlbumListDownloader loadPicData = new AlbumListDownloader(adapter,
                                 "https://picasaweb.google.com/data/feed/api/user/default/",
                                 googleKey);
            loadPicData.execute();   
            Log.d("Main","Oncreate finish");
        }
    }
    
	private void startAlbumlistView(String gauth){
		PicasaListAdapter adapter = new PicasaListAdapter(this);
        albumList.setAdapter(adapter);                                    

        AlbumListDownloader loadPicData = new AlbumListDownloader(adapter,
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
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    //super.onActivityResult(requestCode, resultCode, data);
	    Log.d("Activityresult","Start");
	    switch(requestCode){
	    //Got auth String from login Activity
	    case 1:
            if(data.getExtras().containsKey("AUTH")){
                googleKey = data.getStringExtra("AUTH");
                Log.d("Activityresult","Auth got");
                SharedPreferences sp = getPreferences(MODE_PRIVATE);
    	        sp.edit().putString("gauth",googleKey).commit();  
                startAlbumlistView(googleKey);
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
		 if(getPreferences(MODE_PRIVATE).getString("gauth","") == ""){
	        menu.add("Login")
	        //.setIcon(R.drawable.ic_compose)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	        //return true;
		 }else{
			 menu.add("Logout")
		        //.setIcon(R.drawable.ic_compose)
		        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		        //return true; 
		 }
		 menu.add("Photo")
	     .setIcon(android.R.drawable.ic_menu_camera)
	     .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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
	        	 item.setTitle("Logout");
	             return true;
	         }else if(item.getTitle() == "Login"){
	        	 googleKey = "";
	        	 item.setTitle("Login");
	        	 PicasaListAdapter adapter = new PicasaListAdapter(this);
	        	 SharedPreferences sp = getPreferences(MODE_PRIVATE);
	     	     sp.edit().putString("gauth","").commit();
	             albumList.setAdapter(adapter); 
	        	 return true;
	         }else{
             	 Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			     startActivityForResult(cameraIntent, 2); 
	         }
		 default:	 
	         return super.onOptionsItemSelected(item);
		 }        
	 }
}
