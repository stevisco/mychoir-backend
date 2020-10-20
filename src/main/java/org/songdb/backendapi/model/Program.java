package org.songdb.backendapi.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Program {

	public Program() {
		
	}
	
	
	public Program(String id, String title) {
		super();
		this.id = id;
		this.title = title;
	}
	
	@Id
	private String id;
	
	private String title;

	private String jsonSummary;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getJsonSummary() {
		return jsonSummary;
	}
	public void setJsonSummary(String jsonSummary) {
		this.jsonSummary = jsonSummary;
	}
	
	
	
}
