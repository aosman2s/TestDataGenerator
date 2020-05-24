package de.aspera.dataexport.util.json;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;

public class JsonDatabaseMarshallingTest {

	
	@Test
	public void readFromObject() {
		
		List<JsonDatabase> jsonDatabases = new ArrayList<>();
		
		JsonDatabase json1 = new JsonDatabase();
		json1.setDbDriver("foobarDriver");
		json1.setDbPassword("dbPassword");
		json1.setDbUrl("dbUrl://foobar1/foo1");
		json1.setDbUser("dbUser");
		json1.setId(1);
		
		JsonDatabase json2 = new JsonDatabase();
		
		json2.setDbDriver("foobarDriver");
		json2.setDbPassword("dbPassword");
		json2.setDbUrl("dbUrl://foobar/foo");
		json2.setDbUser("dbUser");
		json2.setId(2);
		
		jsonDatabases.add(json1);
		jsonDatabases.add(json2);
		
		Gson gson = new Gson();
		String jsonAsString = gson.toJson(jsonDatabases.toArray(new JsonDatabase[] {}));
		JsonDatabase[] jsonDataAsArray = gson.fromJson(jsonAsString, JsonDatabase[].class);
		
		
	}
}
