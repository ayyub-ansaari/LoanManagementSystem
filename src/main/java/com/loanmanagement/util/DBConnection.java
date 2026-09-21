package com.loanmanagement.util;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {

    private static final String PROPERTIES_FILE = "db.properties";
    private static final Properties PROPS = new Properties();

    static {
        try(InputStream in = DBConnection.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)){
            if(in == null){
                throw new IllegalArgumentException(PROPERTIES_FILE + "not found on classpath ");
            }
            PROPS.load(in);
        }catch (IOException e) {
            throw new IllegalStateException("Failed to load" + PROPERTIES_FILE,e);
        }
    }
    private DBConnection(){
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(PROPS.getProperty("db.url"),
                PROPS.getProperty("db.user"),
                PROPS.getProperty("db.password"));

    }

}