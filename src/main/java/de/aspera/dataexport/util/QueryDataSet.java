package de.aspera.dataexport.util;


import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.database.IDatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryDataSet extends org.dbunit.database.QueryDataSet {

    private String                        schemaName;

    private static final Logger logger = LoggerFactory.getLogger(QueryDataSet.class);

    public QueryDataSet(IDatabaseConnection connection, String schemaName) {
        super(connection);
        this.schemaName = schemaName;
    }

    public void addQry(TableDescriptor qry) throws AmbiguousTableNameException {
        qry.setSchemaName(schemaName);

        String sql = qry.getSql();
        logger.debug("using sql: {}", sql);

        addTable(qry.getTableName(), sql);

    }
}