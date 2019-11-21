package democracy.http;

import java.util.ArrayList;
import java.util.List;

import democracy.dao.BoundDAO;
import democracy.dao.CardDAO;
import democracy.dao.ElementDAO;
import democracy.dao.EventDAO;
import democracy.dao.FaceDAO;
import democracy.dao.FontDAO;
import democracy.dao.LayoutDAO;
import democracy.model.Bounds;
import democracy.model.Card;
import democracy.model.Event;
import democracy.model.Face;
import democracy.model.Font;
import democracy.model.Layout;
import democracy.model.VisualElement;

public class ResponseFieldGenerator 
{
	public static ChangeCardListResponse getChangeCardListResponse() throws Exception
	{
		List<Card> cards;
		List<Event> events;
		List<Layout> layouts;
		
		cards = new CardDAO().getAllCards();
		events = new EventDAO().getAllEvents();
		layouts = new LayoutDAO().getAllLayouts();
		
		return new ChangeCardListResponse(cards.toArray(new Card[0]), events.toArray(new Event[0]), layouts.toArray(new Layout[0]));
	}
	
	public static VisualElementResponse getVisualElementResponse(int cardId) throws Exception
	{
		int layoutId;
		List<Face> faces;
		List<VisualElement> images;
		List<VisualElement> textboxes;
		List<Font> fonts;
		List<Bounds> bounds = new ArrayList<Bounds>();
		List<Layout> layouts;
		
		BoundDAO dao = new BoundDAO();

		layoutId = new CardDAO().getCard(cardId).getLayoutId();
		faces = new FaceDAO().getAllFaces();
		images = new ElementDAO().getAllImages(cardId);
		textboxes = new ElementDAO().getAllTextboxes(cardId);
		fonts = new FontDAO().getAllFonts();
		layouts = new LayoutDAO().getAllLayouts();
		for(VisualElement v: images) {
			bounds.add(dao.getBounds(v.getBoundId()));
		}
		for(VisualElement v: textboxes) {
			bounds.add(dao.getBounds(v.getBoundId()));
		}
		
		
		return new VisualElementResponse(layoutId, faces.toArray(new Face[0]), images.toArray(new VisualElement[0]), textboxes.toArray(new VisualElement[0]), fonts.toArray(new Font[0]), bounds.toArray(new Bounds[0]), layouts.toArray(new Layout[0]));
	}
	
	public static GenerateUrlResponse getGenerateUrlResponse(int cardId) {
		return new GenerateUrlResponse("https://cs509-democracy.s3.amazonaws.com/view.html?cardId="+ cardId);
	}
	
	public static ListImageResponse getListImageResponse() throws Exception
	{
		List<VisualElement> images;
		images = new ElementDAO().listAllImage();
		return new ListImageResponse(images.toArray(new VisualElement[0]));
	}
}
