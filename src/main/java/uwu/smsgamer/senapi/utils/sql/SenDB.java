package uwu.smsgamer.senapi.utils.sql;

import uwu.smsgamer.senapi.utils.Pair;

import java.sql.*;
import java.util.*;

/**
 * A database interface. Thank you Vagdedes a *lot* for helping me figure out the methods I wanted to use.
 */
public interface SenDB {
    void initialize(Pair<String, String>... rows);

    /**
     * Connects to the database.
     */
    void connect();

    /**
     * Disconnects from the database.
     */
    default void disconnect() {
        if (!isConnected()) {
            try {
                getConnection().close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /**
     * Should be called before every action. If we are no longer connected to the database, then we connect again.
     */
    default void testConnection() {
        if (!isConnected()) connect();
    }

    /**
     * Gets the connection to the database.
     *
     * @return The connection to the database.
     */
    Connection getConnection();

    /**
     * Gets if we are connected to the database.
     *
     * @return If we are connected to the database.
     */
    default boolean isConnected() {
        try {
            return getConnection() != null && !getConnection().isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets a table.
     *
     * @param name The name of the table.
     * @return The table.
     */
    Table getTable(String name);

    /**
     * Creates a new table.
     *
     * @param name The name of the table.
     * @param columns The columns the table should have.
     * @return The table that you have created.
     */
    Table createTable(String name, String columns);

    /**
     * Returns if a table exists.
     *
     * @param name The name of the table.
     * @return if a table exists.
     */
    boolean tableExists(String name);

    /**
     * Runs an update statement.
     *
     * @param update The command to do.
     * @throws SQLException if an SQL error occurred.
     */
    default void update(String update) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(update);
        ps.executeUpdate();
    }

    /**
     * Runs an query statement.
     *
     * @param query The command to do.
     * @return The result of the query.
     * @throws SQLException if an SQL error occurred.
     */
    default ResultSet query(String query) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(query);
        return ps.executeQuery();
    }

    /**
     * A table interface that all tables should implement.
     * Please, someone, tell me how tf types work bc OMG IT'S SHITTY IN JAVA
     */
    interface Table {
        /**
         * Gets the name of the table.
         *
         * @return The name of the table.
         */
        String getName();

        /**
         * Deletes the table from the database.
         */
        void delete();

        /**
         * Truncates/clears the table. Removes all elements from the table.
         */
        void truncate(); // Clears

        /**
         * Counts the number of rows in the table.
         *
         * @return The number of rows in the table.
         */
        int countRows();

        /**
         * Checks if a value in a column exists.
         *
         * @param column The name of the column.
         * @param checkValue The value to check for.
         * @return If a value in a column exists.
         */
        default boolean exists(String column, String checkValue) {
            return exists(column + "=" + checkValue);
        }

        /**
         * Checks if a row with said params exists.
         *
         * @param params Parameters
         * @return If a value in a column exists.
         */
        boolean exists(String... params);

        /**
         * Adds a row to the table.
         *
         * @param columns The columns of the table.
         * @param values The values of the row to add.
         */
        void add(String columns, String values);

        /**
         * Removes a row from the table.
         *
         * @param column The column to check for.
         * @param operand The operand to use when checking.
         * @param checkData The data to check for.
         */
        default void removeFromTable(String column, String operand, String checkData) {
            removeFromTable(column + operand + checkData);
        }

        /**
         * Removes a row from the table.
         *
         * @param where The "where" arguments.
         */
        void removeFromTable(String... where);

        /**
         * TODO: Doc this lol. I still don't understand what this does but I feel it's important.
         *
         * @param selected a
         * @param object a
         * @param column a
         * @param checkData a
         */
        void upsert(String selected, Object object, String column, String checkData);

        /**
         * Sets a value in the table.
         *
         * @param selected The select column to set.
         * @param object The object to set it to.
         * @param column The column to check for.
         * @param operand The operand to use when checking.
         * @param checkData The data to check for.
         */
        default void set(String selected, Object object, String column, String operand, String checkData) {
            set(selected, object, column + operand + checkData);
        }

        /**
         * Sets a value in the table.
         *
         * @param selected The select column to set.
         * @param object The object to set it to.
         * @param where The condition to set it to.
         */
        void set(String selected, Object object, String... where);

        /**
         * Gets a value from the table.
         *
         * @param selected The selected column.
         * @param column The column to check for.
         * @param operand The operand to use when checking.
         * @param checkData The data to check for.
         * @return A value from the table.
         */
        default Object get(String selected, String column, String operand, String checkData) {
            return get(selected, column + operand + checkData);
        }

        /**
         * Gets a value from the table.
         *
         * @param selected The selected column.
         * @param where The conditions to get the value.
         * @return A value from the table.
         */
        Object get(String selected, String... where);

        /**
         * Gets all the values from the table that satisfies a condition.
         *
         * @param selected The selected column.
         * @param column The column to check for.
         * @param operand The operand to use when checking.
         * @param checkData The data to check for.
         * @return All the values from the table that satisfies a condition.
         */
        default List<?> getList(String selected, String column, String operand, String checkData) {
            return getList(selected, column + operand + checkData);
        }

        /**
         * Gets all the values from the table that satisfies a condition.
         *
         * @param selected The selected column.
         * @param where The conditions to check for.
         * @return All the values from the table that satisfies a condition.
         */
        List<?> getList(String selected, String... where);

        /**
         * Gets all the values from a row in the table.
         *
         * @param selected The selected column.
         * @return All the values from a row in the table.
         */
        List<?> getAll(String selected);

        /**
         * Gets all the values from all the rows in the table.
         *
         * @return All the values from all the rows in the table.
         */
        <T> Map<String, List<T>> getAll();

        /**
         * For internal use. Returns the array joined by {@code " AND "}.
         *
         * @param where The array to use.
         * @return The array joined by {@code " AND "}.
         */
        static String getCondition(String... where) {
            return String.join(" AND ", where);
        }
    }
}
