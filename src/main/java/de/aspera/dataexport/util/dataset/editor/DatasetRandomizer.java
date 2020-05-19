package de.aspera.dataexport.util.dataset.editor;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.NoSuchColumnException;
import org.dbunit.dataset.RowOutOfBoundsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class DatasetRandomizer {
	
	@Autowired
	private DatasetReader reader;

	public IDataSet randomizeValues(boolean keepOldData) throws DatasetRandomizerException {
		DefaultDataSet randomDataset = new DefaultDataSet();
		Random random = new Random();
		try {
			for (String tabName : reader.getTableNames()) {
				DefaultTable randomTable = new DefaultTable(reader.getMetaDataOfTable(tabName));
				List<String> uniques = reader.getUniqueAndPrimaryColNames(tabName);
				// Copy Old table
				randomTable.addTableRows(reader.getTable(tabName));
				for (String colName : reader.getRandomFields(tabName)) {
					for (int row = 0; row < reader.getRowCountOfTable(tabName); row++) {
						int randomNum = random.nextInt(reader.getRowCountOfTable(tabName));
						if (uniques != null) {
							if (!uniques.contains(colName))
								randomTable.setValue(row, colName, reader.getValueInTable(tabName, randomNum, colName));
						}
					}
				}
				if (keepOldData) {
					randomTable = maintainIdsOfTable(randomTable);
					randomTable = maintainRefsOfTable(randomTable);
				}
				randomDataset.addTable(randomTable);
			}
			if (keepOldData)
				return new CompositeDataSet(reader.getDataSet(), randomDataset);
			else
				return randomDataset;
		} catch (DatasetReaderException | TableKeysInvestigatorException | DataSetException e) {
			throw new DatasetRandomizerException(e.getMessage(), e);
		}
	}

	public IDataSet randomizeValues(String tableName, boolean keepOldData) throws DatasetRandomizerException {
		DefaultDataSet randomDataset = new DefaultDataSet();
		Random random = new Random();
		try {
			DefaultTable randomTable = new DefaultTable(reader.getMetaDataOfTable(tableName));
			List<String> uniques = reader.getUniqueAndPrimaryColNames(tableName);
			// Copy Old table
			randomTable.addTableRows(reader.getTable(tableName));
			Object obj = null;
			for (String colName : reader.getRandomFields(tableName)) {
				for (int row = 0; row < reader.getRowCountOfTable(tableName); row++) {
					int randomNum = random.nextInt(reader.getRowCountOfTable(tableName));
					if(uniques!=null) {
						if (!uniques.contains(colName))
							obj = reader.getValueInTable(tableName, randomNum, colName);
					}
					randomTable.setValue(row, colName, obj);
				}
			}
			if (keepOldData) {
				randomTable = maintainIdsOfTable(randomTable);
				randomTable.addTableRows(reader.getTable(tableName));
			}

			randomDataset.addTable(randomTable);
			// copy old tables to the new dataset
			for (String oldTableName : reader.getTableNames()) {
				if (!oldTableName.equalsIgnoreCase(tableName)) {
					randomDataset.addTable(reader.getDataSet().getTable(oldTableName));
				}
			}
		} catch (DatasetReaderException | TableKeysInvestigatorException | DataSetException e) {
			throw new DatasetRandomizerException(e.getMessage(), e);
		}
		return randomDataset;
	}

	private DefaultTable maintainIdsOfTable(DefaultTable newTable) throws TableKeysInvestigatorException,
			RowOutOfBoundsException, NoSuchColumnException, DataSetException, DatasetReaderException {
		String tabName = newTable.getTableMetaData().getTableName();
		List<String> primaryKeyNames = reader.getPrimarykeysOfTable(tabName);
		// new primary Keys
		for (String priKey : primaryKeyNames) {
			for (int i = 0; i < newTable.getRowCount(); i++) {
				newTable.setValue(i, priKey, reader.getValidUniqueKeyValue(tabName, priKey));
			}
		}
		return newTable;
	}

	private DefaultTable maintainRefsOfTable(DefaultTable newTable)
			throws DatasetReaderException, RowOutOfBoundsException, NoSuchColumnException, DataSetException {
		// reset the foreign keys to new Index
		String tabName = newTable.getTableMetaData().getTableName();
		Map<String, String> refrences = reader.getReferencesToTables(tabName);
		for (String colName : refrences.keySet()) {
			String referencedTableName = refrences.get(colName).split("\\.")[0];
			if (reader.getTableNames().contains(referencedTableName)) {
				int maxNumber = reader.getRowCountOfTable(referencedTableName);
				for (int i = 0; i < newTable.getRowCount(); i++) {
					Object oldValue = reader.getValueInTable(tabName, i, colName);
					if (oldValue != null) {
						int value = Integer.parseInt(oldValue.toString());
						newTable.setValue(i, colName, value + maxNumber);
					}
				}
			}
		}
		return newTable;
	}

}
