package democracy.http;

public class RequestResponse 
{
	public int httpCode;
	public String errorMessage;
	public ResponseField response;
	
	public RequestResponse(int httpCode, String errorMessage)
	{
		this.httpCode = httpCode;
		this.errorMessage = errorMessage;
		this.response = null;
	}
	
	public RequestResponse(int httpCode, ResponseField response)
	{
		this.httpCode = httpCode;
		this.response = response;
		this.errorMessage = null;
	}
}
