package org.songdb.backendapi;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

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
        String[] attach = new String[paths.length];
        int i = 0;
        for(File path:paths) {
            attach[i] = path.getName();
            i++;
        }
        if (res.isPresent()) {
        	Song s = res.get();
        	s.setAttachments(attach);
        	return s;
        }
        else {
        	throw new IllegalArgumentException("Not found: "+id);
        }
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
