package democracy.http;

public class CreateCardRequest 
{
	String recipientName;
	int eventId;
	
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
