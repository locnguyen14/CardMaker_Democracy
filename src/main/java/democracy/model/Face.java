package democracy.model;

public class Face {
	
	public final int id;
	public final String faceName;

	
	public Face(String faceName, int id) {
		this.faceName = faceName;
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getFaceName() {
		return faceName;
	}
	
}
