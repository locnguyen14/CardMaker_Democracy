package democracy.http;

public class EditTextBoxRequest {

	public String cardId;
	public String elementId;
	public String text;
	public String fontId;
	public String x;
	public String y;
	public String width;
	public String height;
	
	public EditTextBoxRequest(String cardId, String elementId, String text, String fontId, String x, String y, String width, String height) {
		this.cardId = cardId;
		this.elementId = elementId;
		this.text = text;
		this.fontId = fontId;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}
