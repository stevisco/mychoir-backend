package org.songdb.backendapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songdb.backendapi.model.Song;
import org.songdb.backendapi.model.SongListFilter;
import org.songdb.backendapi.model.UserLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
public class SongController {

	@Autowired
	private SongService songService;
	
	@Autowired
	private Environment env;
	
	private static final Logger logger = LoggerFactory.getLogger(SongController.class);	
	
	/*
	private void checkAuth(String token) throws AuthException {
		if (!token.equals("AAAA")) throw new AuthException("Request not authorized, perform valid /auth first");
	}
	*/
	
	@RequestMapping("/auth")
	public UserLogin auth(@RequestHeader("x-auth-request")String xarequest) {
		UserLogin res = null;
		logger.info("AUTH CALL "+xarequest);
		//expecting format user;secret
		String[] fields = xarequest.split(";");
		String secret = env.getProperty("xauth.secret","mySecret");
		String xauth="AAAA";
		if (fields!=null&&fields.length==2&&fields[1].equalsIgnoreCase(secret)){
			res = new UserLogin(HttpStatus.OK.value(),"Login successful",xauth);
		}
		else {
			res = new UserLogin(HttpStatus.UNAUTHORIZED.value(),"Login failed",null);
		}
		
		return res;
	}
	
	
	@RequestMapping("/songs")
	public List<Song> songs() throws AuthException{
		return songService.getSongs();
	}
	
	@RequestMapping("/songs/{alphasel}")
	public List<Song> songsAlpha(@PathVariable String alphasel){
		return songService.getSongsAlpha(alphasel);
	}
	
	@RequestMapping("/songssearch/{searchexp}")
	public List<Song> songsSearch(@PathVariable String searchexp){
		return songService.getSongsSearch(searchexp);
	}
	
	@RequestMapping("/songsbytag/{tag}")
	public List<Song> songsByTag(@PathVariable String tag){
		return songService.getSongsByTag(tag);
	}
	
	@RequestMapping("/songsfilter")
	public SongListFilter songsFilter(){
		return songService.getSongsFilter();
	}
	
	@RequestMapping("/song/{id}")
	public Song getSong(@PathVariable String id) {
		Song s = songService.getSong(id);
		return s;
	}
	
	@RequestMapping(method=RequestMethod.POST,value="/songs") 
	public Song addSong(@RequestBody Song song) {
		return songService.addSong(song);
	}
	
	@RequestMapping(path = "/songattachdownload", method = RequestMethod.GET)
	public ResponseEntity<Resource> download(String name) throws IOException {

		logger.info("DOWNLOAD CALL "+name);
		if (name==null) {
			return ResponseEntity.badRequest().body(null);
		}
		String base = System.getProperty("user.dir");
		File file = new File(base+"/attachments/"+name);
		
	    InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
	    HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        //inline ?  attachment
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
        		"attachment;filename=" + file.getName());
         
	    return ResponseEntity.ok() 
	    		.headers(headers)
	            .contentLength(file.length())
	            .contentType(MediaType.parseMediaType("application/octet-stream"))
	            .body(resource);
	}
	
	@RequestMapping(path = "/songattachupload", method = RequestMethod.POST)
	public String upload(@RequestParam("upload_file")MultipartFile file,@RequestParam("filename")String filename) throws IOException {
		
		String base = System.getProperty("user.dir");
		String dest = base+"/attachments/"+filename;
		logger.info("UPLOAD CALL "+filename+" saving to "+dest);
		
		file.transferTo(new File(dest));
				
	    return "Upload completed successfully";
		
	}

}
