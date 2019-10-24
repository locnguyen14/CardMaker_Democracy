package democracy.model;

public class VisualElement 
{
	private final int id;
	private final int cardId;
	private final int faceId;
	private final Bounds bounds;
	private final String content;
	private final int fontId;
	
	public VisualElement(int id, int cardId, int faceId, Bounds bounds, String content, int fontId)
	{
		this.id = id;
		this.cardId = cardId;
		this.faceId = faceId;
		this.bounds = bounds;
		this.content = content;
		this.fontId = fontId;
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

	public Bounds getBounds() 
	{
		return bounds;
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
