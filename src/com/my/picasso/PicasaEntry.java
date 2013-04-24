package com.my.picasso;

public class PicasaEntry {
	private String title;
	private String summary;
	private String url;
	
	public PicasaEntry(String pTitle, String pSummary, String pUrl ){
		this.title    = pTitle;
		this.summary  = pSummary;
		this.url      = pUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
