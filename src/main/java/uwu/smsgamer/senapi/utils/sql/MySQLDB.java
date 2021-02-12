package uwu.smsgamer.senapi.utils.sql;

import uwu.smsgamer.senapi.utils.Pair;

import java.sql.*;
import java.util.*;

/**
 * An Implementation of {@link SenDB} for MySQL.
 */
public class MySQLDB implements SenDB {
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
    public Table getTable(String name) {
        if (!tableExists(name)) System.out.println("Table doesn't exist. Might cause errors!");
        return new MySQLTable(this, name);
    }

    @Override
    public Table createTable(String name, String columns) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(
              "CREATE TABLE IF NOT EXISTS " + tablePrefix + name + " (" + columns + ");");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new MySQLTable(this, name);
    }

    @Override
    public boolean tableExists(String name) {
        try { //100% skidded from vagdedes but optimized a bit.
            Connection connection = getConnection();
            if (connection == null) return false;

            DatabaseMetaData metadata = connection.getMetaData();
            if (metadata == null) return false;

            ResultSet rs = metadata.getTables(null, null, tablePrefix + name, null);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static class MySQLTable implements Table {
        private final MySQLDB db;
        private final String name;

        private MySQLTable(MySQLDB db, String name) {
            this.db = db;
            this.name = name;
        }

        @Override
        public String getName() {
            return db.tablePrefix + name;
        }

        @Override
        public void delete() {
            try {
                db.update("DROP TABLE " + getName() + ";");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void truncate() {
            try {
                db.update("TRUNCATE TABLE " + getName() + ";");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int countRows() {
            try {
                ResultSet rs = db.query("SELECT * FROM " + getName());
                int i = 0;
                while (rs.next()) i++;
                return i;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        public boolean exists(String... params) {
            if (params.length == 0) return false;
            String condition = Table.getCondition(params);
            try {
                ResultSet rs = db.query("SELECT * FROM " + getName() + " WHERE " + condition + ";");
                return rs.next();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return false;
        }

        @Override
        public void add(String columns, String values) {
            try {
                db.update("INSERT INTO " + getName() + " (" + columns + ") VALUES (" + values + ");");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void removeFromTable(String[] where) {
            if (where.length == 0) return;
            String condition = Table.getCondition(where);
            try {
                db.update("DELETE FROM " + getName() + " WHERE " + condition + ";");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void upsert(String selected, Object object, String column, String checkData) {
            if (object != null) object = "'" + object + "'";

            if (checkData != null) checkData = "'" + checkData + "'";

            try {
                ResultSet rs = db.query("SELECT * FROM " + getName() + " WHERE " + column + "=" + checkData + ";");
                if (rs.next()) {
                    db.update("UPDATE " + getName() + " SET " + selected + "=" + object + " WHERE " + column + "=" + checkData + ";");
                } else {
                    add(column + ", " + selected, "'" + checkData + "', '" + object + "'");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void set(String selected, Object object, String[] where) {
            if (where.length == 0) return;
            if (object != null) object = "'" + object + "'";
            String condition = Table.getCondition(where);
            try {
                db.update("UPDATE " + getName() + " SET " + selected + "=" + object + " WHERE " + condition + ";");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Object get(String selected, String[] where) {
            if (where.length == 0) return null;
            String condition = Table.getCondition(where);
            try {
                ResultSet rs = db.query("SELECT * FROM " + getName() + " WHERE " + condition + ";");
                if (rs.next()) return rs.getObject(selected);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public List<?> getList(String selected, String[] where) {
            if (where.length == 0) return null;
            String condition = Table.getCondition(where);
            try {
                ResultSet rs = db.query("SELECT * FROM " + getName() + " WHERE " + condition + ";");
                List<Object> list = new ArrayList<>();
                while (rs.next()) list.add(rs.getObject(selected));
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public List<?> getAll(String selected) {
            try {
                ResultSet rs = db.query("SELECT * FROM " + getName() + ";");
                List<Object> list = new ArrayList<>();
                while (rs.next()) list.add(rs.getObject(selected));
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> Map<String, List<T>> getAll() {
            try {
                ResultSet rs = db.query("SELECT * FROM " + getName() + ";");
                ResultSetMetaData meta = rs.getMetaData();
                int cCount = meta.getColumnCount();
                Map<String, List<T>> map = new LinkedHashMap<>();
                while (rs.next()) {
                    for (int i = 1; i <= cCount; i++) {
                        String name = meta.getColumnName(i);
                        if (map.containsKey(name)) map.get(name).add((T) rs.getObject(i));
                        else map.put(name, new ArrayList<>(Collections.singleton((T) rs.getObject(i))));
                    }
                }
                return map;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
