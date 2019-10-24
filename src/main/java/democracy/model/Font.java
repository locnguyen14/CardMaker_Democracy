package democracy.model;

public class Font
{
	private final int id;
	private final String name;
	private final String style;
	private final int size;
	
	public Font(int id, String name, String style, int size) 
	{
		this.id = id;
		this.name = name;
		this.style = style;
		this.size = size;
	}
	
	public int getId() 
	{
		return id;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public String getStyle() 
	{
		return style;
	}
	
	public int getSize() 
	{
		return size;
	}
		
}
