package de.aspera.dataexport.cmd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.aspera.dataexport.dao.ConfigFacade;
import de.aspera.dataexport.dao.DatabaseException;
import de.aspera.dataexport.dto.Config;
import de.aspera.dataexport.util.Resources;
import de.aspera.dataexport.util.json.ExportJsonCommand;
import de.aspera.dataexport.util.json.JsonDatabase;
import de.aspera.dataexport.util.json.TableQuery;

@Component
public class ConfigInitCommand implements CommandRunnable {

	private static final Logger LOGGER = Logger.getLogger(ConfigInitCommand.class.getName());
	public final static String PROJECT_NAME = "dataExporter";
	public static final String EXCLUDED_KEY = "Excluded_Paths";
	public static final String[] EXCLUDED_VALUES = { "target" };

	@Override
	public void run() {
		this.init();

	}

	private void init() {
		try {
			List<Config> configs = new ArrayList<>();
			ConfigFacade configFacade = new ConfigFacade();
			Config config = new Config();

			config.setKey(EXCLUDED_KEY);
			config.setValue(EXCLUDED_VALUES);
			configs.add(config);

			// if no entry found, so add the default entries
			if (configFacade.findAll().isEmpty()) {
				configFacade.saveConfig(configs);
			}
			// write initial Command and Connection files for export Command
			writeJsonExportFiles();
			// write Groovy file
			writeGroovyEditFile();
		} catch (DatabaseException | IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void writeGroovyEditFile() throws IOException {
		String declaration = "/** please use the following methods to edit your Dataset! **/";
		String[] methodDeclaration = {
				"multiplyData(int factor): to multiply the number of rows in each table in the Dataset by the given factor",
				"multiplyRowInTable(String tableName, int row, int factor): to multiply a specific row inside a table",
				"multiplyDataInTable(String tableName, int factor): to multiply the number of rows in a specific table ",
				"changeValuesInRow(String tableName, int row, Map<String, String> newValuesColName): to change the vlues of a specific row",
				"addRow(String tableName, Map<String, String> newValuesColName): to add row to a table",
				"setRandomFields(List<String> fields): to define which columns to have random values in the table. They should be defined as follows : 'TableName.ColumnName'",
				"randomizeValues(): to randomize the values of the defined columns in each table in the Dataset.",
				"randomizeValues(String tableName): to randomize the values of the defined columns in a specific table" };
		String clz = "\nimport de.aspera.dataexport.cmd.DatasetEditorUserFacade;\n" + "class Script {\r\n"
				+ "\r\n" + "     void runScript(DatasetEditorUserFacade facade) {\n" + "\r}\n}";

		String filePath = System.getProperty("user.home") + File.separator + "." + PROJECT_NAME + File.separator
				+ "GroovyScript.groovy";
		File file = new File(filePath);
		if(!file.exists()) {
			FileWriter fileWriter = new FileWriter(filePath);
			fileWriter.write(declaration+"\n");
			fileWriter.write("/*"+"\n");
			for (int i = 0; i < methodDeclaration.length; i++) {
				fileWriter.write("*" + methodDeclaration[i]+"\n");
			}
			fileWriter.write("*/"+"\n");
			fileWriter.write(clz);
			fileWriter.close();
		}		
	}

	private void writeJsonExportFiles() throws IOException {
		String filePath;
		FileWriter fileWriter;
		File file;
		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		List<TableQuery> tablesList = new ArrayList<TableQuery>();
		List<ExportJsonCommand> jsonCommands = new ArrayList<>();
		List<JsonDatabase> jsonConnections = new ArrayList<>();

		// Write Connection file
		filePath = System.getProperty("user.home") + File.separator + "." + PROJECT_NAME + File.separator
				+ "dataExporter_Connections.json";
		file = new File(filePath);
		if (!file.exists()) {
			fileWriter = new FileWriter(file);
			for (int i = 0; i < 2; i++) {
				JsonDatabase jsonDB = new JsonDatabase();
				jsonDB.setIdent("uniqe-connection-Id-" + i);
				jsonDB.setDbDriver("Db-Driver");
				jsonDB.setDbPassword("root");
				jsonDB.setDbSchema("schema-1");
				jsonDB.setDbUrl("database-URL");
				jsonDB.setDbUser("root");
				jsonConnections.add(jsonDB);
			}
			gson.toJson(jsonConnections, fileWriter);
			fileWriter.close();
		}
		// Write Command file
		filePath = System.getProperty("user.home") + File.separator + "." + Resources.PROJECT_NAME + File.separator
				+ "dataExporter_ExportCommands.json";
		file = new File(filePath);
		if (!file.exists()) {
			fileWriter = new FileWriter(file);
			for (int i = 0; i < 2; i++) {
				ExportJsonCommand command = new ExportJsonCommand();
				command.setConnId("uniqe-connection-Id-" + i);
				command.setTables(tablesList);
				command.setCommandId("uniqe-command-Id-" + i);
				command.setExportedFilePath(System.getProperty("user.home"));
				for (int j = 0; j < 2; j++) {
					TableQuery tab = new TableQuery();
					tab.setTableName("tab-" + j);
					tab.setColumns("col1,col2,col3");
					tab.setOrderByCondition("col1 asc");
					tab.setWhereCondition("col1='wert1' and col2='wert2'");
					tablesList.add(tab);
				}
				jsonCommands.add(command);
			}
			gson.toJson(jsonCommands, fileWriter);
			fileWriter.close();

		}
	}
}
