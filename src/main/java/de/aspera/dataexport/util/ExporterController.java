package de.aspera.dataexport.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.DatabaseUnitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.aspera.dataexport.util.json.ExportJsonCommand;
import de.aspera.dataexport.util.json.ExportJsonCommandHolder;
import de.aspera.dataexport.util.json.ImportJsonCommandException;
import de.aspera.dataexport.util.json.JsonConnectionHolder;
import de.aspera.dataexport.util.json.JsonConnectionReadException;
import de.aspera.dataexport.util.json.JsonDatabase;
import de.aspera.dataexport.util.json.TableQuery;
@Component
public class ExporterController {
	private static DataSetExporter exporter;
	
	@Autowired
	private JsonConnectionHolder jsonConnectionHolder;
	
	@Autowired
	private ExportJsonCommandHolder exportJsonCommandHolder;

	/**
	 * Get a buffered output stream to transform data or persist on the filesystem.
	 * A Command must have the same Schema and connection put can have more than one
	 * Table
	 * 
	 * @param tableName
	 * @param columnsComaSeperated
	 *            e.g. "firstname, lastname, ...." or "*" for all columns
	 * @param whereClause
	 * @param orderByClause
	 * @return
	 * @throws DatabaseUnitException
	 * @throws SQLException
	 */
	public  ByteArrayOutputStream startExportForTable(JsonDatabase databaseConnection,
			ExportJsonCommand exportCommand, boolean editable) throws DatabaseUnitException, SQLException {
		List<TableDescriptor> descriptors = new ArrayList<>();
		if (databaseConnection == null)
			throw new IllegalArgumentException("The databaseConnection can not be null");
		exporter = new DataSetExporter(databaseConnection);
		for (TableQuery table : exportCommand.getTables()) {
			TableDescriptor descriptor = new TableDescriptor(table.getTableName());
			descriptor.setSchemaName(databaseConnection.getDbSchema());
			if (table.getOrderByCondition() != null)
				descriptor.setOrderByClause(table.getOrderByCondition());
			if (table.getWhereCondition() != null)
				descriptor.setWhereClause(table.getWhereCondition());
			if (!table.getColumns().isEmpty())
				descriptor.addField(table.getColumns());
				else
					descriptor.addField("*");
			descriptors.add(descriptor);
		}
		return exporter.exportDataSet(descriptors, editable);
	}

	public void readJsonDatabaseFile() throws JsonConnectionReadException {
		jsonConnectionHolder.deleteConnections();
		jsonConnectionHolder.initJsonDatabases();
	}

	public void readJsonCommandsFile() throws FileNotFoundException, ImportJsonCommandException {
		exportJsonCommandHolder.deleteCommands();
		exportJsonCommandHolder.importJsonCommands();
		
	}
	
	public Connection getConnection() {
		return exporter.getConnection();
	}
	

}
