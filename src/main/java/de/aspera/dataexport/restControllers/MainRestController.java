package de.aspera.dataexport.restControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.aspera.dataexport.Repositories.JsonDbRepository;
import de.aspera.dataexport.Repositories.JsonExportCommandRepository;
import de.aspera.dataexport.Repositories.TableQueryRepository;
import de.aspera.dataexport.util.json.ExportJsonCommand;
import de.aspera.dataexport.util.json.JsonDatabase;
import de.aspera.dataexport.util.json.TableQuery;

@RestController
public class MainRestController {

	@Autowired
	private JsonDbRepository jsonDbRepository;

	@Autowired
	private JsonExportCommandRepository jsonExportCommandRepository;
	
	@Autowired
	private TableQueryRepository   tableQueryRepository;

	@GetMapping("/")
	public String homePage() {
		return "enta weslt";
	}

	@PostMapping(value="/connection", consumes= {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.CREATED)
	public void createConnection(@RequestBody JsonDatabase jsonDatabase) {
		jsonDbRepository.save(jsonDatabase);
		System.out.println(jsonDatabase.toString());
	}

	@PostMapping(value="connection/{connectionId}/exportCommand", consumes= {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.CREATED)
	public void createExportCommand(@PathVariable int connectionId, @RequestBody ExportJsonCommand exportJsonCommand) {
		JsonDatabase database = jsonDbRepository.findById(connectionId);
		exportJsonCommand.setDataBase(database);
		jsonExportCommandRepository.save(exportJsonCommand);
		System.out.println(exportJsonCommand.toString());
		System.out.println(database.toString());
	}
	
	@PostMapping(value="exportCommand/{cmdId}/table", consumes= {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.CREATED)
	public void addTableQueryToCommand(@PathVariable int cmdId, @RequestBody TableQuery tableQuery) {
		ExportJsonCommand exportCommand = jsonExportCommandRepository.findById(cmdId);
		tableQuery.setExportCommand(exportCommand);
		tableQueryRepository.save(tableQuery);
	}
	
	@PutMapping(value="connection/{connectionId}", consumes= {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateConnection(@PathVariable int connectionId, @RequestBody JsonDatabase jsonDatabase) {
		JsonDatabase oldDb = jsonDbRepository.findById(connectionId);
		oldDb.setDbDriver(jsonDatabase.getDbDriver());
		oldDb.setDbPassword(jsonDatabase.getDbPassword());
		oldDb.setDbUrl(jsonDatabase.getDbUrl());
		oldDb.setDbUser(jsonDatabase.getDbUser());
		oldDb.setDbSchema(jsonDatabase.getDbSchema());
		jsonDbRepository.save(oldDb);
	}
	
}
