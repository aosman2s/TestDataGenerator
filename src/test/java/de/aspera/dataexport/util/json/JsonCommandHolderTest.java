package de.aspera.dataexport.util.json;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@ContextConfiguration(classes={
		ExportJsonCommandHolder.class
		})
public class JsonCommandHolderTest {
	@Autowired
	private ExportJsonCommandHolder exportJsonCommandHolder;
	private ExportJsonCommand commandJsonObj;
	private List<ExportJsonCommand> commands;

	@Before
	public void makeConnectionData() {
		List<TableQuery> tables = new ArrayList<>();
		TableQuery table1 = new TableQuery();
		TableQuery table2 = new TableQuery();
		table1.setTableName("sap_sys");
		table2.setTableName("account_type");
		tables.add(table1);
		tables.add(table2);
		commandJsonObj = new ExportJsonCommand();
		commandJsonObj.setCommandId("ID-1");
		commandJsonObj.setConnId("connId");
		commandJsonObj.setTables(tables);
		commandJsonObj.setExportedFilePath(System.getProperty("user.dir"));
	}

	@Test(expected = ImportJsonCommandException.class)
	public void doubleCommandId() throws ImportJsonCommandException {
		commands = new ArrayList<>();
		commands.add(commandJsonObj);
		commands.add(commandJsonObj);
		exportJsonCommandHolder.addCommandList(commands);
	}

	@Test(expected = ImportJsonCommandException.class)
	public void emptyCommandId() throws ImportJsonCommandException {
		commands = new ArrayList<>();
		commands.add(commandJsonObj);
		commandJsonObj.setCommandId("");
		commands.add(commandJsonObj);
		exportJsonCommandHolder.addCommandList(commands);
	}

	@Test(expected = ImportJsonCommandException.class)
	public void emptyConnectionId() throws ImportJsonCommandException {
		commands = new ArrayList<>();
		commandJsonObj.setConnId("");
		commands.add(commandJsonObj);
		exportJsonCommandHolder.addCommandList(commands);
	}

	@Test(expected = ImportJsonCommandException.class)
	public void flaseFilePath() throws ImportJsonCommandException {
		commands = new ArrayList<>();
		commandJsonObj.setExportedFilePath("dummy");
		commands.add(commandJsonObj);
		exportJsonCommandHolder.addCommandList(commands);
	}

	@Test
	public void savedCommands() throws ImportJsonCommandException {
		commands = new ArrayList<>();
		ExportJsonCommand cmd = new ExportJsonCommand();
		cmd.setCommandId("ID-2");
		cmd.setConnId("connId");
		List<TableQuery> tables = new ArrayList<>();
		TableQuery table2 = new TableQuery();
		table2.setTableName("sap_sys");
		tables.add(table2);
		cmd.setTables(tables);
		cmd.setExportedFilePath(System.getProperty("user.dir"));
		commands.add(commandJsonObj);
		commands.add(cmd);
		exportJsonCommandHolder.addCommandList(commands);
		assertEquals(exportJsonCommandHolder.getCommand("ID-1"), commandJsonObj);
		assertEquals(exportJsonCommandHolder.getCommand("ID-2"), cmd);
	}

}
