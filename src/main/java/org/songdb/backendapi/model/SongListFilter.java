package org.songdb.backendapi.model;

public class SongListFilter {

	private String[] alpha;
	private String[] tags;
	
	public SongListFilter() {
		
	}
	public SongListFilter(String[] alpha, String[] tags) {
		super();
		this.alpha = alpha;
		this.tags = tags;
	}
	
	public String[] getAlpha() {
		return alpha;
	}
	public void setAlpha(String[] alpha) {
		this.alpha = alpha;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	
}
