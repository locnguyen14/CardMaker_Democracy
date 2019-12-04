package democracy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import democracy.model.Card;
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
	
	public Layout getLayoutById(int layoutId) throws Exception
	{
		try
		{
			Layout layout = null;
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM LAYOUTS WHERE LAYOUTID = ?;");
			ps.setInt(1, layoutId);
			ResultSet resultSet = ps.executeQuery();
			
			while (resultSet.next())
			{
				layout = generateLayout(resultSet);
			}
			
			resultSet.close();
			ps.close();
			
			return layout;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("Failed to retrieve layout: " + e.getMessage());
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
