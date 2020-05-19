package util;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.Test;

import de.aspera.dataexport.dao.DatabaseException;
import de.aspera.dataexport.util.JDBCConnection;

/**
 * 
 * @author Victoria Schneider
 *
 */
public class ConnectionTest {

	@Test
	public void testConnection() throws DatabaseException {
		Connection connection = JDBCConnection.getConnection("jdbc:h2:~/test;INIT=CREATE SCHEMA IF NOT EXISTS TESTX", "sa", "");
		assertNotNull("No connection with database available", connection);
	}

	@Test
	public void testConnectionJdbi() throws DatabaseException {

		Connection connection = JDBCConnection.getConnection("jdbc:h2:~/test;INIT=CREATE SCHEMA IF NOT EXISTS TESTX", "sa", "");
		Handle handle = Jdbi.open(connection);
		handle.begin();
		handle.isInTransaction();
		handle.commit();
		handle.close();
	}

}
