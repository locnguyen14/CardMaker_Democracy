package democracy.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseUtil 
{
	public final static String rdsMySqlDatabaseUrl = "democracydb.cqohivx9j5sq.us-east-1.rds.amazonaws.com";
	public final static String dbUsername = "calcAdmin";
	public final static String dbPassword = "calc:pass";
		
	public final static String jdbcTag = "jdbc:mysql://";
	public final static String rdsMySqlDatabasePort = "3306";
	public final static String multiQueries = "?allowMultiQueries=true";
	   
	public final static String dbName = "innodb";

	// variable shared across all usages.
	static Connection conn;
 
	protected static Connection connect() throws Exception
	{
		if (conn != null) { return conn; }
		
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
