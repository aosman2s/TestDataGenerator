package de.aspera.dataexport.Repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.aspera.dataexport.util.json.ExportJsonCommand;
import de.aspera.dataexport.util.json.JsonDatabase;


public interface JsonExportCommandRepository extends CrudRepository<ExportJsonCommand, Integer> {

	List<ExportJsonCommand> findByDataBase(JsonDatabase dataBase);

	ExportJsonCommand findById(int id);
}
