package de.aspera.dataexport.cmd;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.dbunit.dataset.IDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.aspera.dataexport.util.dataset.editor.DatasetEditorException;
import de.aspera.dataexport.util.dataset.editor.DatasetEditorFacade;
import de.aspera.dataexport.util.dataset.editor.DatasetMultiplierException;
import de.aspera.dataexport.util.dataset.editor.DatasetRandomizerException;
import de.aspera.dataexport.util.dataset.editor.DatasetReaderException;
import de.aspera.dataexport.util.dataset.editor.DatasetRowEditorException;
import de.aspera.dataexport.util.dataset.editor.TableKeysInvestigatorException;
@Component
public class DatasetEditorUserFacade {
	@Autowired
	private DatasetEditorFacade facade;

	public void setEditor(ByteArrayInputStream inputStream)
			throws DatasetEditorException, DatasetReaderException, IOException {
		facade.readDataset(inputStream);
	}

	public void multiplyData(int factor)
			throws TableKeysInvestigatorException, DatasetReaderException, DatasetMultiplierException {
		facade.multiplyData(factor);
	}

	public void multiplyRowInTable(String tableName, int row, int factor)
			throws TableKeysInvestigatorException, DatasetReaderException, DatasetMultiplierException {
		facade.multiplyRowInTable(tableName, row, factor);
	}

	public void multiplyDataInTable(String tableName, int factor)
			throws TableKeysInvestigatorException, DatasetReaderException, DatasetMultiplierException {
		facade.multiplyDataInTable(tableName, factor);
	}

	public void changeValuesInRow(String tableName, int row, Map<String, String> newValuesColName)
			throws DatasetReaderException, TableKeysInvestigatorException, DatasetRowEditorException {
		facade.changeValuesInRow(tableName, row, newValuesColName);
	}

	public void addRow(String tableName, Map<String, String> newValuesColName)
			throws TableKeysInvestigatorException, DatasetReaderException, DatasetRowEditorException {
		facade.addRow(tableName, newValuesColName);
	}

	public void setRandomFields(List<String> fields) {
		facade.setRandomFields(fields);
	}

	public void randomizeValues(boolean keepOldData) throws DatasetReaderException, DatasetRandomizerException {
		facade.randomizeValues(keepOldData);
	}

	public void randomizeValues(String tableName, boolean keepOldData)
			throws DatasetReaderException, DatasetRandomizerException {
		facade.randomizeValues(tableName, keepOldData);
	}

	protected void setConnectionOfDB(Connection connection) throws TableKeysInvestigatorException {
		facade.setConnectionOfDB(connection);

	}

	protected IDataSet getDataSet() {
		return facade.getDataSet();
	}

}
