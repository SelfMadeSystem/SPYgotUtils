package uwu.smsgamer.senapi.utils.sql;

import uwu.smsgamer.senapi.utils.Pair;

import java.sql.*;
import java.util.*;

/**
 * An Implementation of {@link SenDB} for SQLite.
 */
public class SQLiteDB extends AbstractDB {
    public String path;

    public SQLiteDB(String path) {
        this.path = path;
    }

    @Override
    public void connect() {
        if (!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:sqlite:" + path);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String tableName(String name) {
        return name;
    }

    @Override
    public Table newTable(String name) {
        return new SQLiteTable(this, name);
    }

    private static class SQLiteTable extends AbstractTable {
        private SQLiteTable(SQLiteDB db, String name) {
            super(db, name);
        }
        @Override
        public void truncate() {
            try {
                db.update("DELETE FROM " + getName() + ";");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
