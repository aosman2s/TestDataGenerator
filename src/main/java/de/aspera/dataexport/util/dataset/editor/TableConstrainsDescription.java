package de.aspera.dataexport.util.dataset.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;

import de.aspera.dataexport.dataFaker.DataFaker;

public class TableConstrainsDescription {
	private Map<String, String> columnNamesType;
	private Map<String, Integer> numericUniqueColValueMap;
	private Map<String, Set<String>> charUniqueColValueMap;
	private Map<String, Integer> numericPrimaryKeyValueMap;
	private Map<String, Set<String>> charPrimaryKeyValueMap;
	private Map<String, Set<String>> referencedFromTables;
	private Map<String, String> referencesToTables;
	private Set<String> randomFields;
	private Set<String> notNullableColumns;
	@Autowired
	private DataFaker faker;
	
	

	public TableConstrainsDescription() {
		this.columnNamesType = new HashedMap<String, String>();
		this.numericUniqueColValueMap = new HashedMap<String, Integer>();
		this.charUniqueColValueMap = new HashedMap<String, Set<String>>();
		this.numericPrimaryKeyValueMap = new HashedMap<String, Integer>();
		this.charPrimaryKeyValueMap = new HashedMap<String, Set<String>>();
		this.referencedFromTables = new HashedMap<String, Set<String>>();
		this.referencesToTables = new HashedMap<String, String>();
		this.randomFields = new HashSet<String>();
		this.notNullableColumns = new HashSet<String>();
	}

	public void setNotNullableColumn(Set<String> notNullableColumns) {
		this.notNullableColumns = notNullableColumns;
	}

	public List<String> getNotNullableColumns() {
		return new ArrayList<String>(this.notNullableColumns);
	}

	public Set<String> getRandomFields() {
		return randomFields;
	}

	public void setRandomFields(Set<String> randomFields) {
		this.randomFields = randomFields;
	}

	public void setUniqueNumericColTypeMap(Map<String, Integer> numericUniqueColValueMap) {
		this.numericUniqueColValueMap = numericUniqueColValueMap;
	}

	public void setReferencedFromTables(Map<String, Set<String>> referencedFromTables) {
		this.referencedFromTables = referencedFromTables;
	}

	public void setReferencesToTables(Map<String, String> referencesToTables) {
		this.referencesToTables = referencesToTables;
	}

	public void setUniqueCharColTypeMap(Map<String, Set<String>> charUniqueColValueMap) {
		this.charUniqueColValueMap = charUniqueColValueMap;
	}

	public void setNumericPrimaryKeyValueMap(Map<String, Integer> numericPrimaryKeyValueMap) {
		this.numericPrimaryKeyValueMap = numericPrimaryKeyValueMap;
	}

	public void setCharPrimaryKeyValueMap(Map<String, Set<String>> charPrimaryKeyValueMap) {
		this.charPrimaryKeyValueMap = charPrimaryKeyValueMap;
	}

	public void setColumnNamesType(Map<String, String> columnNamesType) {
		this.columnNamesType = columnNamesType;
	}

	public Set<String> getReferencedFromTables() {
		Set<String> combinedTableNames = new HashSet<String>();
		for (String colName : referencedFromTables.keySet()) {
			combinedTableNames.addAll(referencedFromTables.get(colName));
		}
		return combinedTableNames;
	}

	public Map<String, String> getReferencesToTables() {
		return referencesToTables;
	}

	public List<String> getColNames() {
		return new ArrayList<String>(columnNamesType.keySet());
	}

	public List<String> getPrimaryKeyNames() {
		Set<String> combinedPrimary = new HashSet<String>();
		combinedPrimary.addAll(charPrimaryKeyValueMap.keySet());
		combinedPrimary.addAll(numericPrimaryKeyValueMap.keySet());
		return new ArrayList<String>(combinedPrimary);
	}

	public List<String> getUniqueColNames() {
		Set<String> combinedUniques = new HashSet<String>();
		combinedUniques.addAll(charUniqueColValueMap.keySet());
		combinedUniques.addAll(numericUniqueColValueMap.keySet());
		return new ArrayList<String>(combinedUniques);
	}

	public List<String> getUniqueAndPrimaryColNames() {
		Set<String> combinedAllUniques = new HashSet<String>();
		combinedAllUniques.addAll(charUniqueColValueMap.keySet());
		combinedAllUniques.addAll(numericUniqueColValueMap.keySet());
		combinedAllUniques.addAll(charPrimaryKeyValueMap.keySet());
		combinedAllUniques.addAll(numericPrimaryKeyValueMap.keySet());
		return new ArrayList<String>(combinedAllUniques);
	}

	public String getValidUniqueKeyValue(String colName) throws TableKeysInvestigatorException {
		if (!getUniqueAndPrimaryColNames().contains(colName)) {
			// TODO: exceptions!!
			throw new TableKeysInvestigatorException("column: " + colName + " not found in Unique Columns");
		}
		if (numericPrimaryKeyValueMap.containsKey(colName) || numericUniqueColValueMap.containsKey(colName)) {
			return getNextValidNumberUniqueKey(colName);
		} else {
			return getNextValidCharUniqueKey(colName);
		}

	}

	private String getNextValidNumberUniqueKey(String colName) throws TableKeysInvestigatorException {
		int current;
		if (numericPrimaryKeyValueMap.containsKey(colName)) {
			current = numericPrimaryKeyValueMap.get(colName);
		} else {
			current = numericUniqueColValueMap.get(colName);
		}
		numericPrimaryKeyValueMap.put(colName, ++current);
		return Integer.toString(current);
	}

	private String getNextValidCharUniqueKey(String colName) throws TableKeysInvestigatorException {
		String validStr;
		String typeOfKey;
		Set<String> valuesSet;
		typeOfKey = columnNamesType.get(colName);
		if (charPrimaryKeyValueMap.containsKey(colName)) {
			valuesSet = charPrimaryKeyValueMap.get(colName);
		} else {
			valuesSet = charUniqueColValueMap.get(colName);
		}

		int strLength = Integer.parseInt(typeOfKey.split(",")[1]);
		validStr = faker.fakeStringWithLength(strLength);
		while (!valuesSet.add(validStr)) {
			// TODO: make the implementation of the faker better
			validStr = faker.fakeStringWithLength(strLength);
		}
		return validStr;
	}
}
