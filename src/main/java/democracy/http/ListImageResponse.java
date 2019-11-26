package democracy.http;

public class ListImageResponse extends ResponseField {

	public String[] imageS3URL;
	
	public ListImageResponse(String[] imageBase64URL) {
		this.imageS3URL = imageS3URL;
	}
}
