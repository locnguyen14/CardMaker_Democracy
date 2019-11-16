package democracy.http;

public class DeleteElementRequest  {
	public int elementId;
	
	public DeleteElementRequest(int elementId) {
		this.elementId = elementId;
	}
	
	public String toString() 
	{
		return "DeleteElement(" + Integer.toString(elementId)+ ")";
	}
	
}
