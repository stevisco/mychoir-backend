package org.songdb.backendapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songdb.backendapi.model.Program;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

 
@Service
public class ProgramService {


	@Autowired
	private ProgramRepository programRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(ProgramService.class);	
	

	public List<Program> getPrograms(){
		logger.info("GET PROGRAMS call -------------------");
		List<Program> res= new ArrayList<Program>();
		programRepository.findAllByOrderById().forEach(res::add);
		return res;
	}

	public Program addProgram(Program program) {
		programRepository.save(program);
		return program;
	}

	public Program getProgram(String id) {
		Optional<Program> res=programRepository.findById(id);
		if (res.isPresent()) {
        	Program s = res.get();
        	return s;
        }
        else {
        	throw new IllegalArgumentException("Not found: "+id);
        }
	}

}
