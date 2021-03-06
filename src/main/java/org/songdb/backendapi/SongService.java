package org.songdb.backendapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songdb.backendapi.model.Song;
import org.songdb.backendapi.model.SongListFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

 
@Service
public class SongService {

	@Autowired
	private SongRepository songRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(SongService.class);	
	
	public List<Song> getSongs(){
		logger.info("GET SONGS call -------------------");
		List<Song> res= new ArrayList<Song>();
		songRepository.findAllByOrderById().forEach(res::add);
		return res;
	}

	public Song getSong(String id) {
		Optional<Song> res=songRepository.findById(id);
		//retrieve attachment list
		FilenameFilter fileNameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
               if(name.startsWith(id.toUpperCase()+"_")) return true;
               return false;
            }
        };
        File f = new File("./attachments");
        File[] paths = f.listFiles(fileNameFilter);
        Set<String> attach = null;
		Set<String> attachLinks = null;
		StringBuffer fulltext = null;
        if (paths!=null) {
	        attach = new HashSet<String>();
	        for(File path:paths) {
	            if (path.getName().endsWith(".url")) {
	            	//this attachment is a file containing a URL to open. add this information as object attachmentLinks
	            	//open the file to extract the URL
	            	String url = extractUrlFromFile(path.getName());
	            	if (url!=null) {
	            		if (attachLinks==null) attachLinks = new HashSet<String>();
	            		attachLinks.add(url);
	            	}
				}
				else if (path.getName().endsWith(".txt")){
					//this attachment is the full text of this song, so needs to be read and added to song object
					fulltext = extractFulltextFromFile(path.getName());
				}
	            else {
	            	attach.add(path.getName());
	            }
	        }
        }
        if (res.isPresent()) {
        	Song s = res.get();
        	if (attach!=null) s.setAttachments(attach.toArray(new String[0]));
			if (attachLinks!=null) s.setAttachmentsLinks(attachLinks.toArray(new String[0]));
			if (fulltext!=null) s.setFulltext(fulltext.toString());
        	return s;
        }
        else {
        	throw new IllegalArgumentException("Not found: "+id);
        }
	}

	private String extractUrlFromFile(String name) {
		String url = null;
		String base = System.getProperty("user.dir");
		Pattern p = Pattern.compile(".*URL=(.*)");
		try {
			LineNumberReader fr = new LineNumberReader(
				new InputStreamReader(
                new FileInputStream(base+"/attachments/"+name),"UTF-8"));
			
			String line = null;
			while ((line=fr.readLine())!=null){
				Matcher m = p.matcher(line);
				if (m.matches()) {
					url = m.group(1);
				}
			}
			fr.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return url;
	}

	private StringBuffer extractFulltextFromFile(String name) {
		StringBuffer txt = null;
		String base = System.getProperty("user.dir");
 
		try {
			LineNumberReader fr = new LineNumberReader(
				new InputStreamReader(
                new FileInputStream(base+"/attachments/"+name),"UTF-8"));
			
			String line = null;
			while ((line=fr.readLine())!=null){
				if (txt==null) txt=new StringBuffer();
				txt.append(line+"\n");
			}
			fr.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return txt;
	}

	public Song addSong(Song song) {
		songRepository.save(song);
		return song;
	}

	public List<Song> getSongsAlpha(String alphasel) {
		List<Song> res= new ArrayList<Song>();
		Iterator<Song> elit = songRepository.findAll().iterator();
		while (elit.hasNext()) {
			Song s = elit.next();
			if (s.getTitle()!=null&&s.getTitle().toUpperCase().startsWith(alphasel.toUpperCase())) {
				res.add(s);
			}
		}
		return res;
	}

	public List<Song> getSongsSearch(String searchexp) {
		logger.info("GET SONGS SEARCH call -------------------");
		List<Song> res= new ArrayList<Song>();
		songRepository.findByCustomSearch(searchexp.toLowerCase()).forEach(res::add);
		return res;
	}

	public SongListFilter getSongsFilter() {
		logger.info("GET SONGS FILTER call -------------------");
		
		Iterable<Song> list = songRepository.findAll();
		Set<String> alpha = new TreeSet<String>();
		Set<String> tags = new TreeSet<String>();
		for (Iterator<Song> it = list.iterator(); it.hasNext();) {
			Song s = it.next();
			if (s.getTitle()!=null)
				alpha.add(s.getTitle().substring(0,1));
			if (s.getTags()!=null)
				for (int i=0;i<s.getTags().length;i++) tags.add(s.getTags()[i]);
		}
		SongListFilter slf = new SongListFilter(alpha.toArray(new String[0]),tags.toArray(new String[0]));
		return slf;
	}

	public List<Song> getSongsByTag(String tag) {
		logger.info("GET SONGS BY TAG call -------------------");
		List<Song> res= new ArrayList<Song>();
		for (Iterator<Song> it=songRepository.findAllByOrderById().iterator();it.hasNext();) {
			Song s = it.next();
			String[] tags = s.getTags();
			boolean found = false;
			if (tags!=null) {
				for (int i=0;i<tags.length;i++) if (tags[i]!=null&&tags[i].equalsIgnoreCase(tag)) found = true;
			}
			if (found) res.add(s);
		}
		return res;
	}
	
	
}
