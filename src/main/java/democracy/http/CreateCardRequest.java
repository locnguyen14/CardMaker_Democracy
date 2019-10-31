package democracy.http;

public class CreateCardRequest 
{
	public String recipientName;
	public String eventId;
	public String layoutId;
	
	public CreateCardRequest(String recipientName, String eventId, String layoutId)
	{
		this.recipientName = recipientName;
		this.eventId = eventId;
		this.layoutId = layoutId;
	}
	
	public String toString()
	{
		return "CreateCard(" + recipientName + "," + eventId + ")";
	}
}
