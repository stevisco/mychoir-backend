package org.songdb.backendapi;

import org.songdb.backendapi.model.Song;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SongRepository extends CrudRepository<Song, String> {

	@Query("select s from Song s where lower(s.title) like %?1% or lower(s.id) like %?1%  order by id asc")
	public Iterable<Song> findByCustomSearch(String search);

	
	public Iterable<Song> findAllByOrderById();
}
