package com.my.picasso;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PicasaListAdapter extends BaseAdapter {
	 
    private Context mContext;

    private LayoutInflater mLayoutInflater;                              
    private ArrayList<PicasaEntry> mEntries = new ArrayList<PicasaEntry>();         

    private final ImageDownloader mImageDownloader;                      

    public PicasaListAdapter(Context context) {                          
       mContext = context;
       mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       mImageDownloader = new ImageDownloader();
    }

    @Override
    public int getCount() {
       return mEntries.size();
    }

    @Override
    public Object getItem(int position) {
       return mEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
       return position;
    }

    @Override
    public View getView(int position, View convertView,
          ViewGroup parent) {                                           
       RelativeLayout itemView;
       if (convertView == null) {                                        
          itemView = (RelativeLayout) mLayoutInflater.inflate(
                   R.layout.list_item_layout, parent, false);

       } else {
          itemView = (RelativeLayout) convertView;
       }

       ImageView imageView = (ImageView)
          itemView.findViewById(R.id.listImage);                        
       TextView titleText = (TextView)
          itemView.findViewById(R.id.listTitle);                        
       TextView descriptionText = (TextView)
          itemView.findViewById(R.id.listDescription);                  

       String imageUrl = mEntries.get(position).getUrl();   
       mImageDownloader.download(imageUrl, imageView);                   

       String title = mEntries.get(position).getTitle();
       titleText.setText(title);
       String description =
          mEntries.get(position).getSummary();
       if (description.trim().length() == 0) {
          description = "Sorry, no description for this image.";
       }
       descriptionText.setText(description);

       return itemView;
    }

    public void upDateEntries(ArrayList<PicasaEntry> entries) {
       mEntries = entries;
       notifyDataSetChanged();
    }	

}
