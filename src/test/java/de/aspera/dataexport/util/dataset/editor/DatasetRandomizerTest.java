package de.aspera.dataexport.util.dataset.editor;

import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
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
		DatasetRandomizer.class
		})
public class DatasetRandomizerTest {
	@Autowired
	DatasetReader reader;
	@Autowired
	DatasetRandomizer randomizer;
	@MockBean
	TableKeysInvestigator tableKeysInvestigator;
	
	DefaultDataSet dataset;
	DefaultTable table;
	
	@Before
	public void setUp() throws Exception {
		dataset = new DefaultDataSet();
		Column col1 = new Column("val1Col", DataType.UNKNOWN);
		Column col2 = new Column("val2Col", DataType.UNKNOWN);
		Column col3 = new Column("val3Col", DataType.UNKNOWN);
		Column[] cols = new Column[] { col1, col2, col3 };
		table = new DefaultTable("test-table", cols);
		for(int i=0; i<6;i++) {
			String[] valuesRow = { "val1R"+i, "val2R"+i, "valR"+i };
			table.addRow(valuesRow);
		}
		dataset.addTable(table);
		reader.setDataset(dataset);
		TableConstrainsDescription tabDesc = new TableConstrainsDescription();
		reader.addTableDescriptionContriant("test-table", tabDesc);
		
	}
	
	@Test
	public void ranomizerTest() throws DatasetRandomizerException, DataSetException {
		List<String> randoms = new ArrayList<String>();
		randoms.add("test-table.val1Col");
		randoms.add("test-table.val2Col");
		randoms.add("test-table.val3Col");
		reader.setRandomFields(randoms);
		randomizer.randomizeValues(false);
		String [] values = new String[3];
		values[0]=dataset.getTable("test-table").getValue(0, "val1Col").toString();
		values[1]=dataset.getTable("test-table").getValue(0, "val2Col").toString();
		values[2]=dataset.getTable("test-table").getValue(0, "val2Col").toString();
		String [] valuesRow0 = {"val1R0", "val2R0", "valR0" };
		assertNotEquals(valuesRow0, values);
	}
	
	@Test
	public void ranomizeTableTest() throws DatasetRandomizerException, DataSetException {
		List<String> randoms = new ArrayList<String>();
		randoms.add("test-table.val1Col");
		randoms.add("test-table.val2Col");
		randoms.add("test-table.val3Col");
		reader.setRandomFields(randoms);
		randomizer.randomizeValues("test-table", false);
		String [] values = new String[3];
		values[0]=dataset.getTable("test-table").getValue(0, "val1Col").toString();
		values[1]=dataset.getTable("test-table").getValue(0, "val2Col").toString();
		values[2]=dataset.getTable("test-table").getValue(0, "val2Col").toString();
		String [] valuesRow0 = {"val1R0", "val2R0", "valR0" };
		assertNotEquals(valuesRow0, values);
	}


}
