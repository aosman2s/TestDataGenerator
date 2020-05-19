package de.aspera.dataexport.util.json;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={
		JsonConnectionHolder.class
		})
public class JsonConnectionRepoTest {
	@Autowired
	private JsonConnectionHolder jsonConnectionHolder;
	JsonObject connectionDetails;
	private Gson gson;

	@Before
	public void makeConnectionData() {
		gson= new Gson();
		connectionDetails = new JsonObject();
		String ID = "ID-1";
		String Driver = "com.mysql.jdbc.Driver";
		String URL = "jdbc:mysql://127.0.0.1/slc_test";
		String Username = "root";
		String pass = "pass";
		connectionDetails.addProperty("ident", ID);
		connectionDetails.addProperty("dbDriver", Driver);
		connectionDetails.addProperty("dbUrl", URL);
		connectionDetails.addProperty("dbUser", Username);
		connectionDetails.addProperty("dbPassword", pass);
	}

	@Test(expected = NoDriverDefinedException.class)
	public void testReadJsonDbValuesThrowDriverException() throws JsonConnectionReadException {
		jsonConnectionHolder.deleteConnections();
		connectionDetails.addProperty("dbUrl", "jdbc:jtds:sqlserver://192.168.111.150:1433/slc_dev");
		connectionDetails.addProperty("dbDriver", "");
		JsonDatabase connection = gson.fromJson(connectionDetails, JsonDatabase.class);
		jsonConnectionHolder.parseJsonConnection(connection);

	}

	@Test(expected = NoIdDefinedException.class)
	public void testReadJsonDbValuesThrowIdException() throws JsonConnectionReadException {
		jsonConnectionHolder.deleteConnections();
		connectionDetails.addProperty("ident", "");
		JsonDatabase connection = gson.fromJson(connectionDetails, JsonDatabase.class);
		jsonConnectionHolder.parseJsonConnection(connection);

	}

	@Test(expected = DuplicateConnIdException.class)
	public void testReadDatabaseThrowsItenticalIDException() throws JsonConnectionReadException {
		JsonDatabase connectionOne = gson.fromJson(connectionDetails, JsonDatabase.class);;
		JsonDatabase connectionTwo = gson.fromJson(connectionDetails, JsonDatabase.class);
		jsonConnectionHolder.deleteConnections();
		jsonConnectionHolder.parseJsonConnection(connectionOne);
		jsonConnectionHolder.parseJsonConnection(connectionTwo);
	}

	@Test
	public void testReadJsonDbValues() throws JsonConnectionReadException {
		for (int i = 0; i < 6; i++) {
			connectionDetails.addProperty("ident", "ID-" + i);
			JsonDatabase conn = gson.fromJson(connectionDetails, JsonDatabase.class);
			jsonConnectionHolder.parseJsonConnection(conn);
		}
		assertEquals("Wrong number of saved Connections", 6, jsonConnectionHolder.getNumberOfJsonDatabases());
	}

}
