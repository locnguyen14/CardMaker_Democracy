package democracy.http;

public class CreateCardRequest 
{
	public String recipientName;
	public int eventId;
	
	public CreateCardRequest(String recipientName, int eventId)
	{
		this.recipientName = recipientName;
		this.eventId = eventId;
	}
	
	public String toString()
	{
		return "CreateCard(" + recipientName + "," + eventId + ")";
	}
}
