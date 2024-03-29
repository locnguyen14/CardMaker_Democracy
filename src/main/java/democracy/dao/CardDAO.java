package democracy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import democracy.model.Card;
import democracy.model.VisualElement;

public class CardDAO 
{
	Connection conn;
	
	public CardDAO()
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
	
	public int addCard(Card card) throws Exception
	{
		try
		{
			PreparedStatement ps = conn.prepareStatement("INSERT INTO CARDS (EVENTTYPEID, RECIPIENT_NAME, LAYOUTID) VALUES (?,?,?);", PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setInt(1, card.getEventId());
			ps.setString(2, card.getRecipientName());
			ps.setInt(3, card.getLayoutId());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
			return -1;
		}
		catch (Exception e)
		{
			throw new Exception("Failed to insert card: " + e.getMessage());
		}
	}
	
	public boolean deleteCard(Card card) throws Exception
	{
		try 
		{
			ElementDAO dao = new ElementDAO();
			List<VisualElement> images = dao.getAllImages(card.getId());
			List<VisualElement> textboxes = dao.getAllTextboxes(card.getId());
			List<Integer> boundsIds = new ArrayList<Integer>();
			
			// Delete all the image
			for (VisualElement image : images) 
			{
				if (dao.deleteVisualElement(image)) 
				{
					continue;
				}
			}
			
			// Delete all the text boxes
			for (VisualElement textbox : textboxes) 
			{
				if (dao.deleteVisualElement(textbox)) 
				{
					continue;
				}
			}
			
			// Delete the card
			PreparedStatement ps = conn.prepareStatement("DELETE FROM CARDS WHERE CARDID = ?;");
			ps.setInt(1, card.getId());	
			int numAffected = ps.executeUpdate();
			ps.close();
			
			return (numAffected == 1);
		}
		catch (Exception e)
		{
			throw new Exception("Failed to delete card: " + e.getMessage());
		}
	}
	
	public Card getCard(int cardId) throws Exception
	{
		try
		{
			Card card = null;
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM CARDS WHERE CARDID = ?;");
			ps.setInt(1, cardId);
			ResultSet resultSet = ps.executeQuery();
			
			while (resultSet.next())
			{
				card = generateCard(resultSet);
			}
			
			resultSet.close();
			ps.close();
			
			return card;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("Failed to retrieve card: " + e.getMessage());
		}
	}
	
	public List<Card> getAllCards() throws Exception
	{
		List<Card> allCards = new ArrayList<Card>();
		try 
		{
			Statement statement = conn.createStatement();
			String query = "SELECT * FROM CARDS";
			ResultSet resultSet = statement.executeQuery(query);
			
			while (resultSet.next())
			{
				Card card = generateCard(resultSet);
				allCards.add(card);
			}
			
			resultSet.close();
			statement.close();
			
			return allCards;
		}
		catch (Exception e)
		{
			throw new Exception("Failed to retrieve all cards: " + e.getMessage());
		}
	}
	
	private Card generateCard(ResultSet resultSet) throws Exception
	{
		int id = resultSet.getInt("CARDID");
		int eventId = resultSet.getInt("EVENTTYPEID");
		String recipientName = resultSet.getString("RECIPIENT_NAME");
		int layoutId = resultSet.getInt("LAYOUTID");
		
		return new Card(id, eventId, recipientName, layoutId);
	}
}
