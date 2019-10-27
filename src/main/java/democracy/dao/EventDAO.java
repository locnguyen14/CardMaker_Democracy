package democracy.dao;

import java.sql.Connection;

public class EventDAO 
{
	Connection conn;
	
	public EventDAO()
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
