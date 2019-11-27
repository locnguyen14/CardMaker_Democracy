package democracy.http;

public class ListImageResponse extends ResponseField {

	public String[] imageS3URL;
	
	public ListImageResponse(String[] imageS3URL) {
		this.imageS3URL = imageS3URL;
	}
}
