package democracy.http;

public class DuplicateCardRequest {
	public String cardId;
	public String recipient;
	
	public DuplicateCardRequest(String cardId, String recipient) {
		this.cardId = cardId;
		this.recipient = recipient;
	}
}
