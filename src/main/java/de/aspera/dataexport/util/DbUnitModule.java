package de.aspera.dataexport.util;

import static org.junit.Assert.fail;
import static org.unitils.util.PropertyUtils.getStringList;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlMetadataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DbSupportFactory;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.DatabaseUnitils;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;

import com.mchange.v2.c3p0.ComboPooledDataSource;



public class DbUnitModule extends org.unitils.dbunit.DbUnitModule {

    private static final Logger logger       = LoggerFactory.getLogger(DbUnitModule.class);

    private DatabaseType        databaseType = DatabaseType.UNKNOWN;

    /**
     * Returns the dbms specific {@link DbSupport} as configured in the given
     * <code>Configuration</code> for the default schema. The default schema is
     * the first schema in the configured list of schemas.
     * 
     * @param configuration
     *            The config, not null
     * @param sqlHandler
     *            The sql handler, not null
     * @return The dbms specific instance of {@link DbSupport}, not null
     */
    public DbSupport getDefaultDbSupport(Properties configuration, SQLHandler sqlHandler) {
        String defaultSchemaName = null;

        try {
            defaultSchemaName = getStringList(DbSupportFactory.PROPKEY_DATABASE_SCHEMA_NAMES, configuration, true).get(
                    0);
        } catch (Exception x) {
        	
        }
         
        return DbSupportFactory.getDbSupport(configuration, sqlHandler, defaultSchemaName, getDefaultDbSupport().getDatabaseDialect());
    }

    /**
     * A short cut method to get the default unitils schema name..
     * @return
     */
    public String getDefaultSchemaName() {
        return getDefaultDbSupport().getSchemaName();
    }

    /**
     * Reset the auto increment value to 1 for specific table.
     * 
     * @param tableName
     */
    public void resetAutoincrementValue(String tableName) {
        this.resetAutoincrementValue(tableName, 1L);
    }

    /**
     * Determine the database type here. This will be called once after the initialization 
     * of the module.
     * 
     * @return
     */
	protected DatabaseType determineDatabaseType() {
        DbSupport dbSupport = getDefaultDbSupport();
        DataSource ds = dbSupport.getSQLHandler().getDataSource();

        String url = null;

        if (ds instanceof BasicDataSource) {
            url = ((BasicDataSource) ds).getUrl();
        } else if (ds instanceof ComboPooledDataSource) {
            url = ((ComboPooledDataSource) ds).getJdbcUrl();
        }

        DatabaseType databaseType = DatabaseType.UNKNOWN;

		if (url.indexOf("mysql") > -1) {
			databaseType = DatabaseType.MYSQL;
		} else if (url.indexOf("sqlserver") > -1) {
			databaseType = DatabaseType.MSSQL;
		}
//            else if (url.indexOf("hsqldb") > -1) {
//            databaseType = DatabaseType.HSQL;
//        } else if (url.indexOf("jdbc:h2") > -1) {
//            databaseType = DatabaseType.H2;
		else {
			// ALTER TABLE tableName ALTER COLUMN columnName RESTART WITH long
			fail("We do not support resetAutoincrementValue for this database, jdbc url: " + url);
		}

        logger.debug("determined database type {}", databaseType);

        return databaseType;
    }

    /**
     * Get the database type we use in the persistence layer (beneath JPA/Hibernate).
     * 
     * @return
     */
//    public DatabaseType getDatabaseType() {
//        return databaseType;
//    }

    @Override
    public void afterInit() {
        super.afterInit();

//        this.databaseType = determineDatabaseType();
    }

