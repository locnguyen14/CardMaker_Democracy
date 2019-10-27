package democracy.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import democracy.model.Face;

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
	
	public List<Face> getAllFaces() throws Exception
	{
		List<Face> allFaces = new ArrayList<Face>();
		try
		{
			Statement statement = conn.createStatement();
			String query = "SELECT * FROM FACES";
			ResultSet resultSet = statement.executeQuery(query);
			
			while(resultSet.next())
			{
				Face face = generateFace(resultSet);
				allFaces.add(face);
			}
			
			return allFaces;
		} 
		catch (Exception e)
		{
			throw new Exception("Failed to retrieve all faces: " + e.getMessage());
		}
	}
	
	private Face generateFace(ResultSet resultSet) throws Exception
	{
		String faceName = resultSet.getString("FACENAME");
		int faceId = resultSet.getInt("FACEID");
		return new Face(faceName, faceId);
	}
}
