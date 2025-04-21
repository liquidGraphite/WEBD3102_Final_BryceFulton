package com.nscc.finance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLServerDBHelper {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=PersonalFinanceDB;encrypt=true;trustServerCertificate=true";
    private static final String USER = "BDFulton";
    private static final String PASSWORD = "Everest69";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLServer JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
