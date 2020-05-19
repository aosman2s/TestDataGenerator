package de.aspera.dataexport.util.dataset.editor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {
		DatasetEditorFacade.class,
		DatasetReader.class,
		DatasetMultiplier.class,
		DatasetRowEditor.class,
		TableKeysInvestigator.class,
		DatasetRandomizer.class,
		RefrencesEditor.class,
		})
public class DatasetEditorFacadeTest {
	
	@Autowired
	DatasetEditorFacade editorFacade;
	
	DefaultDataSet dataset;
	DefaultTable table;
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		dataset = new DefaultDataSet();
		String[] values = { "val1", "val2", "val3" };
		Column col1 = new Column("val1Col", DataType.UNKNOWN);
		Column col2 = new Column("val2Col", DataType.UNKNOWN);
		Column col3 = new Column("val3Col", DataType.UNKNOWN);
		Column[] cols = new Column[] { col1, col2, col3 };
		table = new DefaultTable("test-table", cols);
		table.addRow(values);
		dataset.addTable(table);
	}
	
	@Test
	public void testReadDatasetFile() throws IOException, DataSetException, DatasetReaderException {	
		File file = tempFolder.newFile("testDataSet.xml");
		FileOutputStream out = new FileOutputStream(file);
		FlatXmlDataSet.write(dataset, out);
		editorFacade.readDataset(file.getAbsolutePath());
		assertEquals(1, editorFacade.getTableNames().size(), "tables of the dataset were not correctly read from file");
		assertEquals(3, editorFacade.getColumnNamesOfTable("test-table").size(), "columns of the dataset were not correctly read from file");
	}
	
	@Test
	public void testReadDatasetStream() throws IOException, DataSetException, ClassNotFoundException, DatasetReaderException, DatasetEditorException {	
		File file = tempFolder.newFile("testDataSet.xml");
		FileOutputStream out = new FileOutputStream(file);
		FlatXmlDataSet.write(dataset, out);
		out.close();
		FileInputStream in = new FileInputStream(file);
		editorFacade.readDataset(in);
		assertEquals(1, editorFacade.getTableNames().size(), "tables of the dataset were not correctly read from stream");
		assertEquals(3, editorFacade.getColumnNamesOfTable("test-table").size(), "columns of the dataset were not correctly read from stream");
	}

}
