package democracy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import democracy.model.Bounds;

public class BoundDAO {
	Connection conn;
	
	public BoundDAO()
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
	
	public int addBound(Bounds bounds) throws Exception{
		try 
		{
			PreparedStatement ps = conn.prepareStatement("INSERT INTO BOUNDS(X, Y, WIDTH, HEIGHT) VALUES(?,?,?,?);", PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setInt(1, bounds.getX());
			ps.setInt(2, bounds.getY());
			ps.setInt(3, bounds.getWidth());
			ps.setInt(4, bounds.getHeight());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
			return -1;
		}
		catch (Exception e)
		{
			throw new Exception("Failed to insert bounds: " + e.getMessage());
		}
	}
	
	public boolean deleteBounds(Bounds bounds) throws Exception{
		try {
			PreparedStatement ps = conn.prepareStatement("DELETE FROM BOUNDS WHERE BOUNDSID = ?;");
			ps.setInt(1, bounds.getId());	
			int numAffected = ps.executeUpdate();
			ps.close();
			
			return (numAffected == 1);
			
		}
		catch(Exception e) {
			throw new Exception("Failed to delete bounds: " + e.getMessage());
		}
	}
	
	public boolean deleteBoundsbyId(int boundId) throws Exception{
		try {
			PreparedStatement ps = conn.prepareStatement("DELETE FROM BOUNDS WHERE BOUNDSID = ?;");
			ps.setInt(1, boundId);	
			int numAffected = ps.executeUpdate();
			ps.close();
			
			return (numAffected == 1);
			
		}
		catch(Exception e) {
			throw new Exception("Failed to delete bounds: " + e.getMessage());
		}
	}
	
	public Bounds getBounds(int boundsId) throws Exception
	{
		try
		{
			Bounds bounds = null;
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM BOUNDS WHERE BOUNDSID = ?;");
			ps.setInt(1, boundsId);
			ResultSet resultSet = ps.executeQuery();
			
			while (resultSet.next())
			{
				bounds = generateBounds(resultSet);
			}
			
			resultSet.close();
			ps.close();
			
			return bounds;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("Failed to retrieve bounds: " + e.getMessage());
		}
	}
	
	private Bounds generateBounds(ResultSet resultSet) throws Exception{
		int id = resultSet.getInt("BOUNDSID");
		int x = resultSet.getInt("X");
		int y = resultSet.getInt("Y");
		int width = resultSet.getInt("WIDTH");
		int height = resultSet.getInt("HEIGHT");
		
		return new Bounds(id, x, y, width, height);
		
	}

	public boolean updateBound(Bounds bounds) throws Exception 
	{
		try 
		{
			PreparedStatement ps = conn.prepareStatement("UPDATE BOUNDS SET X = ?, Y = ?, WIDTH = ?, HEIGHT = ? WHERE BOUNDSID = ?;");
			ps.setInt(1, bounds.getX());
			ps.setInt(2, bounds.getY());
			ps.setInt(3, bounds.getWidth());
			ps.setInt(4, bounds.getHeight());
			ps.setInt(5, bounds.getId());
			
			int numAffected = ps.executeUpdate();
			ps.close();
			
			return (numAffected == 1 || numAffected == 0);
		}
		catch (Exception e) 
		{
			throw new Exception("Failed to update bounds: " + e.getMessage());
		}
	}

}
