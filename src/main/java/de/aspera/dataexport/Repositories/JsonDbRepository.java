package de.aspera.dataexport.Repositories;

import org.springframework.data.repository.CrudRepository;

import de.aspera.dataexport.util.json.JsonDatabase;

public interface JsonDbRepository extends CrudRepository<JsonDatabase, Integer>{
	
	
	JsonDatabase findById(int id);

}
