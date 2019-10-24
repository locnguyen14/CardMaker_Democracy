package democracy.model;

public class Event 
{
	private int id;
	private String name;
	
	public Event(int id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public int getId() 
	{
		return id;
	}

	public String getName() 
	{
		return name;
	}
}
