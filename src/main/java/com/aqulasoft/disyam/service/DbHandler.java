package com.aqulasoft.disyam.service;

import org.sqlite.JDBC;

import java.sql.*;
import java.util.*;

public class DbHandler {

    private static final String CON_STR = "jdbc:sqlite:C:/Projects/DisYam/settings.db";
    private static DbHandler instance = null;

    public static synchronized DbHandler getInstance() throws SQLException {
        if (instance == null)
            instance = new DbHandler();
        return instance;
    }

    private Connection connection;

    private DbHandler() throws SQLException {
        DriverManager.registerDriver(new JDBC());
        this.connection = DriverManager.getConnection(CON_STR);
    }

}
