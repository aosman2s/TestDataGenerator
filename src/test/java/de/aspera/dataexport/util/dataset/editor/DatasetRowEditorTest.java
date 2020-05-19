package de.aspera.dataexport.util.dataset.editor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.DataType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {
		DatasetReader.class,
		DatasetRowEditor.class,
		TableKeysInvestigator.class
})
public class DatasetRowEditorTest {
	DatasetReader reader;
	DatasetRowEditor rowEditor;
	DefaultDataSet dataset;
	DefaultTable table;

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
		reader.setDataset(dataset);
	}

	@Test
	public void testChangeValuesInTable() throws DataSetException, DatasetReaderException, SQLException, TableKeysInvestigatorException, DatasetRowEditorException {
		Map<String, String> newColNameValueMap = new HashMap<String, String>();
		newColNameValueMap.put("val1Col", "newValCol1");
		newColNameValueMap.put("val3Col", "newValCol3");
		IDataSet editedDataset= rowEditor.changeValuesInRow("test-table", 0, newColNameValueMap);
		assertEquals("newValCol1", editedDataset.getTable("test-table").getValue(0, "val1Col").toString(),"false value in the first column");
		assertEquals("newValCol3", editedDataset.getTable("test-table").getValue(0, "val3Col").toString(),"false value in the third column");
		assertEquals("val2", editedDataset.getTable("test-table").getValue(0, "val2Col").toString(), "the value of second column were falsly changed"); 
	}
	
	@Test
	public void testAddRowToTable() throws DataSetException, DatasetReaderException, SQLException, TableKeysInvestigatorException, DatasetRowEditorException {
		Map<String, String> newColNameValueMap = new HashMap<String, String>();
		newColNameValueMap.put("val1Col", "newValCol1");
		newColNameValueMap.put("val3Col", "newValCol3");
		IDataSet editedDataset= rowEditor.addRow("test-table", newColNameValueMap);
		assertEquals("newValCol1", editedDataset.getTable("test-table").getValue(1, "val1Col").toString(),"false value in the first column for the new Row");
		assertEquals("newValCol3", editedDataset.getTable("test-table").getValue(1, "val3Col").toString(),"false value in the third column for the new Row");
		assertNull(editedDataset.getTable("test-table").getValue(1, "val2Col"), "the value in the second column should be null!");
	}

}
