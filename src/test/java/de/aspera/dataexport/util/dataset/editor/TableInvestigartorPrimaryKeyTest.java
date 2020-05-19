package de.aspera.dataexport.util.dataset.editor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.dbunit.dataset.DataSetException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
		TableKeysInvestigator.class
})
public class TableInvestigartorPrimaryKeyTest {
	@Autowired
	private TableKeysInvestigator tableInvestigator;
	@Mock
	Connection conn;
	@Mock
	DatabaseMetaData metadata;
	@Mock
	ResultSet primaryColstab;
	@Mock
	ResultSet priKeyInfoTab;
	
	@Before
	public void setUp() throws SQLException, DataSetException, TableKeysInvestigatorException, DatasetReaderException {
		
		Mockito.when(conn.getMetaData()).thenReturn(metadata);
		Mockito.when(metadata.getPrimaryKeys(null, null, "test-table")).thenReturn(primaryColstab);
		Mockito.when(primaryColstab.next()).thenAnswer(new AnswerImplementation(2));
		Mockito.when(primaryColstab.getString("COLUMN_NAME")).thenReturn("priKey1");
		Mockito.when(metadata.getColumns(null, null, "test-table", "priKey1")).thenReturn(priKeyInfoTab);
		Mockito.when(priKeyInfoTab.getString("TYPE_NAME")).thenReturn("number");
		Mockito.when(priKeyInfoTab.getString("COLUMN_SIZE")).thenReturn("2");
		tableInvestigator.setConnection(conn);
	}

	@Test
	public void testUniqueCols() throws TableKeysInvestigatorException {
		Map<String, String> keyMap = tableInvestigator.getPrimarykeysOfTable("test-table");
		assertEquals(1, keyMap.keySet().size(),"false number of primary keys given");
		assertEquals("number,2", keyMap.get("priKey1"), "false type of primary keys given");
	}

	private final class AnswerImplementation implements Answer<Object> {
		AnswerImplementation(int maxCount) {
			this.maxCount = maxCount;
		}

		private int maxCount;
		private int count = 0;

		public Object answer(InvocationOnMock invocation) {
			if (count <= maxCount) {
				count++;
				return true;
			}
			return false;
		}
	}
}
