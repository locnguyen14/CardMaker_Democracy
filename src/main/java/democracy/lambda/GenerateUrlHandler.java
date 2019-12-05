package democracy.lambda;

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

import democracy.dao.CardDAO;
import democracy.http.ChangeCardListResponse;
import democracy.http.GenerateUrlResponse;
import democracy.http.RequestResponse;
import democracy.http.ResponseFieldGenerator;
import democracy.model.Card;

public class GenerateUrlHandler implements RequestStreamHandler {
	
	LambdaLogger logger;

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

    	logger = context.getLogger();
		
		// set up response
		JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");  // not sure if needed anymore?
		headerJson.put("Access-Control-Allow-Methods", "GET,POST,DELETE,OPTIONS");
		headerJson.put("Access-Control-Allow-Origin",  "*");

		JSONObject responseJson = new JSONObject();
		responseJson.put("headers", headerJson);
		
		RequestResponse response = null;
		
		GenerateUrlResponse result;
		
		// Incoming HTTP GET request, but has nothing in body. 
		// Have to think of way to extract cardID from path
		// Ideas: https://forums.aws.amazon.com/thread.jspa?messageID=643660
		boolean inputProcessingFailed = false;
		String path;
		int cardID = -1;
		
		
		try 
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			JSONParser parser = new JSONParser();
			JSONObject event = (JSONObject) parser.parse(reader);
			logger.log("event:" + event.toJSONString());
			
			path = (String) ((JSONObject) event.get("pathParameters")).get("cardId"); // always check the cloudwatch log
			logger.log("Path=" + path);
			cardID = Integer.parseInt(path);
	
		}
		catch(ParseException pe) 
		{
			logger.log(pe.toString());
			response = new RequestResponse(422, "Bad Request Parsing");  // unable to process input
			responseJson.put("body", new Gson().toJson(response));
			inputProcessingFailed = true;
			path = null;
		}
		catch(NumberFormatException ne) 
		{
			logger.log(ne.toString());
			response = new RequestResponse(422, "Bad Request Number Format");  // unable to process input
			responseJson.put("body", new Gson().toJson(response));
			inputProcessingFailed = true;
			path = null;
		}
		 
		 
		if (!inputProcessingFailed) 
		{
			
			// LOGIC OF LAMBDA FUNCTION
			
			try 
			{
				result = ResponseFieldGenerator.getGenerateUrlResponse(cardID);
				response = new RequestResponse(200, result);

			} 
			catch (Exception e)
			{
				response = new RequestResponse(403, e.getMessage());
			}
		}
		
		//response = new RequestResponse(Integer.parseInt(path), "Fake error");
		//last thing we do 
		responseJson.put("body", new Gson().toJson(response));  
		responseJson.put("statusCode", response.statusCode);
		
		logger.log(responseJson.toJSONString());
		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		writer.write(responseJson.toJSONString());  
		writer.close();
    }

}
