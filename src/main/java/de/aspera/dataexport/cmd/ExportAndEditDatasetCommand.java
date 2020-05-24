package de.aspera.dataexport.cmd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.aspera.dataexport.Repositories.JsonDbRepository;
import de.aspera.dataexport.Repositories.JsonExportCommandRepository;
import de.aspera.dataexport.groovy.GroovyReader;
import de.aspera.dataexport.util.ExporterController;
import de.aspera.dataexport.util.json.ExportJsonCommand;
import de.aspera.dataexport.util.json.ImportJsonCommandException;
import de.aspera.dataexport.util.json.JsonConnectionReadException;
import de.aspera.dataexport.util.json.JsonDatabase;

@Component
public class ExportAndEditDatasetCommand {

	@Autowired
	private ExporterController exporterController;

	@Autowired
	private DatasetEditorUserFacade datasetEditorUserFacade;

	@Autowired
	private GroovyReader groovyReader;

	@Autowired
	private JsonExportCommandRepository jsonExportCommandRepository;

	@Autowired
	private JsonDbRepository jsonDbRepository;

	private static final Logger LOGGER = Logger.getLogger(ExportAndEditDatasetCommand.class.getName());
	private JsonDatabase dataConnection;
	private ExportJsonCommand exportCommand;
	private ByteArrayOutputStream exportStream;
	private File file;
	private FileOutputStream fileOut;
	private ByteArrayInputStream inputStream;

	public void run(int jsonDbId, int exportCommandId) throws CommandException {
		boolean executed = startExportFromDataBase(jsonDbId, exportCommandId);
		if (executed)
			startEditingDataset();

	}


	private boolean startExportFromDataBase(int jsonDbId, int exportCommandId) throws CommandException {
		exportCommand = jsonExportCommandRepository.findById(exportCommandId);
		if (exportCommand == null) {
			LOGGER.log(Level.WARNING, "Your commandId:\"{0}\" could not found!", exportCommandId);
			return false;
		}
		dataConnection = jsonDbRepository.findById(jsonDbId);
		try {
			exportStream = exporterController.startExportForTable(dataConnection, exportCommand, true);
			return true;
		} catch (DatabaseUnitException | SQLException e) {
			throw new CommandException(e.getMessage(), e);
		} finally {
			if (exportStream != null)
				IOUtils.closeQuietly(exportStream);
			if (fileOut != null)
				IOUtils.closeQuietly(fileOut);
		}
	}

	private void startEditingDataset() throws CommandException {
		try {
			// convert output to input stream without writing to the desk
			inputStream = new ByteArrayInputStream(exportStream.toByteArray());
			datasetEditorUserFacade.setEditor(inputStream);
			groovyReader.readGroovyScript();
			datasetEditorUserFacade.setConnectionOfDB(exporterController.getConnection());
			groovyReader.executeGroovyScript(datasetEditorUserFacade);
			// Write results Back to file
			file = new File(exportCommand.getExportedFilePath().concat(File.separator + "DataSet-Table-"
					+ exportCommand.getCommandId() + "-" + dataConnection.getId() + ".xml"));
			fileOut = new FileOutputStream(file);
			FlatXmlDataSet.write(datasetEditorUserFacade.getDataSet(), fileOut);
		} catch (Exception e) {
			throw new CommandException(e.getMessage(), e);
		} finally {
			if (inputStream != null)
				IOUtils.closeQuietly(inputStream);
			if (fileOut != null)
				IOUtils.closeQuietly(fileOut);
		}

	}
}
