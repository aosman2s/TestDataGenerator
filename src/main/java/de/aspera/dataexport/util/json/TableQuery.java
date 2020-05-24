package de.aspera.dataexport.util.json;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class TableQuery {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name="TABLE_NAME")
	private String tableName;
	
	@Column(name="COLUMNS")
	private String columns;
	
	@Column(name="WHERE_CLAUSE")
	private String whereClause;
	
	@Column(name="ORDERBY_CLAUSE")
	private String orderByClause;
	
	@ManyToOne
	@JoinColumn(name="EXPORT_COMMAND_ID", nullable=false)
	private ExportJsonCommand exportCommand;

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

	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public String getOrderByClause() {
		return orderByClause;
	}

	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	public ExportJsonCommand getExportCommand() {
		return exportCommand;
	}

	public void setExportCommand(ExportJsonCommand exportCommand) {
		this.exportCommand = exportCommand;
	}

	
}
