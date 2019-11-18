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

import democracy.dao.ElementDAO;
import democracy.http.DeleteElementRequest;
import democracy.http.RequestResponse;
import democracy.http.ResponseFieldGenerator;
import democracy.http.VisualElementResponse;
import democracy.model.VisualElement;

public class DeleteElementHandler implements RequestStreamHandler {

	public LambdaLogger logger = null;
	
	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler to delete Element");
		
		// set up response
		JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");  // not sure if needed anymore?
		headerJson.put("Access-Control-Allow-Methods", "GET,POST,DELETE,OPTIONS");
		headerJson.put("Access-Control-Allow-Origin",  "*");

		JSONObject responseJson = new JSONObject();
		responseJson.put("headers", headerJson);
		
		RequestResponse response = null;
		VisualElementResponse result;
		
		// extract body from incoming HTTP POST request. If any error, then return 422 error
		String path;
		int elementId = -1;
		boolean inputProcessingFailed = false;
		
		try 
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			JSONParser parser = new JSONParser();
			JSONObject event = (JSONObject) parser.parse(reader);
			logger.log("event:" + event.toJSONString());
			
			path = (String) ((JSONObject) event.get("pathParameters")).get("elementId"); // always check the cloudwatch log
			logger.log("Path=" + path);
			elementId = Integer.parseInt(path);
			
			if (path == null) 
			{
				path = event.toJSONString();  // this is only here to make testing easier
			}
		}
		catch (ParseException pe) 
		{
				logger.log(pe.toString());
				response = new RequestResponse(442, "Unable to process input");  // unable to process input
				responseJson.put("body", new Gson().toJson(response));
				inputProcessingFailed = true;
				path = null;
		}

		if(!inputProcessingFailed) 
		{
			try 
			{	
				// Get the element. In this case, either  getTextBox or getImage works.
				ElementDAO dao = new ElementDAO();
				//VisualElement element = dao.getTextbox(elementId);
				VisualElement element = dao.getVisualElement(elementId);
				int cardId = element.getCardId();

				// Delete the element
				if (dao.deleteVisualElement(element)) 
				{
					result = ResponseFieldGenerator.getVisualElementResponse(cardId);
					response = new RequestResponse(200, result);
				}
				else 
				{
					response = new RequestResponse(422, "Unable to Delete an Element");
				}
			}
			catch (Exception e) 
			{
				response = new RequestResponse(403, e.getMessage());
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