    /**
     * Reset the auto increment value used when generating an id for a new entry
     * in a table.
     * 
     * TODO: this supports only mysql, sqlserver, hssql and h2 now
     * 
     * @param tableName
     * @param autoincrementValue
     */
    public void resetAutoincrementValue(String tableName, long autoincrementValue) {

        DbSupport dbSupport = getDefaultDbSupport();
        String schemaName = dbSupport.getSchemaName();

        StringBuilder builder = new StringBuilder();

        switch (databaseType) {
            case HSQL:
                // syntax for hsql
                // ALTER TABLE ALTER COLUMN <column name> RESTART WITH <new value>;
                builder.append("ALTER TABLE ");
                builder.append(schemaName);
                builder.append(".");
                builder.append(tableName);
                builder.append(" ALTER COLUMN ID RESTART WITH ");
                builder.append(autoincrementValue);
                break;
            case H2:
                // syntax for h2
                // ALTER TABLE ALTER COLUMN <column name> RESTART WITH <new value>;
                builder.append("ALTER TABLE ");
                builder.append(schemaName);
                builder.append(".");
                builder.append(tableName);
                builder.append(" ALTER COLUMN ID RESTART WITH ");
                builder.append(autoincrementValue);
                break;
            case MYSQL:
                // syntax for mysql
                // this is ok, we support this database engine
                builder.append("ALTER TABLE ");
                builder.append(schemaName);
                builder.append(".");
                builder.append(tableName);
                builder.append(" AUTO_INCREMENT=");
                builder.append(autoincrementValue);
                break;
            case MSSQL:
                // syntax for mssql
                // DBCC CHECKIDENT (SAP_ACCOUNT, RESEED, 100)

                builder.append("DBCC CHECKIDENT ('");
                builder.append(schemaName);
                builder.append(".");
                builder.append(tableName);
                builder.append("' , RESEED, ");
                builder.append(autoincrementValue - 1);
                builder.append(")");
                break;
            default:
                // ALTER TABLE tableName ALTER COLUMN columnName RESTART WITH long
                fail("We do not support resetAutoincrementValue for this database");
        }

        Statement stat = null;
        int sqlResult = 0;

        try {
            logger.debug("reset autoincrement sql: {}", builder);
            stat = getConnection().createStatement();
            sqlResult = stat.executeUpdate(builder.toString());

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.error("SQL ErrorCode ({}): {}", dbSupport.getDatabaseDialect(), e.getErrorCode());
            fail("SQLException: " + e.getMessage());
        } finally {
            try {
                stat.close();
            } catch (Exception x) {
            }
        }
    }

    /**
     * Shortcut method to get the internal sql connection used by DbUnitModule
     * @return
     * @throws SQLException
     */
    protected Connection getConnection() throws SQLException {
    	getDefaultDbSupport().getDatabaseDialect();
        return getDbUnitDatabaseConnection(getDefaultDbSupport().getSchemaName()).getConnection();
    }

    /**
     * Clean up the table with the sap account employee multiple matches.
     * Since it is hard to access this information using Hibernate (i.e. we would have
     * to load all sap accounts and manually remove the multiple matched employees, we delete
     * the entries from the SAP_ACCOUNT_EMPLOYEE_MULTIPLE_MATCH using normal SQL.
     * 
     */
    public void cleanupSapAccountEmployeeMultipleMatchTable() {
        DbSupport dbSupport = getDefaultDbSupport();

        String schemaName = dbSupport.getSchemaName();

        StringBuilder builder = new StringBuilder();

        builder.append("delete from ").append(schemaName).append(".SAP_ACCOUNT_EMPLOYEE_MULTIPLE_MATCH");

        Statement stat = null;

        try {
            logger.debug("cleanup of SAP_ACCOUNT_EMPLOYEE_MULTIPLE_MATCH, sql: {}", builder);

            stat = getConnection().createStatement();

            int result = stat.executeUpdate(builder.toString());

            logger.debug("deleted {} sap account employee multiple match entries", result);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail("sql exception: " + e.getMessage());
        } finally {
            try {
                stat.close();
            } catch (Exception x) {
            }
        }
    }

    @Override
    protected DbUnitDatabaseConnection createDbUnitConnection(String schemaName) {
        DbUnitDatabaseConnection connection = super.createDbUnitConnection(schemaName);

        // DataSource dataSource = getDatabaseModule().activateTransactionIfNeeded();
        getDatabaseModule().activateTransactionIfNeeded();
        DataSource dataSource = DatabaseUnitils.getDataSource();

        DatabaseConfig config = connection.getConfig();

        String url = null;
        
        if (dataSource instanceof ComboPooledDataSource) {
            ComboPooledDataSource ds = (ComboPooledDataSource)dataSource;
            url = ds.getJdbcUrl();
        } else if (dataSource instanceof BasicDataSource) {
            BasicDataSource ds = (BasicDataSource) dataSource;
            url = ds.getUrl();
        } else {
            throw new RuntimeException("unable to get the jdbc url!");
        }

        logger.debug("url: {}", url);

        if (url.indexOf("hsqldb") > -1) {
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
            logger.debug("set datatype factory to {}", HsqldbDataTypeFactory.class);
        } else if (url.indexOf("sqlserver") > -1) {
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MsSqlDataTypeFactory());
            // config.setProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER,
            // new MsSqlMetadataHandler());
            logger.debug("set datatype factory to {}", MsSqlDataTypeFactory.class);
        } else if (url.indexOf("mysql") > -1) {
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
            config.setProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER, new MySqlMetadataHandler());
            logger.debug("set datatype factory to {}", MySqlDataTypeFactory.class);
        } else if (url.indexOf("jdbc:h2") > -1) {
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
            logger.debug("set datatype factory to {}", H2DataTypeFactory.class);
        } else {
            logger.warn("Unknown database type for url '{}', unable to set the datatype factory!", url);
            fail("unknown database type for url '" + url + "', unable to set the datatype factory");
        }

        return connection;
    }
}
