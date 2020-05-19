package de.aspera.dataexport.util.dataset.editor;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.datatype.DataType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {
		DatasetReader.class,
		DatasetMultiplier.class,
		TableKeysInvestigator.class
})
public class DatasetReaderTest {
	@Autowired
	DatasetReader reader;
	@Autowired
	DatasetMultiplier multiplier;
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
	public void testTable() throws DataSetException, DatasetReaderException {
		assertTrue("Wrong number of Rows", reader.getTableNames().contains("test-table"));
		assertEquals(3, reader.getColumnNamesOfTable("test-table").size(), "wrong Number of Columns");
		assertEquals(0, reader.getMetaDataOfTable("test-table").getColumnIndex("val1Col"), "wrong Index of Columns");
		assertEquals("test-table", reader.getTableNames().get(0), "wrong table name");
		assertEquals("val2", reader.getValueInTable("test-table", 0, "val2Col"), "wrong value of col in row");
	}

}
