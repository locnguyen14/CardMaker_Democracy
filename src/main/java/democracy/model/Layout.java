package democracy.model;

public class Layout 
{
	private final int id;
	private final String layout;
	
	public Layout(int id, String layout)
	{
		this.id = id;
		this.layout = layout;
	}

	public int getId() 
	{
		return id;
	}

	public String getLayout() 
	{
		return layout;
	}
}