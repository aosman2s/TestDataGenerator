package de.aspera.dataexport.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.ext.h2.H2Connection;
import org.dbunit.ext.mssql.MsSqlConnection;
import org.dbunit.ext.mysql.MySqlConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.aspera.dataexport.util.json.JsonDatabase;

/**
 * Use this to export data from database to a flat XML data set..
 * 
 * @author Ahmed Osman
 * 
 */
public class DataSetExporter {

	private static final Logger logger = LoggerFactory.getLogger(DataSetExporter.class);
	private Connection connection;
	private JsonDatabase databaseConnection;

	public DataSetExporter(JsonDatabase databaseConnection) {
		this.databaseConnection = databaseConnection;
		this.connection = getConnection(databaseConnection);
	}

	/**
	 * Export a unitils/dbunit datasheet as xml file
	 * 
	 * @param descriptors
	 * @return
	 * @throws DatabaseUnitException
	 * @throws SQLException
	 */
	public ByteArrayOutputStream exportDataSet(List<TableDescriptor> descriptors, boolean editable)
			throws DatabaseUnitException, SQLException {

		IDatabaseConnection conn = null;
		if (databaseConnection.getDbUrl().contains("mysql"))
			conn = new MySqlConnection(connection, descriptors.get(0).getSchemaName());
		if (databaseConnection.getDbUrl().contains("sqlserver"))
			conn = new MsSqlConnection(connection, descriptors.get(0).getSchemaName());
		if (databaseConnection.getDbUrl().contains("h2"))
			conn = new H2Connection(connection, descriptors.get(0).getSchemaName());

		ByteArrayOutputStream outputStream = null;
		// partial database export
		QueryDataSet partialDataSet = new QueryDataSet(conn, descriptors.get(0).getSchemaName());
		try {
			for (TableDescriptor descriptor : descriptors) {
				partialDataSet.addQry(descriptor);
			}

		} catch (AmbiguousTableNameException e) {
			logger.error("can not export the dataset", e);
		}

		try {
			outputStream = new ByteArrayOutputStream();
			if(editable) {
				XmlDataSet.write(partialDataSet, outputStream);
			}else {
				FlatXmlDataSet.write(partialDataSet, outputStream);
			}
		} catch (DataSetException e) {
			logger.error("can not export the dataset", e);
		} catch (IOException e) {
			logger.error("can not export the dataset", e);
		}
		return outputStream;
	}

	/**
	 * Create and returns a standard jdbc database connection
	 * 
	 * @param databaseConnection
	 * @return
	 */
	private Connection getConnection(JsonDatabase databaseConnection) {
		return JDBCConnection.getConnection(databaseConnection.getDbUrl(), databaseConnection.getDbUser(),
				databaseConnection.getDbPassword());
	}

	/**
	 * Reuse the connection where possible.
	 * 
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * 
	 * @param Url
	 * @param username
	 * @param password
	 * @return
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}
