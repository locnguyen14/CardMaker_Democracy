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
import democracy.http.ChangeCardListResponse;
import democracy.http.CreateCardRequest;
import democracy.http.PostRequest;
import democracy.http.PostResponse;
import democracy.http.RequestResponse;
import democracy.http.ResponseFieldGenerator;
import democracy.model.Card;

public class CreateCardHandler implements RequestStreamHandler 
{
	
	LambdaLogger logger;
	
	boolean createCard(String recipient, int eventid) throws Exception {
		if (logger != null) { logger.log("in createCard"); }
		CardDAO dao = new CardDAO();
		
		// check if legal
		if (recipient.length() > 0 && eventid >= 1 && eventid <= 17) {
		Card card = new Card(1, eventid, recipient, 1);
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
				if(createCard(req.recipientName, req.eventId)) {
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
		responseJson.put("statusCode", response.httpCode);
		
		logger.log(responseJson.toJSONString());
		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		writer.write(responseJson.toJSONString());  
		writer.close();
	}
}
