/**
 *
 */
package de.aspera.dataexport.util;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus.Zychowski
 *
 */
public class JDBCConnection {

	protected static final Logger logger = LoggerFactory.getLogger(JDBCConnection.class);

	private static String procedureName = "TruncateTableForSelenium";

	/**
	 * Weiß nicht ob weg, bitte prüfen! Executes the procedure "TruncateTable"
	 * stored in MySQL.
	 * 
	 */
	public static void truncate(String urlDatabase, String username, String password) throws SQLException, IOException {

		logger.info("truncate()");
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(urlDatabase, username, password);

			String querry = "{call " + procedureName + "()}";
			CallableStatement cStmt = conn.prepareCall(querry);

			cStmt.executeQuery();
			logger.info("Execute Procedure successful.");

		} catch (SQLException ex) { // handle any errors
			logger.error("Execute Procedure unsuccessful.");
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new RuntimeException(ex.getMessage(), ex);

		} finally {
			conn.close();
		}
	}

	/**
	 * Return the known database connection by unitils config.
	 * 
	 * @return
	 */
	public static Connection getConnection(String url_database, String username, String password) {
		try {
			return DriverManager.getConnection(url_database, username, password);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	/**
	 * 
	 * @param table
	 * @param column
	 * @param ascending
	 * @return sorted list of values from specific column
	 * @throws IOException
	 * @deprecated check and may be delete it!
	 */
	public static List<String> getSortedColumnValues(String table, String column, boolean ascending, String url_database, String username, String password)
			throws IOException {
		List<String> sortedColumnValues = new ArrayList<>();
		StringBuilder sb = new StringBuilder();

		Connection conn = getConnection(url_database, username, password);

		sb.append("select ").append(column).append(" from ").append(table).append(" order by ").append(column);
		if (ascending) {
			sb.append(" asc");
		} else {
			sb.append(" desc");
		}

		try {
			Statement statement = (Statement) conn.createStatement();
			ResultSet rs = statement.executeQuery(sb.toString());
			while (rs.next()) {
				sortedColumnValues.add(rs.getString(1));
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e.getErrorCode(), e);
		}

		return sortedColumnValues;
	}

	/**
	 * Execute SQL querry. Param is the query.
	 * 
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static int executeSQL(String query, String url_database, String username, String password) throws IOException, SQLException {
		logger.info("Execute SQL");

		int count = 0;
		Connection connection = getConnection(url_database, username, password);
		Statement stmt = (Statement) connection.createStatement();

		try {
			count = stmt.executeUpdate(query);

		} catch (SQLException ex) {
			logger.error("Execute insertSQL unsuccessful.");
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new RuntimeException(ex.getMessage(), ex);

		} finally {
			connection.close();
		}
		return count;
	}
}
