package democracy.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import democracy.model.Font;

public class FontDAO {
	
Connection conn;
	
	public FontDAO()
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
	
	public List<Font> getAllFonts() throws Exception
	{
		List<Font> allFonts = new ArrayList<Font>();
		try
		{
			Statement statement = conn.createStatement();
			String query = "SELECT * FROM FONT";
			ResultSet resultSet = statement.executeQuery(query);
			
			while(resultSet.next())
			{
				Font font = generateFont(resultSet);
				allFonts.add(font);
			}
			
			return allFonts;
		} 
		catch (Exception e)
		{
			throw new Exception("Failed to retrieve all fonts: " + e.getMessage());
		}
	}
	
	private Font generateFont(ResultSet resultSet) throws Exception
	{
		String fontName = resultSet.getString("FONTNAME");
		int fontId = resultSet.getInt("FONTID");
		int size = resultSet.getInt("SIZE");
		String style = resultSet.getString("STYLE");
		
		return new Font(fontId, fontName, style, size);
	}

}
