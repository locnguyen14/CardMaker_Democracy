package democracy.lambda;

import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

import democracy.dao.BoundDAO;
import democracy.dao.CardDAO;
import democracy.dao.ElementDAO;
import democracy.http.DuplicateCardRequest;
import democracy.http.RequestResponse;
import democracy.http.ResponseFieldGenerator;
import democracy.http.VisualElementResponse;
import democracy.model.Bounds;
import democracy.model.Card;
import democracy.model.VisualElement;

public class DuplicateCardHandler implements RequestStreamHandler {
	
	LambdaLogger logger;
	
	boolean duplicateCard(int cardId, String recipient) throws Exception{
		if(recipient.length()>0) {
		Card card = new CardDAO().getCard(cardId);
		Card newcard = new Card(0, card.getEventId(), recipient, card.getLayoutId());
		int new_cardId = new CardDAO().addCard(newcard);
		List<VisualElement> images = new ElementDAO().getAllImages(cardId);
		List<VisualElement> textboxes = new ElementDAO().getAllTextboxes(cardId);
		BoundDAO bdao = new BoundDAO();
		ElementDAO edao = new ElementDAO();
		for(VisualElement image: images) {
			Bounds bounds = bdao.getBounds(image.getBoundId());
			int new_boundId = bdao.addBound(bounds);
			VisualElement new_image = new VisualElement(0, new_cardId, image.getFaceId(), new_boundId, image.getContent());
			edao.addImage(new_image);
		}
		
		for(VisualElement textbox: textboxes) {
			Bounds bounds = bdao.getBounds(textbox.getBoundId());
			int new_boundId = bdao.addBound(bounds);
			VisualElement new_textbox = new VisualElement(0, new_cardId, textbox.getFaceId(), new_boundId, textbox.getContent(), textbox.getFontId());
			edao.addTextbox(new_textbox);
		}
		return true;
		}
		return false;
	}

    @Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException 
	{
		logger = context.getLogger();
		
		// set up response
		JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");  // not sure if needed anymore?
		headerJson.put("Access-Control-Allow-Methods", "GET,POST,DELETE,OPTIONS");
		headerJson.put("Access-Control-Allow-Origin",  "*");

		JSONObject responseJson = new JSONObject();
		responseJson.put("headers", headerJson);
		
		RequestResponse response = null;
		
		VisualElementResponse result;
		
		String body;
		boolean inputProcessingFailed = false;
		
		try 
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			JSONParser parser = new JSONParser();
			JSONObject event = (JSONObject) parser.parse(reader);
			logger.log("event:" + event.toJSONString());
			
			body = (String)event.get("body");
			if (body == null) 
			{
				body = event.toJSONString();  // this is only here to make testing easier
			}
		}
		catch (ParseException pe) 
		{
				logger.log(pe.toString());
				response = new RequestResponse(442, "Unable to process input");  // unable to process input
				responseJson.put("body", new Gson().toJson(response));
				inputProcessingFailed = true;
				body = null;
		}
		
		
		
		if (!inputProcessingFailed) {
			DuplicateCardRequest req = new Gson().fromJson(body, DuplicateCardRequest.class);
			logger.log(req.toString());
			// LOGIC OF LAMBDA FUNCTION
			try 
			{	
				int cardId = Integer.parseInt(req.cardId);
				String recipient = req.recipient;
				Card card = new CardDAO().getCard(cardId);
				Card newcard = new Card(0, card.getEventId(), recipient, card.getLayoutId());
				int new_cardId = new CardDAO().addCard(newcard);
				List<VisualElement> images = new ElementDAO().getAllImages(cardId);
				List<VisualElement> textboxes = new ElementDAO().getAllTextboxes(cardId);
				BoundDAO bdao = new BoundDAO();
				ElementDAO edao = new ElementDAO();
				for(VisualElement image: images) {
					Bounds bounds = bdao.getBounds(image.getBoundId());
					int new_boundId = bdao.addBound(bounds);
					VisualElement new_image = new VisualElement(0, new_cardId, image.getFaceId(), new_boundId, image.getContent());
					edao.addImage(new_image);
				}
			
				for(VisualElement textbox: textboxes) {
					Bounds bounds = bdao.getBounds(textbox.getBoundId());
					int new_boundId = bdao.addBound(bounds);
					VisualElement new_textbox = new VisualElement(0, new_cardId, textbox.getFaceId(), new_boundId, textbox.getContent(), textbox.getFontId());
					edao.addTextbox(new_textbox);
				}
				result = ResponseFieldGenerator.getVisualElementResponse(new_cardId);
				response = new RequestResponse(200, result);
			} 
			
			catch (Exception e)
			{
				response = new RequestResponse(400, "unable to duplicate the card");
			}
		}
		
		//last thing we do 
		responseJson.put("body", new Gson().toJson(response));  
		responseJson.put("statusCode", response.statusCode);
		
		logger.log(responseJson.toJSONString());
		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		writer.write(responseJson.toJSONString());  
		writer.close();
	}

}
