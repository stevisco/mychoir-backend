package org.songdb.backendapi.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Song {

	public Song() {
		
	}
	
	public Song(String id, String title, String author, String genre,String[] tags,String ref1,String ref2,String album,String yearpublished,String publisher) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.genre = genre;
		this.tags = tags;
		this.ref1=ref1;
		this.ref2=ref2;
		this.album=album;
		this.yearpublished=yearpublished;
		this.publisher=publisher;
	}
	public Song(String id, String title, String author, String genre,String[] tags,String ref1,String ref2,String album,String yearpublished,String publisher,String[] attachments,String[] attachmentsLinks,String fulltext) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.genre = genre;
		this.tags=tags;
		this.ref1=ref1;
		this.ref2=ref2;
		this.album=album;
		this.yearpublished=yearpublished;
		this.publisher=publisher;
		this.attachments = attachments;
		this.attachmentsLinks = attachmentsLinks;
		this.fulltext = fulltext;
	}
	
	@Id
	private String id;
	
	private String title;
	
	private String author;
	
	private String genre;
	private String authorText;
	private String[] attachments;
	private String[] attachmentsLinks;
	private String[] tags;
	private String ref1;
	private String ref2;
	private String album;
	private String yearpublished;
	private String publisher;
	private String fulltext;
	
	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getFulltext() {
		return fulltext;
	}

	public void setFulltext(String fulltext) {
		this.fulltext = fulltext;
	}

	public String getYearpublished() {
		return yearpublished;
	}

	public void setYearpublished(String yearpublished) {
		this.yearpublished = yearpublished;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getRef1() {
		return ref1;
	}

	public void setRef1(String ref1) {
		this.ref1 = ref1;
	}

	public String getRef2() {
		return ref2;
	}

	public void setRef2(String ref2) {
		this.ref2 = ref2;
	}

	
	
	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String[] getAttachments() {
		return attachments;
	}

	public void setAttachments(String[] attachments) {
		this.attachments = attachments;
	}

	public String getAuthorText() {
		return authorText;
	}

	public void setAuthorText(String authorText) {
		this.authorText = authorText;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

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
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}

	public void setAttachmentsLinks(String[] attachmentsLinks) {
		this.attachmentsLinks = attachmentsLinks;
	}
	public String[] getAttachmentsLinks() {
		return attachmentsLinks;
	}
	
}
