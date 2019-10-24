package democracy.model;

public class Card 
{
	private final int id;
	private final int eventId;
	private final String recipientName;
	private final int layoutId;
	
	public Card(int id, int eventId, String recipientName, int layoutId) 
	{
		this.id = id;
		this.eventId = eventId;
		this.recipientName = recipientName;
		this.layoutId = layoutId;
	}

	public int getId() 
	{
		return id;
	}

	public int getEventId() 
	{
		return eventId;
	}

	public String getRecipientName() 
	{
		return recipientName;
	}

	public int getLayoutId() 
	{
		return layoutId;
	}
}
