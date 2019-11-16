package democracy.http;


public class NewTextBoxRequest {
	public String cardId;
	public String faceId;
	public String text;
	public String fontId;
	public String x;
	public String y;
	public String width;
	public String height;
	
	public NewTextBoxRequest(String cardId, String faceId, String text, String fontId, String x, String y, String width, String height) {
		this.cardId = cardId;
		this.faceId = faceId;
		this.text = text;
		this.fontId = fontId;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}
