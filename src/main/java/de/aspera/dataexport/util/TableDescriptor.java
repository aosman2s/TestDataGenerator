package de.aspera.dataexport.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableDescriptor {
	private static final Logger logger = LoggerFactory.getLogger(TableDescriptor.class);

    /*
     * Class attributes
     */
    private String              schemaName;
    private final String        tableName;
    private String              whereClause;
    private String              orderByClause;
    private final List<String>  fields = new ArrayList<String>();

    /**
     * Constructor
     * @param tableName
     */
    public TableDescriptor(String tableName) {
        this.tableName = tableName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSql() {
        StringBuilder sql = new StringBuilder();

        sql.append("select ");

        Iterator<String> it = fields.iterator();

        while (it.hasNext()) {
            String fieldName = it.next();
            sql.append(fieldName);

            if (it.hasNext()) {
                sql.append(", ");
            }
        }

        appendFromClause(sql);

        if (StringUtils.isNotBlank(this.getWhereClause())) {
            sql.append(" where ").append(getWhereClause());
        }

        if (StringUtils.isNotBlank(this.getOrderByClause())) {
            sql.append(" order by ").append(getOrderByClause());
        }

        return sql.toString();
    }

    public void appendFromClause(StringBuilder sql) {
        sql.append(" from ").append(getSchemaName()).append(".").append(getTableName());
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public String getTableName() {
        return tableName;
    }

    public void addFields(String... fields) {

        int length = fields.length;

        for (int i = 0; i < length; i++) {
            String field = fields[i];
            addField(field);
        }
    }

    public void addField(String fieldName) {
        if (this.fields.contains(fieldName)) {

        } else {
            fields.add(fieldName);
        }
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }


}

