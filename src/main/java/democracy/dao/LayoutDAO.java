package democracy.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import democracy.model.Layout;

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
	
	public List<Layout> getAllLayouts() throws Exception
	{
		List<Layout> allLayouts = new ArrayList<Layout>();
		try
		{
			Statement statement = conn.createStatement();
			String query = "SELECT * FROM LAYOUTS";
			ResultSet resultSet = statement.executeQuery(query);
			
			while(resultSet.next())
			{
				Layout layout = generateLayout(resultSet);
				allLayouts.add(layout);
			}
			
			return allLayouts;
		} 
		catch (Exception e)
		{
			throw new Exception("Failed to retrieve all layouts: " + e.getMessage());
		}
	}
	
	private Layout generateLayout(ResultSet resultSet) throws Exception
	{
		int id = resultSet.getInt("LAYOUTID");
		String layout = resultSet.getString("LAYOUTNAME");
		return new Layout(id, layout);
	}
}
