package democracy.http;

import democracy.model.VisualElement;

public class ListImageResponse extends ResponseField {
	
	public VisualElement [] images;
	
	public ListImageResponse(VisualElement [] images) {
		this.images = images;
	}
}
