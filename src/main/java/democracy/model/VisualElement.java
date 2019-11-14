package democracy.model;

public class VisualElement 
{
	private final int id;
	private final int cardId;
	private final int faceId;
	private final int boundId;
	private final String content;
	private int fontId;
	
	public VisualElement(int id, int cardId, int faceId, int boundId, String text, int fontId)
	{
		this.id = id;
		this.cardId = cardId;
		this.faceId = faceId;
		this.boundId = boundId;
		this.content = text;
		this.fontId = fontId;
	}
	
	public VisualElement(int id, int cardId, int faceId, int boundId, String image)
	{
		this.id = id;
		this.cardId = cardId;
		this.faceId = faceId;
		this.boundId = boundId;
		this.content = image;
	}

	public int getId() 
	{
		return id;
	}

	public int getCardId() 
	{
		return cardId;
	}

	public int getFaceId() 
	{
		return faceId;
	}

	public int getBoundId() 
	{
		return boundId;
	}

	public String getContent() 
	{
		return content;
	}

	public int getFontId() 
	{
		return fontId;
	}
}
