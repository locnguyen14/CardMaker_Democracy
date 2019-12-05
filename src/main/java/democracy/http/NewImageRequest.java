package democracy.http;

public class NewImageRequest {
	public String cardId;
	public String faceId;
	public String image;
	public String x;
	public String y;
	public String width;
	public String height;
	public String image_name;
	
	public NewImageRequest(String cardId, String faceId, String image, String x, String y, String width, String height, String image_name) {
		this.cardId = cardId;
		this.faceId = faceId;
		this.image = image;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.image_name = image_name;
	}
}
