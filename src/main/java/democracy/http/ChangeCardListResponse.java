package democracy.http;

import democracy.model.Card;
import democracy.model.Event;
import democracy.model.Layout;

public class ChangeCardListResponse extends ResponseField 
{
	public Card cards[];
	public Event events[];
	public Layout layouts[];
	
	public ChangeCardListResponse(Card cards[], Event events[], Layout layouts[])
	{
		this.cards = cards;
		this.events = events;
		this.layouts = layouts;
	}
}