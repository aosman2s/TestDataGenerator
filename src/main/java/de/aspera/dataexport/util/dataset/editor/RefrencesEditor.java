package de.aspera.dataexport.util.dataset.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RefrencesEditor {
	@Autowired
	private DatasetReader reader;


	public IDataSet maintainDataIntegrity(DefaultDataSet bigDataset) throws TableKeysInvestigatorException {
		DefaultTable table = null;
		DefaultTable referencedTable = null;
		boolean doRefrence = true;
		Random random = new Random();
		for (String tableName : reader.getTableNames()) {
			Map<String, String> refrences = reader.getReferencesToTables(tableName);
			for (String colName : refrences.keySet()) {
				String referencedTableName = refrences.get(colName).split("\\.")[0];
				String referencedColName = refrences.get(colName).split("\\.")[1];
				boolean notNullable = reader.isNotNullable(tableName, colName);
				try {
					if (reader.getTableNames().contains(referencedTableName)) {
						table = (DefaultTable) bigDataset.getTable(tableName);
						referencedTable = (DefaultTable) bigDataset.getTable(referencedTableName);
						int indexOfRow = reader.getRowCountOfTable(tableName);
						int rowIndexOfRefTable = reader.getRowCountOfTable(referencedTableName);
						int randomNum = random.nextInt(referencedTable.getRowCount() / 3);
						List<String> uniques = reader.getUniqueAndPrimaryColNames(tableName);
						int j = 0;
						for (int i = indexOfRow; i < table.getRowCount(); i++) {
							if (!notNullable)
								doRefrence = random.nextBoolean();
							if (doRefrence) {
								Object referencedValue = referencedTable.getValue(rowIndexOfRefTable,
										referencedColName);
								table.setValue(i, colName, referencedValue);
								if (uniques.contains(colName)) {
									// 1:1 Relation
									if (rowIndexOfRefTable < referencedTable.getRowCount())
										rowIndexOfRefTable++;
									else
										break;
								} else if (j == randomNum && rowIndexOfRefTable < referencedTable.getRowCount()) {
									// 1:N Relation
									rowIndexOfRefTable++;
									j = 0;
								}
							}
							j++;
						}
					}
				} catch (DataSetException e) {
					// Ignore the Exception if the referenced Table does not exist in dataset
					if (referencedTable != null)
						throw new TableKeysInvestigatorException(e.getMessage(), e);
				}
			}
		}
		return bigDataset;
	}

	public IDataSet editForNewImport() throws DataSetException, TableKeysInvestigatorException {
		DefaultDataSet editedDataset = new DefaultDataSet();
		List<DefaultTable> defTables = new ArrayList<DefaultTable>();
		List<ITable> tables = reader.getTables();
		for (ITable table : tables) {
			DefaultTable defTable = new DefaultTable(table.getTableMetaData());
			defTable.addTableRows(table);
			defTables.add(defTable);
		}
		Map<String, Map<String, String>> columnOldNewValues = editPrimaryKeys(defTables);
		editForeignKeys(defTables, columnOldNewValues);
		for(DefaultTable deftable :defTables) {
			editedDataset.addTable(deftable);
		}
		return editedDataset;
	}

	private void editForeignKeys(List<DefaultTable> defTables, Map<String, Map<String, String>> columnOldNewValues)
			throws DataSetException {
		for (DefaultTable defTable : defTables) {
			String tabName = defTable.getTableMetaData().getTableName();
			Map<String, String> forKeyTablePkMap = reader.getReferencesToTables(tabName);
			for (String foreignkey : forKeyTablePkMap.keySet()) {
				String refrencedTable = forKeyTablePkMap.get(foreignkey).split("\\.")[0];
				if (reader.getTableNames().contains(refrencedTable)) {
					Map<String, String> oldNewValues = columnOldNewValues.get(forKeyTablePkMap.get(foreignkey));
					for (int i = 0; i < defTable.getRowCount(); i++) {
						Object oldValue = defTable.getValue(i, foreignkey);
						if(oldValue!=null) {
							String newValue = oldNewValues.get(oldValue.toString());
							defTable.setValue(i, foreignkey, newValue);
						}
					}
				}
			}
		}
	}

	private Map<String, Map<String, String>> editPrimaryKeys(List<DefaultTable> defTables)
			throws DataSetException, TableKeysInvestigatorException {
		Map<String, Map<String, String>> colOldNewValues = new HashMap<String, Map<String, String>>();
		for (DefaultTable defTable : defTables) {
			String tabName = defTable.getTableMetaData().getTableName();
			List<String> primaryKeys = reader.getUniqueAndPrimaryColNames(tabName);
			for (String primKey : primaryKeys) {
				Map<String, String> oldNewVlaues = new HashMap<String, String>();
				for (int i = 0; i < defTable.getRowCount(); i++) {
					Object oldValue = defTable.getValue(i, primKey);
					String newValue = reader.getValidUniqueKeyValue(tabName, primKey);
					defTable.setValue(i, primKey, newValue);
					oldNewVlaues.put(oldValue.toString(), newValue);
				}
				colOldNewValues.put(tabName + "." + primKey, oldNewVlaues);
			}
		}
		return colOldNewValues;
	}
}
