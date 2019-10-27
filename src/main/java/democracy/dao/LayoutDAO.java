package democracy.dao;

import java.sql.Connection;

public class LayoutDAO 
{
	Connection conn;
	
	public LayoutDAO()
	{
		try 
		{
			conn = DatabaseUtil.connect();
		} 
		catch (Exception e) 
		{
			conn = null;
		}
	}
}
