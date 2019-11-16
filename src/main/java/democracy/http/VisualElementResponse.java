package democracy.http;

import democracy.model.Bounds;
import democracy.model.Face;
import democracy.model.Font;
import democracy.model.Layout;
import democracy.model.VisualElement;

public class VisualElementResponse extends ResponseField{
	public Face[] faces;
	public VisualElement[] images;
	public VisualElement[] textboxes;
	public Font[] fonts;
	public Bounds[] bounds;
	public Layout[] layouts;
	public int layoutId;
	
	public VisualElementResponse(int layoutId, Face[] faces, VisualElement[] images, VisualElement[] textboxes, Font[] fonts, Bounds[] bounds, Layout[] layouts) {
		this.layoutId = layoutId;
		this.faces = faces;
		this.images =images;
		this.textboxes = textboxes;
		this.fonts = fonts;
		this.bounds = bounds;
		this.layouts = layouts;
	}
}
