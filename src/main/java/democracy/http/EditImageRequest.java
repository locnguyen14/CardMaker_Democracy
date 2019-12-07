package democracy.http;

public class EditImageRequest {

	public String cardId;
	public String elementId;
	public String x;
	public String y;
	public String width;
	public String height;
	
	public EditImageRequest(String cardId, String elementId, String x, String y, String width, String height) {
		this.cardId = cardId; 
		this.elementId = elementId;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}
