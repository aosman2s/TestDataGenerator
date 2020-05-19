package de.aspera.dataexport.util.dataset.editor;

import java.util.List;
import java.util.Map;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class DatasetRowEditor {
	@Autowired
	private DatasetReader reader;


	/*
	 * The Map of the new values must not have all the Column names, only the ones
	 * to be changed
	 */
	public IDataSet changeValuesInRow(String tableName, int row, Map<String, String> newValuesColName)
			throws TableKeysInvestigatorException, DatasetRowEditorException, DatasetReaderException {
		DefaultTable table = new DefaultTable(reader.getMetaDataOfTable(tableName));
		DefaultDataSet editedDataSet = null;
		List<String> uniqueCols = reader.getUniqueAndPrimaryColNames(tableName);
		Map<String, String> colNameValueMap = reader.getRowOfTable(tableName, row);
		for (String colName : newValuesColName.keySet()) {
			// don't exchange values of the Unique keys
			if (uniqueCols != null) {
				// filter the Unique keys out
				if (!uniqueCols.contains(colName)) {
					String value = newValuesColName.get(colName);
					colNameValueMap.put(colName, value);
				}
			} else {
				// update all values without taking the Unique keys into consideration
				String value = newValuesColName.get(colName);
				colNameValueMap.put(colName, value);
			}
		}
		try {
			// Copy Old table
			table.addTableRows(reader.getTable(tableName));
			for (String colName : colNameValueMap.keySet()) {
				table.setValue(row, colName, colNameValueMap.get(colName));
			}
			editedDataSet = new DefaultDataSet();
			for (String name : reader.getTableNames()) {
				if (name.equals(tableName)) {
					editedDataSet.addTable(table);
				} else {
					editedDataSet.addTable(reader.getDataSet().getTable(name));
				}
			}
		} catch (DataSetException e) {
			throw new DatasetRowEditorException(e.getMessage(), e);
		}
		return editedDataSet;
	}

	public IDataSet addRow(String tableName, Map<String, String> newValuesColName)
			throws TableKeysInvestigatorException,  DatasetRowEditorException, DatasetReaderException {
		DefaultTable table;
		DefaultDataSet editedDataSet = null;
		ITableMetaData metaData = reader.getMetaDataOfTable(tableName);
		try {
			// the table have no columns
			if (metaData.getColumns().length == 0 && !reader.isTableInvestigatorNull()) {
				List<String> colNames = reader.getColNamesOfTable(tableName);
				Column[] cols = new Column[colNames.size()];
				for (int i = 0; i < colNames.size(); i++) {
					cols[i] = new Column(colNames.get(i), DataType.UNKNOWN);
				}
				table = new DefaultTable(tableName, cols);
			} else {
				table = new DefaultTable(metaData);
			}
			if (!reader.isTableInvestigatorNull()) {
				List<String> tableColUniques = reader.getUniqueAndPrimaryColNames(tableName);
				// get primary Keys of the Table and update the Values in the Map of Values
				for (String colName : tableColUniques) {
					newValuesColName.put(colName, reader.getValidUniqueKeyValue(tableName,colName));
				}
			}

			// Copy Old table
			table.addTableRows(reader.getTable(tableName));
			// add new Row at the end
			int indexOfLastRow = table.getRowCount();
			table.addRow();
			for (String key : newValuesColName.keySet()) {
				table.setValue(indexOfLastRow, key, newValuesColName.get(key));
			}
			// replace the old table in the dataset
			editedDataSet = new DefaultDataSet();
			for (String name : reader.getTableNames()) {
				if (name.equals(tableName)) {
					editedDataSet.addTable(table);
				} else {
					editedDataSet.addTable(reader.getDataSet().getTable(name));
				}
			}
		} catch (DataSetException e) {
			throw new DatasetRowEditorException(e.getMessage(), e);
		}

		return editedDataSet;
	}

}
