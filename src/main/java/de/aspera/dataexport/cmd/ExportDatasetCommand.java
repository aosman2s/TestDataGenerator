package de.aspera.dataexport.cmd;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.aspera.dataexport.Repositories.JsonDbRepository;
import de.aspera.dataexport.Repositories.JsonExportCommandRepository;
import de.aspera.dataexport.util.ExporterController;
import de.aspera.dataexport.util.json.ExportJsonCommand;
import de.aspera.dataexport.util.json.JsonDatabase;

@Component
public class ExportDatasetCommand {
	@Autowired
	private JsonDbRepository jsonDbRepository;

	@Autowired
	private ExporterController exporterController;

	@Autowired
	private JsonExportCommandRepository jsonExportCommandRepository;

	
	private ByteArrayOutputStream exportStream;
	private static final Logger LOGGER = Logger.getLogger(ExportDatasetCommand.class.getName());
	private JsonDatabase dataConnection;
	private ExportJsonCommand exportCommand;

	public void run(int jsonDbId, int jsonExportCommandId) throws CommandException {

		try {
			exportCommand = jsonExportCommandRepository.findById(jsonExportCommandId); 
			if (exportCommand == null) {
				LOGGER.log(Level.WARNING, "Your commandId:\"{0}\" could not found!", jsonExportCommandId);
				return;
			}

			dataConnection = jsonDbRepository.findById(jsonDbId); 
			FileOutputStream fileOut = null;
			File file;
			exportStream = exporterController.startExportForTable(dataConnection, exportCommand, false);
			file = new File(exportCommand.getExportedFilePath().concat(File.separator + "DataSet-Table-"
					+ exportCommand.getCommandId() + "-" + dataConnection.getId() + ".xml"));
			fileOut = new FileOutputStream(file);
			exportStream.writeTo(fileOut);
			if (exportStream != null)
				IOUtils.closeQuietly(exportStream);
			if (fileOut != null)
				IOUtils.closeQuietly(fileOut);
		} catch (Exception e) {
			throw new CommandException(e.getMessage(), e);
		}

	}
}
