package de.aspera.dataexport.util.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import de.aspera.dataexport.cmd.ConfigInitCommand;
import de.aspera.dataexport.util.Resources;
@Component
public final class ExportJsonCommandHolder {
	private static final Logger LOGGER = Logger.getLogger(ConfigInitCommand.class.getName());
	private Map<String, ExportJsonCommand> cmdRepo = new HashMap<>();
	private Gson gson = new Gson();
	private JsonReader jsonReader;

	
	public void importJsonCommands() throws FileNotFoundException, ImportJsonCommandException {
		cmdRepo = new HashMap<>();
		FileReader reader = new FileReader(getJasonFileConn());
		jsonReader = new JsonReader(reader);
		jsonReader.setLenient(true);
		Type listType = new TypeToken<List<ExportJsonCommand>>() {
		}.getType();
		List<ExportJsonCommand> commands = gson.fromJson(reader, listType);
		addCommandList(commands);
		try {
			reader.close();
			jsonReader.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void addCommandList(List<ExportJsonCommand> commands) throws ImportJsonCommandException {
		for (ExportJsonCommand cmd : commands) {
			String commId = cmd.getCommandId();
			if (cmdRepo.keySet().contains(commId)) {
				throw new ImportJsonCommandException("duplicated Command Id");
			} else if (commId.isEmpty()) {
				throw new ImportJsonCommandException("Id for Command not defined");
			} else if (cmd.getExportedFilePath().isEmpty() || !new File(cmd.getExportedFilePath()).isDirectory()) {
				throw new ImportJsonCommandException("Path for the exported File is empty or does not exist");
			} else if (cmd.getConnId().isEmpty()) {
				throw new ImportJsonCommandException("Connection Id is empty");
			} else {
				for (TableQuery table : cmd.getTables()) {
					if (table.getTableName().isEmpty())
						throw new ImportJsonCommandException("Table name is empty");
				}
				cmdRepo.put(commId, cmd);
			}
		}
	}

	public ExportJsonCommand getCommand(String id) {
		return cmdRepo.get(id);
	}

	public File getJasonFileConn() {
		String filePath;
		if (SystemUtils.IS_OS_WINDOWS) {
			filePath = System.getProperty("user.home") + "\\." + Resources.PROJECT_NAME
					+ "\\dataExporter_ExportCommands.json";
		} else {
			filePath = System.getProperty("user.home") + "/." + Resources.PROJECT_NAME
					+ "dataExporter_ExportCommands.json";
		}
		Path pathOfFile = Paths.get(filePath);
		return pathOfFile.toFile();
	}

	public void deleteCommands() {
		cmdRepo.clear();

	}

}
