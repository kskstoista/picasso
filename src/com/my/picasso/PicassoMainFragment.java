package com.my.picasso;


import com.actionbarsherlock.app.SherlockListFragment;


import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

	public class PicassoMainFragment extends SherlockListFragment {
		
		OnAlbumClickedListener mCallback;

	    public interface OnAlbumClickedListener {
	        public void OnAlbumClicked(PicasaEntry itemSelected);
	    } 
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
	        return inflater.inflate(R.layout.picasa_main_fragment, container, false);
	    }
	    
		
		public String getAuthString(){
		    String googleKey = "";
		    SharedPreferences sp = getActivity().getPreferences(0);
		    googleKey = sp.getString("gauth","");
			return googleKey;
		}
		
		@Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
            mCallback = (OnAlbumClickedListener) activity;
		}
            
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.d("Clicked","Item"+position);
			mCallback.OnAlbumClicked((PicasaEntry)this.getListAdapter().getItem(position));
					
			
		}
	
}
