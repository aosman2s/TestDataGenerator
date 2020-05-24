package de.aspera.dataexport.util.json;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class ExportJsonCommand {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int commandId;

	@OneToMany(mappedBy="exportCommand")
	private List<TableQuery> tables;

	@Column(name = "EXPORT_PATH")
	private String exportedFilePath;

	@ManyToOne
	@JoinColumn(name="DATABASE_ID", nullable=false)
	private JsonDatabase dataBase;

	public int getCommandId() {
		return commandId;
	}

	public void setCommandId(int commandId) {
		this.commandId = commandId;
	}

	public List<TableQuery> getTables() {
		return tables;
	}

	public void setTables(List<TableQuery> tables) {
		this.tables = tables;
	}

	public String getExportedFilePath() {
		return exportedFilePath;
	}

	public void setExportedFilePath(String exportedFilePath) {
		this.exportedFilePath = exportedFilePath;
	}

	public JsonDatabase getDataBase() {
		return dataBase;
	}

	public void setDataBase(JsonDatabase dataBase) {
		this.dataBase = dataBase;
	}

	@Override
	public String toString() {
		return "ExportJsonCommand [commandId=" + commandId + ", exportedFilePath="
				+ exportedFilePath + ", dataBase=" + dataBase.getId() + "]";
	}
	

}
