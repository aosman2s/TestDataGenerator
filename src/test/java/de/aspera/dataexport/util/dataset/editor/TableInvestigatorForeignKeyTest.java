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
@ContextConfiguration(classes= {
		TableKeysInvestigator.class
})
public class TableInvestigatorForeignKeyTest {
	@Autowired
	private TableKeysInvestigator tableInvestigator;
	@Mock
	Connection conn;
	@Mock
	DatabaseMetaData metadata;
	@Mock
	ResultSet foriegnKeyCols;
	
	@Before
	public void setUp() throws SQLException, DataSetException, TableKeysInvestigatorException, DatasetReaderException {
		
		Mockito.when(conn.getMetaData()).thenReturn(metadata);
		Mockito.when(metadata.getImportedKeys(null, null, "test-table")).thenReturn(foriegnKeyCols);
		Mockito.when(foriegnKeyCols.next()).thenAnswer(new AnswerImplementation(2));
		Mockito.when(foriegnKeyCols.getString("PKTABLE_NAME")).thenReturn("refrenced-table");
		Mockito.when(foriegnKeyCols.getString("PKCOLUMN_NAME")).thenReturn("refrenced-PKcol");
		Mockito.when(foriegnKeyCols.getString("FKCOLUMN_NAME")).thenReturn("FKcol");
		tableInvestigator.setConnection(conn);
	}

	@Test
	public void testUniqueCols() throws TableKeysInvestigatorException {
		Map<String, String> keyMap = tableInvestigator.getReferencesToTables("test-table");
		assertEquals(1, keyMap.keySet().size(),"false number of refrences keys given");
		assertEquals("refrenced-table.refrenced-PKcol", keyMap.get("FKcol"), "false refrnced primary keys given");
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
