package de.aspera.dataexport.util.dataset.editor;

import java.util.List;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class DatasetMultiplier {
	
	@Autowired
	private DatasetReader reader;

	public DefaultDataSet multiplyData(int factor)
			throws TableKeysInvestigatorException, DatasetReaderException, DatasetMultiplierException {
		DefaultDataSet bigDataset = new DefaultDataSet();
		List<String> tableNames = reader.getTableNames();
		List<String> uniqesOfTable = null;
		try {
			for (String tabName : tableNames) {
				DefaultTable bigTable = new DefaultTable(reader.getMetaDataOfTable(tabName));
				bigTable.addTableRows(reader.getTable(tabName));
				uniqesOfTable = reader.getUniqueAndPrimaryColNames(tabName);
				for (int y = 0; y < factor; y++) {
					DefaultTable copyTable = new DefaultTable(reader.getMetaDataOfTable(tabName));
					copyTable.addTableRows(reader.getTable(tabName));
					if (uniqesOfTable != null) {
						for (String uniqeCol : uniqesOfTable) {
							for (int i = 0; i < copyTable.getRowCount(); i++) {
								copyTable.setValue(i, uniqeCol, reader.getValidUniqueKeyValue(tabName, uniqeCol));
							}
						}
					}
					bigTable.addTableRows(copyTable);
				}
				bigDataset.addTable(bigTable);
			}
		} catch (DataSetException e) {
			throw new DatasetMultiplierException(e.getMessage(), e);
		}
		return bigDataset;
	}

	public DefaultDataSet multiplyRowInTable(String tableName, int row, int factor)
			throws TableKeysInvestigatorException, DatasetReaderException, DatasetMultiplierException {
		DefaultDataSet bigDataset = new DefaultDataSet();
		DefaultTable bigTable = new DefaultTable(reader.getMetaDataOfTable(tableName));
		List<String> uniqesOfTable = null;
		try {
			bigTable.addTableRows(reader.getTable(tableName));
			uniqesOfTable = reader.getUniqueAndPrimaryColNames(tableName);
			for (int y = 0; y < factor; y++) {
				List<String> colNames = reader.getColumnNamesOfTable(tableName);
				Object[] valuesOfRow = new Object[colNames.size()];
				int j = 0;
				for (String colName : colNames) {
					if (uniqesOfTable != null && uniqesOfTable.contains(colName)) {
						valuesOfRow[j] = reader.getValidUniqueKeyValue(tableName, colName);
					} else {
						valuesOfRow[j] = reader.getValueInTable(tableName, row, colName);
					}
					j++;
				}
				bigTable.addRow(valuesOfRow);
			}
			bigDataset.addTable(bigTable);
			// copy other tables into the new dataset
			List<String> oldTableNames = reader.getTableNames();
			for (String oldTableName : oldTableNames) {
				if (!oldTableName.equalsIgnoreCase(tableName)) {
					bigDataset.addTable(reader.getDataSet().getTable(oldTableName));
				}
			}
		} catch (DataSetException e) {
			throw new DatasetMultiplierException(e.getMessage(), e);
		}
		return bigDataset;
	}

	public DefaultDataSet multiplyDataInTable(String tableName, int factor)
			throws TableKeysInvestigatorException, DatasetReaderException, DatasetMultiplierException {
		DefaultDataSet bigDataset = new DefaultDataSet();
		List<String> uniqesOfTable = null;
		DefaultTable bigTable = new DefaultTable(reader.getMetaDataOfTable(tableName));
		try {
			bigTable.addTableRows(reader.getTable(tableName));
			uniqesOfTable = reader.getUniqueAndPrimaryColNames(tableName);
			for (int y = 0; y < factor; y++) {
				DefaultTable copyTable = new DefaultTable(reader.getMetaDataOfTable(tableName));
				copyTable.addTableRows(reader.getTable(tableName));
				if (uniqesOfTable != null) {
					for (String uniqeCol : uniqesOfTable) {
						for (int i = 0; i < copyTable.getRowCount(); i++) {
							copyTable.setValue(i, uniqeCol, reader.getValidUniqueKeyValue(tableName, uniqeCol));
						}
					}
				}
				bigTable.addTableRows(copyTable);
			}
			bigDataset.addTable(bigTable);
			// copy other tables into the new dataset
			List<String> oldTableNames = reader.getTableNames();
			for (String oldTableName : oldTableNames) {
				if (!oldTableName.equalsIgnoreCase(tableName)) {
					bigDataset.addTable(reader.getDataSet().getTable(oldTableName));
				}
			}
		} catch (DataSetException e) {
			throw new DatasetMultiplierException(e.getMessage(), e);
		}
		return bigDataset;
	}

}
