package uwu.smsgamer.senapi.utils.sql;

import uwu.smsgamer.senapi.utils.Pair;

import java.sql.*;
import java.util.*;

/**
 * An Implementation of {@link SenDB} for MySQL.
 */
public class MySQLDB extends AbstractDB {
    public String host;
    public int port;
    public String database;
    public String username;
    public String password;
    public String tablePrefix;
    public Connection con;

    public MySQLDB(String host, int port, String database, String username, String password, String tablePrefix) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.tablePrefix = tablePrefix;
    }

    @Override
    public void initialize(Pair<String, String>... rows) {
        connect();
        for (Pair<String, String> row : rows) {
            createTable(row.a, row.b);
        }
    }

    @Override
    public void connect() {
        if (!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Connection getConnection() {
        return con;
    }

    @Override
    public String tableName(String name) {
        return tablePrefix + name;
    }

    @Override
    public Table newTable(String name) {
        return new MySQLTable(this, name);
    }

    private static class MySQLTable extends AbstractTable {
        private MySQLTable(MySQLDB db, String name) {
            super(db, name);
        }
    }
}
