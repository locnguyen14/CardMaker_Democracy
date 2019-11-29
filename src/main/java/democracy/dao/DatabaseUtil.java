package democracy.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseUtil 
{
	public final static String rdsMySqlDatabaseUrl = "democracydb.cqohivx9j5sq.us-east-1.rds.amazonaws.com";
	public final static String dbUsername = "democracyadmin";
	public final static String dbPassword = "democracypassword";
		
	public final static String jdbcTag = "jdbc:mysql://";
	public final static String rdsMySqlDatabasePort = "3306";
	public final static String multiQueries = "?allowMultiQueries=true";
	   
	public final static String dbName = "innodb";

	// variable shared across all usages.
	static Connection conn;
 
	protected static Connection connect() throws Exception
	{
		if (conn != null) { return conn; }
		
		String dbName = "innodb";
		
		// Get an alternative database name if performing JUnit testing
		String testing = System.getenv("TESTING");
		if (testing != null && testing.equals("1")) 
		{ 
			System.out.println("Using test database.");
			dbName = "test_innodb"; 
		}
		
		try 
		{
			// Connect to database using predefined values
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					jdbcTag + rdsMySqlDatabaseUrl + ":" + rdsMySqlDatabasePort + "/" + dbName + multiQueries,
					dbUsername,
					dbPassword);
			return conn;
		} 
		catch (Exception ex) 
		{
			throw new Exception("Failed in database connection");
		}
	}
}
