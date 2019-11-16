package democracy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import democracy.model.VisualElement;


public class ElementDAO {
	Connection conn;
	
	public ElementDAO()
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
	
	public boolean addImage(VisualElement element) throws Exception{
		try 
		{
			PreparedStatement ps = conn.prepareStatement("INSERT INTO ELEMENTS(CARDID, FACEID, BOUNDSID, CONTENT) VALUES(?,?,?,?);");
			ps.setInt(1, element.getCardId());
			ps.setInt(2, element.getFaceId());
			ps.setInt(3, element.getBoundId());
			ps.setString(4, element.getContent());
			ps.execute();
			
			return true;	
			
		}
		catch (Exception e)
		{
			throw new Exception("Failed to insert image: " + e.getMessage());
		}
	}
	
	public boolean addTextbox(VisualElement element) throws Exception{
		try 
		{
			PreparedStatement ps = conn.prepareStatement("INSERT INTO ELEMENTS(CARDID, FACEID, BOUNDSID, CONTENT, FONTID) VALUES(?,?,?,?,?);");
			ps.setInt(1, element.getCardId());
			ps.setInt(2, element.getFaceId());
			ps.setInt(3, element.getBoundId());
			ps.setString(4, element.getContent());
			ps.setInt(5, element.getFontId());
			ps.execute();
			
			return true;	
			
		}
		catch (Exception e)
		{
			throw new Exception("Failed to insert image: " + e.getMessage());
		}
	}
	
	
	
	public boolean deleteVisualElement(VisualElement element) throws Exception{
		try {
			
			// Delete the element first
			PreparedStatement ps = conn.prepareStatement("DELETE FROM ELEMENTS WHERE ELEMENTID = ?;");
			ps.setInt(1, element.getId());	
			int numAffectedElement = ps.executeUpdate();
			ps.close();
			
			// Delete the bound associated with the element
			PreparedStatement ps1 = conn.prepareStatement("DELETE FROM BOUNDS WHERE BOUNDSID = ?;");
			ps1.setInt(1, element.getBoundId());	
			int numAffectedBound = ps1.executeUpdate();
			ps.close();
			
			return (numAffectedElement == 1 && numAffectedBound == 1);
			
		}
		catch(Exception e) {
			throw new Exception("Failed to delete image: " + e.getMessage());
		}
	}
	
	public VisualElement getTextbox(int elementId) throws Exception
	{
		try
		{
			VisualElement element = null;
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM ELEMENTS WHERE ELEMENTID = ?;");
			ps.setInt(1, elementId);
			ResultSet resultSet = ps.executeQuery();
			
			while (resultSet.next())
			{
				element = generateTextbox(resultSet);
			}
			
			resultSet.close();
			ps.close();
			
			return element;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("Failed to retrieve textbox: " + e.getMessage());
		}
	}
	
	public VisualElement getImage(int elementId) throws Exception
	{
		try
		{
			VisualElement element = null;
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM ELEMENTS WHERE ELEMENTID = ?;");
			ps.setInt(1, elementId);
			ResultSet resultSet = ps.executeQuery();
			
			while (resultSet.next())
			{
				element = generateImage(resultSet);
			}
			
			resultSet.close();
			ps.close();
			
			return element;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("Failed to retrieve image: " + e.getMessage());
		}
	}
	
	
	
	private VisualElement generateTextbox(ResultSet resultSet) throws Exception{
		int id = resultSet.getInt("ELEMENTID");
		int cardId = resultSet.getInt("CARDID");
		int faceId = resultSet.getInt("FACEID");
		int boundsId = resultSet.getInt("BOUNDSID");
		String content = resultSet.getString("CONTENT");
		int fontId = resultSet.getInt("FONTID");
		
		return new VisualElement(id, cardId, faceId, boundsId, content, fontId);
		
	}
	
	private VisualElement generateImage(ResultSet resultSet) throws Exception{
		int id = resultSet.getInt("ELEMENTID");
		int cardId = resultSet.getInt("CARDID");
		int faceId = resultSet.getInt("FACEID");
		int boundsId = resultSet.getInt("BOUNDSID");
		String content = resultSet.getString("CONTENT");
		
		return new VisualElement(id, cardId, faceId, boundsId, content);
		
	}
	
	public List<VisualElement> getAllImages(int cardId) throws Exception{
		List<VisualElement> allimages = new ArrayList<VisualElement>();
		try 
		{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM ELEMENTS WHERE FONTID IS NULL AND CARDID = ?;");
			ps.setInt(1, cardId);
			ResultSet resultSet = ps.executeQuery();
			
			while (resultSet.next())
			{
				VisualElement element = generateImage(resultSet);
				allimages.add(element);
			}
			
			resultSet.close();
			ps.close();
			
			return allimages;
		}
		catch (Exception e)
		{
			throw new Exception("Failed to retrieve all images: " + e.getMessage());
		}
	}
	
	public List<VisualElement> getAllTextboxes(int cardId) throws Exception{
		List<VisualElement> alltextboxes = new ArrayList<VisualElement>();
		try 
		{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM ELEMENTS WHERE FONTID IS NOT NULL AND CARDID = ?;");
			ps.setInt(1, cardId);
			ResultSet resultSet = ps.executeQuery();
			
			while (resultSet.next())
			{
				VisualElement element = generateTextbox(resultSet);
				alltextboxes.add(element);
			}
			
			resultSet.close();
			ps.close();
			
			return alltextboxes;
		}
		catch (Exception e)
		{
			throw new Exception("Failed to retrieve all textboxes: " + e.getMessage());
		}
	}


}
