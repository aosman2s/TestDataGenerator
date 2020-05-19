package de.aspera.dataexport.util.dataset.editor;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatasetEditorFacade {
	@Autowired
	private DatasetReader reader;
	@Autowired
	private DatasetMultiplier multiplier;
	@Autowired
	private DatasetRowEditor rowEditor;
	@Autowired
	private TableKeysInvestigator tableInvestigator;
	@Autowired
	private DatasetRandomizer randomizer;
	@Autowired
	private RefrencesEditor refEditor;

	public void readDataset(String filePath) throws DatasetReaderException {
		reader.readDataset(filePath);
	}

	public void readDataset(InputStream stream) throws DatasetEditorException, DatasetReaderException, IOException {
		IDataSet dataset;
		try {
			dataset = new FlatXmlDataSetBuilder().build(stream);
			reader.setDataset(dataset);
		} catch (DataSetException e) {
			throw new DatasetEditorException(e.getMessage(), e);
		}
	}

	public void readDataset(IDataSet dataset) throws DatasetReaderException {
		reader.setDataset(dataset);
	}

	public List<String> getTableNames() {
		return reader.getTableNames();
	}

	public int getRowCountOfTable(String tableName) {
		return reader.getRowCountOfTable(tableName);
	}

	public List<String> getColumnNamesOfTable(String tableName) throws DatasetReaderException {
		return reader.getColumnNamesOfTable(tableName);
	}

	public String getValueInTable(String tableName, int row, String colName) throws DatasetReaderException {
		return reader.getValueInTable(tableName, row, colName).toString();
	}

	public Map<String, String> getRowOfTable(String tableName, int row) throws DatasetReaderException {
		return reader.getRowOfTable(tableName, row);
	}

	public void multiplyData(int factor)
			throws TableKeysInvestigatorException, DatasetReaderException, DatasetMultiplierException {
		DefaultDataSet datset = multiplier.multiplyData(factor);
		reader.setDataset(refEditor.maintainDataIntegrity(datset));
	}

	public void multiplyRowInTable(String tableName, int row, int factor)
			throws TableKeysInvestigatorException, DatasetReaderException, DatasetMultiplierException {
		reader.setDataset(multiplier.multiplyRowInTable(tableName, row, factor));
	}

	public void multiplyDataInTable(String tableName, int factor)
			throws TableKeysInvestigatorException, DatasetReaderException, DatasetMultiplierException {
		DefaultDataSet datset = multiplier.multiplyDataInTable(tableName, factor);
		reader.setDataset(refEditor.maintainDataIntegrity(datset));
	}

	public void changeValuesInRow(String tableName, int row, Map<String, String> newValuesColName)
			throws DatasetReaderException, TableKeysInvestigatorException, DatasetRowEditorException {
		reader.setDataset(rowEditor.changeValuesInRow(tableName, row, newValuesColName));
	}

	public void addRow(String tableName, Map<String, String> newValuesColName)
			throws TableKeysInvestigatorException, DatasetReaderException, DatasetRowEditorException {
		reader.setDataset(rowEditor.addRow(tableName, newValuesColName));
	}

	public void setConnectionOfDB(Connection conn) throws TableKeysInvestigatorException {
		tableInvestigator.setConnection(conn);
		reader.setTableKeyInvestigator();
	}

	public void setRandomFields(List<String> fields) {
		reader.setRandomFields(fields);
	}

	public void randomizeValues(boolean keepOldData) throws DatasetReaderException, DatasetRandomizerException {
		reader.setDataset(randomizer.randomizeValues(keepOldData));
	}

	public void randomizeValues(String tableName, boolean keepOldData)
			throws DatasetReaderException, DatasetRandomizerException {
		reader.setDataset(randomizer.randomizeValues(tableName, keepOldData));
	}

	public IDataSet getDataSet() {
		return reader.getDataSet();
	}

	public IDataSet editForNewImport() throws DataSetException, TableKeysInvestigatorException {
		return refEditor.editForNewImport();

	}

}
