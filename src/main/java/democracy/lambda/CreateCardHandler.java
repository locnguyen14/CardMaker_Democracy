package democracy.lambda;

import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

import democracy.dao.CardDAO;
import democracy.dao.EventDAO;
import democracy.dao.LayoutDAO;
import democracy.http.ChangeCardListResponse;
import democracy.http.CreateCardRequest;
import democracy.http.PostRequest;
import democracy.http.PostResponse;
import democracy.http.RequestResponse;
import democracy.http.ResponseFieldGenerator;
import democracy.model.Card;
import democracy.model.Event;
import democracy.model.Layout;

public class CreateCardHandler implements RequestStreamHandler 
{
	
	LambdaLogger logger;
	
	boolean createCard(String recipient, int eventid, int layoutid) throws Exception {
		if (logger != null) { logger.log("in createCard"); }
		CardDAO dao = new CardDAO();
		EventDAO edao = new EventDAO();
		LayoutDAO ldao = new LayoutDAO();
		boolean valideventid = false;
		boolean validlayoutid = false;
		for (Event e : edao.getAllEvents()) {
			if (e.getId() == eventid) {
				valideventid = true;
				break;
			}
		}
		for (Layout l: ldao.getAllLayouts()) {
			if (l.getId() == layoutid) {
				validlayoutid = true;
				break;
			}
		}
		if (valideventid && validlayoutid) {
			Card card = new Card(1, eventid, recipient, layoutid);
			return dao.addCard(card);
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
		
		ChangeCardListResponse result;
		
		// extract body from incoming HTTP POST request. If any error, then return 422 error
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
		
		if (!inputProcessingFailed) 
		{
			// Create a Delete card request for later 
			CreateCardRequest req = new Gson().fromJson(body, CreateCardRequest.class);
			logger.log(req.toString());
			
			// LOGIC OF LAMBDA FUNCTION	
			try {
				int eid = Integer.parseInt(req.eventId);
				int lid = Integer.parseInt(req.layoutId);
				if(createCard(req.recipientName, eid, lid)) {
					result = ResponseFieldGenerator.getChangeCardListResponse();
					response = new RequestResponse(200, result);
				}
				else {
					response = new RequestResponse(422, "Unable to create card");	
				}
			}catch(Exception e) {
				response = new RequestResponse(400, "Unable to create card for" + req.recipientName + "(" + e.getMessage() + ")");
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
