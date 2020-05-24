package de.aspera.dataexport.Repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.aspera.dataexport.util.json.ExportJsonCommand;
import de.aspera.dataexport.util.json.TableQuery;

public interface TableQueryRepository extends CrudRepository<TableQuery, Integer>{
	
	List<TableQuery> findByExportCommand(ExportJsonCommand exportCommand);

	TableQuery findById(int id);

}
