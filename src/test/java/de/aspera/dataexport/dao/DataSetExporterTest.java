package de.aspera.dataexport.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.dbunit.DatabaseUnitException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.unitils.dbunit.annotation.DataSet;

import de.aspera.dataexport.util.ExporterController;
import de.aspera.dataexport.util.json.ExportJsonCommand;
import de.aspera.dataexport.util.json.ExportJsonCommandHolder;
import de.aspera.dataexport.util.json.JsonConnectionHolder;
import de.aspera.dataexport.util.json.JsonDatabase;
import de.aspera.dataexport.util.json.TableQuery;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {
		ExporterController.class, 
		JsonConnectionHolder.class,
		ExportJsonCommandHolder.class})
public class DataSetExporterTest extends BasicFacadeTest {

	protected final static Logger logger = Logger.getLogger(DataSetExporterTest.class.getName());
	
	@Autowired
	private ExporterController  exporterController;

	@BeforeClass
	public static void initDb() {
		try {
			Connection conn = DriverManager.getConnection("jdbc:h2:~/test;INIT=CREATE SCHEMA IF NOT EXISTS TESTX", "sa",
					"");
			Statement st = conn.createStatement();
			st.execute("SET SCHEMA TESTX");
			st.execute("DROP TABLE IF EXISTS `customer`");
			st.execute("create table customer(id integer, name varchar(10))");
			st.execute("insert into customer values (1, 'Thomas')");
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt.executeQuery("select name from customer");
			while (rset.next()) {
				String name = rset.getString(1);
				logger.info("out of customer:  " + name);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	@DataSet("DataSetExporterTest.xml")
	public void exportDatasetTest() throws DatabaseUnitException, SQLException, IOException {
		JsonDatabase connectionData = new JsonDatabase();
		connectionData.setDbUrl("jdbc:h2:~/test;INIT=CREATE SCHEMA IF NOT EXISTS TESTX");
		connectionData.setDbUser("sa");
		connectionData.setDbPassword("");
		connectionData.setIdent("Ident-1");
		connectionData.setDbSchema("TESTX");
		ExportJsonCommand command = new ExportJsonCommand();
		command.setConnId("Ident-1");
		command.setCommandId("comm-1");
		command.setExportedFilePath(".//testFolder");
		List<TableQuery> tables = new ArrayList<>();
		TableQuery table= new TableQuery();
		table.setTableName("CUSTOMER");
		table.setColumns("*");
		tables.add(table);
		command.setTables(tables);
		
		ByteArrayOutputStream resultStream = exporterController.startExportForTable(connectionData, command,false);
		assertNotNull("Did not create output stream!", resultStream);
		String content = resultStream.toString();
		assertTrue("Did not export the right table!", content.contains("Thomas"));
	}

	@Override
	public Class<?> getLoggerClass() {
		return this.getClass();
	}
}
