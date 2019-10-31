package democracy.http;

public class RequestResponse 
{
	public int statusCode;
	public String errorMessage;
	public ResponseField response;
	
	public RequestResponse(int httpCode, String errorMessage)
	{
		this.statusCode = httpCode;
		this.errorMessage = errorMessage;
		this.response = null;
	}
	
	public RequestResponse(int httpCode, ResponseField response)
	{
		this.statusCode = httpCode;
		this.response = response;
		this.errorMessage = null;
	}
}