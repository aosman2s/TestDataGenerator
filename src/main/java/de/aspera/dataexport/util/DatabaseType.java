package de.aspera.dataexport.util;


/**
 * Enumeration specifying known und supported database types.
 *
 * @author Petr Stastny
 *
 */
public enum DatabaseType {

    UNKNOWN("unknown"), HSQL("hsql"), H2("h2"), MYSQL("mysql"), MSSQL("mssql"), MARIADB("maria");

    private String type;

    private DatabaseType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
