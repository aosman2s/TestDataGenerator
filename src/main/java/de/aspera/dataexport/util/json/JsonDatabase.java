package de.aspera.dataexport.util.json;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * This class will use by JSON read/write operations with google gson and it
 * will be use as dto object to transport database connection informations.
 * 
 * @author adidweis
 *
 */
@Entity
@Table(name = "DATABASE_CONNECTION")
public class JsonDatabase {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name = "DB_DRIVER",nullable = false)
	private String dbDriver;
	
	@Column(name = "DB_URL",nullable = false)
	private String dbUrl;
	
	@Column(name = "DB_USER",nullable = false)
	private String dbUser;
	
	@Column(name = "DB_PASSWORD",nullable = false)
	private String dbPassword;
	
	@Column(name = "DB_SCHEMA")
	private String dbSchema;

	@OneToMany(mappedBy="dataBase")
	private List<ExportJsonCommand> exportCommands;
	
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getDbSchema() {
		return dbSchema;
	}

	public void setDbSchema(String dbSchema) {
		this.dbSchema = dbSchema;
	}

	public List<ExportJsonCommand> getExportCommands() {
		return exportCommands;
	}

	public void setExportCommands(List<ExportJsonCommand> exportCommands) {
		this.exportCommands = exportCommands;
	}

	@Override
	public String toString() {
		return "JsonDatabase [id=" + id + ", dbDriver=" + dbDriver + ", dbUrl=" + dbUrl + ", dbUser=" + dbUser
				+ ", dbPassword=" + dbPassword + ", dbSchema=" + dbSchema + ", exportCommands=" + exportCommands + "]";
	}
	
	

}
