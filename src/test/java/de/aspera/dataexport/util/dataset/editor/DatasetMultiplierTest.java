package de.aspera.dataexport.util.dataset.editor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.DataType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= {
		DatasetReader.class,
		DatasetMultiplier.class
		})
public class DatasetMultiplierTest {
	@Autowired
	DatasetReader reader;
	@Autowired
	DatasetMultiplier multiplier;
	@MockBean
	private TableKeysInvestigator tableKeysInvestigator;
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
	public void testMultiplyAllTable() throws DataSetException, SQLException, TableKeysInvestigatorException, DatasetReaderException, DatasetMultiplierException {
		IDataSet newDataset = multiplier.multiplyData(4);
		assertEquals(5, newDataset.getTable("test-table").getRowCount(), "Wrong number of Rows");
		Column[] cols = newDataset.getTable("test-table").getTableMetaData().getColumns();
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < cols.length; j++) {
				int colVal = j + 1;
				assertEquals("val" + colVal, newDataset.getTable("test-table").getValue(i, cols[j].getColumnName()));
			}
		}

	}
	
	@Test
	public void testMultiplyRowInTable() throws DataSetException, SQLException, TableKeysInvestigatorException, DatasetReaderException, DatasetMultiplierException {
		IDataSet newDataset = multiplier.multiplyRowInTable("test-table", 0, 5);
		assertEquals(6, newDataset.getTable("test-table").getRowCount(), "Wrong number of Rows");
		Column[] cols = newDataset.getTable("test-table").getTableMetaData().getColumns();
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < cols.length; j++) {
				int colVal = j + 1;
				assertEquals("val" + colVal, newDataset.getTable("test-table").getValue(i, cols[j].getColumnName()));
			}
		}

	}

}
