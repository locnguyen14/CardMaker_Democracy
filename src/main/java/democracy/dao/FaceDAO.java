package democracy.dao;

import java.sql.Connection;

public class FaceDAO 
{
	Connection conn;
	
	public FaceDAO()
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
