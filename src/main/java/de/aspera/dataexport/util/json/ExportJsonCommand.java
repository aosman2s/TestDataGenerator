package de.aspera.dataexport.util.json;

import java.util.List;

public class ExportJsonCommand {
	private String commandId;
	private String connId;
	private List<TableQuery> tables;
	private String exportedFilePath;

	public List<TableQuery> getTables() {
		return tables;
	}

	public void setTables(List<TableQuery> tables) {
		this.tables = tables;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public String getConnId() {
		return connId;
	}

	public void setConnId(String connId) {
		this.connId = connId;
	}

	public String getExportedFilePath() {
		return exportedFilePath;
	}

	public void setExportedFilePath(String exportedFilePath) {
		this.exportedFilePath = exportedFilePath;
	}

}
