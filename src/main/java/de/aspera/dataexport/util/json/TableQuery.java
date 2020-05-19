package de.aspera.dataexport.util.json;

public class TableQuery {
	private String tableName;
	private String columns;
	private String whereCondition;
	private String orderByCondition;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getWhereCondition() {
		return whereCondition;
	}

	public void setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
	}

	public String getOrderByCondition() {
		return orderByCondition;
	}

	public void setOrderByCondition(String orderByCondition) {
		this.orderByCondition = orderByCondition;
	}
}
