package de.aspera.dataexport.util.dataset.editor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatasetReader {
	@Autowired
	private TableKeysInvestigator tableInvestigator;
	
	private IDataSet dataset;
	private Map<String, ITable> tablesMap;
	private Map<String, TableConstrainsDescription> tablesConstraints;

	public void readDataset(String filePath) throws DatasetReaderException {
		try {
			this.dataset = new FlatXmlDataSetBuilder().build(new FileInputStream(filePath));
		} catch (DataSetException | FileNotFoundException e) {
			throw new DatasetReaderException(e.getMessage(), e);
		}
		buildTableMap();
	}

	public List<String> getTableNames() {
		return new ArrayList<String>(tablesMap.keySet());
	}

	public List<ITable> getTables() throws DataSetException {
		List<ITable> tables = new ArrayList<ITable>();
		for (String tabName : getTableNames()) {
			tables.add(getTable(tabName));
		}
		return tables;
	}

	public ITable getTable(String tabName) throws DataSetException {
		return dataset.getTable(tabName);
	}

	public int getRowCountOfTable(String tableName) {
		return this.tablesMap.get(tableName).getRowCount();
	}

	public void setDataset(IDataSet dataset) throws DatasetReaderException {
		this.dataset = dataset;
		buildTableMap();
	}

	public List<String> getColumnNamesOfTable(String tableName) throws DatasetReaderException {
		List<String> colNames = new ArrayList<String>();
		try {
			Column[] cols = tablesMap.get(tableName).getTableMetaData().getColumns();
			for (int i = 0; i < cols.length; i++) {
				colNames.add(cols[i].getColumnName());
			}
		} catch (DataSetException e) {
			throw new DatasetReaderException(e.getMessage(), e);
		}
		return colNames;
	}

	private void buildTableMap() throws DatasetReaderException {
		if (dataset == null) {
			throw new DatasetReaderException("DataSet is null in the reader!");
		}
		tablesMap = new HashMap<String, ITable>();
		String[] tableNames;
		try {
			tableNames = dataset.getTableNames();
			for (int i = 0; i < tableNames.length; i++) {
				ITable table = dataset.getTable(tableNames[i]);
				tablesMap.put(tableNames[i], table);
			}
		} catch (DataSetException e) {
			throw new DatasetReaderException(e.getMessage(), e);
		}

	}

	public ITableMetaData getMetaDataOfTable(String tableName) throws DatasetReaderException {
		if (tablesMap.get(tableName) == null) {
			throw new DatasetReaderException("Table does not exist in the Dataset");
		}
		return tablesMap.get(tableName).getTableMetaData();
	}

	public Object getValueInTable(String tableName, int row, String colName) throws DatasetReaderException {
		Object value = null;
		try {
			value = tablesMap.get(tableName).getValue(row, colName);
		} catch (DataSetException e) {
			throw new DatasetReaderException(e.getMessage(), e);
		}
		return value;
	}

	public Map<String, String> getRowOfTable(String tableName, int row) throws DatasetReaderException {
		Map<String, String> colNameValueMap = new HashMap<String, String>();
		List<String> colNames = getColumnNamesOfTable(tableName);
		for (String colName : colNames) {
			colNameValueMap.put(colName, getValueInTable(tableName, row, colName).toString());
		}
		return colNameValueMap;
	}

	public int getMaxNumberinColumnFromDataSet(String tableName, String colName) throws DatasetReaderException {
		int numberOfRows = getRowCountOfTable(tableName);
		int maxNum = 0;
		for (int i = 0; i < numberOfRows; i++) {
			int currentValue;
			currentValue = (int) getValueInTable(tableName, i, colName);
			if (currentValue > maxNum)
				maxNum = currentValue;
		}
		return maxNum;
	}

	public IDataSet getDataSet() {
		return dataset;
	}

	public void setTableKeyInvestigator() throws TableKeysInvestigatorException {
		this.tablesConstraints = tableInvestigator.createTableConstrainsDescriptions(getTableNames());
	}

	public String getValidUniqueKeyValue(String tabName, String colName) throws TableKeysInvestigatorException {
		return tablesConstraints.get(tabName).getValidUniqueKeyValue(colName);
	}

	// will be useful when using foreign keys
	public List<String> getPrimarykeysOfTable(String tableName) throws TableKeysInvestigatorException {
		if (tableInvestigator == null) {
			throw new TableKeysInvestigatorException("table investigator is null!");
		}
		return tablesConstraints.get(tableName).getPrimaryKeyNames();
	}

	public List<String> getUniqueAndPrimaryColNames(String tableName) throws TableKeysInvestigatorException {
		if (tablesConstraints == null) {
			return null;
		}
		return tablesConstraints.get(tableName).getUniqueAndPrimaryColNames();
	}

	public boolean isTableInvestigatorNull() {
		if (tableInvestigator == null)
			return true;
		else
			return false;
	}

	public List<String> getColNamesOfTable(String tableName) {
		return tablesConstraints.get(tableName).getColNames();
	}

	public Map<String, String> getReferencesToTables(String tableName) {
		return tablesConstraints.get(tableName).getReferencesToTables();
	}

	public void setRandomFields(List<String> fields) {
		for (String str : fields) {
			String tabName = str.split("\\.")[0];
			String fieldName = str.split("\\.")[1];
			TableConstrainsDescription tabCons = tablesConstraints.get(tabName);
			if (tabCons != null) {
				if (tabCons.getRandomFields() != null) {
					tabCons.getRandomFields().add(fieldName);
				} else {
					Set<String> fieldSet = new HashSet<String>();
					fieldSet.add(fieldName);
					tabCons.setRandomFields(fieldSet);
				}
			}
		}
	}

	public List<String> getRandomFields(String tableName) throws DatasetReaderException {
		Set<String> randomFields = tablesConstraints.get(tableName).getRandomFields();
		if (randomFields == null) {
			throw new DatasetReaderException("Random fields are not defined for table: " + tableName);
		}
		return new ArrayList<String>(randomFields);
	}

	public boolean isNotNullable(String tableName, String colName) {
		return tablesConstraints.get(tableName).getNotNullableColumns().contains(colName);
	}
	
	public void addTableDescriptionContriant (String tableName, TableConstrainsDescription tabDesc) {
		if (tablesConstraints ==null) {
			tablesConstraints= new HashMap<String, TableConstrainsDescription>();
		}
		tablesConstraints.put(tableName, tabDesc);
		
	}
}
