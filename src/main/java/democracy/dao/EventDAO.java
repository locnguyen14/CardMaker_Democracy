package democracy.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import democracy.model.Event;

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
			System.out.println("Connection failed");
		}
	}
	
	public List<Event> getAllEvents() throws Exception
	{
		List<Event> allEvents = new ArrayList<Event>();
		try
		{
			Statement statement = conn.createStatement();
			String query = "SELECT * FROM CARDEVENTS";
			ResultSet resultSet = statement.executeQuery(query);
			
			while(resultSet.next())
			{
				Event event = generateEvent(resultSet);
				allEvents.add(event);
			}
			
			return allEvents;
		} 
		catch (Exception e)
		{
			throw new Exception("Failed to retrieve all events: " + e.getMessage());
		}
	}
	
	private Event generateEvent(ResultSet resultSet) throws Exception
	{
		int id = resultSet.getInt("EVENTID");
		String eventName = resultSet.getString("EVENTNAME");
		return new Event(id, eventName);
	}
}
