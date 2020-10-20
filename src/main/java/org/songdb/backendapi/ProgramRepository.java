package org.songdb.backendapi;

import org.songdb.backendapi.model.Program;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ProgramRepository extends CrudRepository<Program, String> {

	@Query("select p from Program p where lower(p.title) like %?1% or lower(p.id) like %?1%  order by id asc")
	public Iterable<Program> findByCustomSearch(String search);

	
	public Iterable<Program> findAllByOrderById();
}
