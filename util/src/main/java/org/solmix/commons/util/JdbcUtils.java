package org.solmix.commons.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JdbcUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(JdbcUtils.class);


    /**
     * Lutris InstantDB JDBC driver class name.
     */
    public static final String INSTANTDB_NAME   = "idb";
    public static final int    INSTANTDB_TYPE   = 2;

    /**
     * Oracle JDBC driver class name.
     */
    public static final String ORACLE_NAME      = "oracle";

    /**
     * PostgreSQL JDBC driver class name.
     */
    public static final String PGSQL_NAME       = "postgres";

    /**
     * Oracle JDBC driver class name.
     */
    public static final String ORACLE_THIN_NAME = "oracle-thin";


    /**
     * MySQL JDBC driver class name.
     */
    public static final String MYSQL_NAME       = "mysql";

    private static HashMap driverClasses;
    static {
        // Map the driver names and types to names
        driverClasses = new HashMap();
        driverClasses.put(INSTANTDB_NAME,  "com.lutris.instantdb.jdbc.idbDriver");
        driverClasses.put(ORACLE_NAME,     "oracle.jdbc.driver.OracleDriver");
        driverClasses.put(PGSQL_NAME,      "org.postgresql.Driver");
        driverClasses.put(ORACLE_THIN_NAME,"oracle.jdbc.driver.OracleDriver");
        driverClasses.put(MYSQL_NAME,      "com.mysql.jdbc.Driver");
        
        
    }
    
    /**
     * Loads the JDBC driver from the driver short name (e.g., oracle) or the
     * driver full name (e.g., oracle.jdbc.driver.OracleDriver).
     *
     * @param driver
     *      The short or full name of the JDBC driver.
     *
     * throws ClassNotFoundException
     *      If the driver name does not specify a valid driver.
     */
    public static void loadDriver(String database) throws ClassNotFoundException
    {
        if(database == null)
            throw new IllegalArgumentException("Parameter \'driver\' cannot be null.");
        String driverClassName = JdbcUtils.getDriverString(database);
        Class.forName(driverClassName);
    }
    
    /**
     * Retrieves the full JDBC Driver String from the database name. The
     * database name can be a short name like cloudscape or a full jdbc
     * connection string like jdbc:cloudscape:.
     *
     * @param database
     *      The database name.
     *
     * @return String
     *      The full JDBC Driver String (e.g., "COM.cloudscape.core.JDBCDriver")
     *      that corresponds the the short driver name.
     */
    public static String getDriverString(String database)
    {
        if(database == null)
            throw new IllegalArgumentException("Parameter \'database\' cannot be null.");

        
        for (Iterator it = driverClasses.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            if (database.indexOf(name) != -1) {
                return (String) entry.getValue();
            }
        }

        return database;
    }
    /**
     * Prints a SQLException including the error code and child exceptions to
     * System.out .
     *
     * @param e
     *      The SQLException object.
     *
     * @see java.sql.SQLException
     */
    public static void printSQLException(SQLException e)
    {
        if(e == null)
            throw new IllegalArgumentException("Parameter cannot be null.");

        JdbcUtils.printSQLException(e, new PrintWriter(System.out, true));
    }

    /**
     * Prints a SQLException including the error code and child exceptions to
     * System.out .
     *
     * @param e
     *      The SQLException object.
     * @param out
     *      The java.io.PrintWriter to print the to.
     *
     * @see java.io.PrintWriter
     * @see java.sql.SQLException
     */
    public static void printSQLException(SQLException e, PrintWriter out)
    {
        if(e == null || out == null)
            throw new IllegalArgumentException("Parameter cannot be null.");

        do
        {
            out.println("Error: " + e.getErrorCode() + ": " + e);
        } while((e = e.getNextException()) != null);
    }
    
    public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                LOG.debug("Could not close JDBC Connection", ex);
            } catch (Throwable ex) {
                // We don't trust the JDBC driver: It might throw RuntimeException or Error.
                LOG.debug("Unexpected exception on closing JDBC Connection", ex);
            }
        }
    }

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                LOG.trace("Could not close JDBC Statement", ex);
            } catch (Throwable ex) {
                // We don't trust the JDBC driver: It might throw RuntimeException or Error.
                LOG.trace("Unexpected exception on closing JDBC Statement", ex);
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                LOG.trace("Could not close JDBC ResultSet", ex);
            } catch (Throwable ex) {
                // We don't trust the JDBC driver: It might throw RuntimeException or Error.
                LOG.trace("Unexpected exception on closing JDBC ResultSet", ex);
            }
        }
    }

    
}
