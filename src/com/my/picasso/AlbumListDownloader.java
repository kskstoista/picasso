package com.my.picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;



import android.os.AsyncTask;
import android.util.Log;

public class AlbumListDownloader extends AsyncTask<Void, Void, ArrayList<PicasaEntry>> {                          

    private String picasaUrl = "" ;
    private String googleKey ="";
    private final PicasaListAdapter albumAdapter;
 

    public AlbumListDownloader(PicasaListAdapter adapter,String url,String auth) {           
       albumAdapter = adapter;
       picasaUrl     = url;
       googleKey    = auth;
    }

    @Override
    protected ArrayList<PicasaEntry> doInBackground(Void... params) {
    	 
    	 HttpClient httpclient = new DefaultHttpClient();
		 HttpGet getAction = new HttpGet(picasaUrl);
         getAction.setHeader("Authorization", "GoogleLogin auth="+googleKey);
         Log.d("LoadAlbus","Started");
         try{
		     HttpResponse response = httpclient.execute(getAction);
	         InputStream responseStream = null;
    	     BufferedReader responseReader = null;
			 String jsonArray ="";
	         if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK ){
                 try {
	   		         responseStream = response.getEntity().getContent();
	    	         responseReader = new BufferedReader(new InputStreamReader(responseStream));
	    	         String responseLine = "";
	    	         while ((responseLine = responseReader.readLine())!=null){
	    	        	 jsonArray += responseLine;
	    	        	 Log.d("PicasaAlbumList","collect resp");
	    	        }
	    	        return parsePictures(jsonArray);
	             } catch (IllegalStateException e) {
	    		    e.printStackTrace();
	             } catch (IOException e) {
    	    		e.printStackTrace();
	             }
	         }else{
	        	 Log.d("PicasaAlbum","HTTP status not OK"+response.getStatusLine().getStatusCode());
	         }
	      }catch(Exception e){
	        	  e.printStackTrace();
	      }
         Log.d("LoadAlbumPictures","Return");
         return new  ArrayList<PicasaEntry>();
    }

    protected void onPostExecute(ArrayList<PicasaEntry> entries) {
    	Log.d("LoadAlbumPictures","onpostexecute");
        albumAdapter.upDateEntries(entries);                        
    }
    
	private ArrayList<PicasaEntry> parsePictures(String xml)
    {
	  ArrayList<PicasaEntry> pictList = new  ArrayList<PicasaEntry>();
      try {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db         = dbf.newDocumentBuilder();
        InputSource is             = new InputSource();
        is.setCharacterStream(new StringReader(xml));

        Document doc     = db.parse(is);
        NodeList entries = doc.getElementsByTagName("entry");
        Node   temp;

        // loop all album entries and store picture ID, Summary, and URL
        for (int i = 0; i < entries.getLength(); i++) {
            Element element = (Element) entries.item(i);
            //node "content"
            temp = (Node)element.getElementsByTagName("media:group").item(0);

        	PicasaEntry p = new PicasaEntry(getCharacterDataFromElement((Element)element.getElementsByTagName("title").item(0)),
        			                      getCharacterDataFromElement((Element)element.getElementsByTagName("gphoto:id").item(0)),
        			                      temp.getChildNodes().item(4).getAttributes().item(0).getNodeValue());
        	Log.d("ProcessPictures","sdfurl:"+p.getUrl() );
       	pictList.add(p);
        }
        
      } catch (Exception e) {
          e.printStackTrace();
      }
      return pictList;
    }

    private String getCharacterDataFromElement(Element element)
    {
      Node child = element.getFirstChild();
      
      if (child instanceof CharacterData) {
         CharacterData cd = (CharacterData) child;
         return cd.getData();
      }
      
      return "";
    }
}
