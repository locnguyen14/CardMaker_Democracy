package democracy.http;

import java.util.List;

import democracy.dao.CardDAO;
import democracy.dao.EventDAO;
import democracy.dao.LayoutDAO;
import democracy.model.Card;
import democracy.model.Event;
import democracy.model.Layout;

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
}
