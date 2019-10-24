package com.amazonaws.lambda.demo;

import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

import democracy.http.PostRequest;
import democracy.http.PostResponse;




public class CreateCardHandler implements RequestStreamHandler {
	
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
		
		PostResponse response = null;
		
		// extract body from incoming HTTP POST request. If any error, then return 422 error
		String body;
		boolean processed = false;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			JSONParser parser = new JSONParser();
			JSONObject event = (JSONObject) parser.parse(reader);
			logger.log("event:" + event.toJSONString());
			
			body = (String)event.get("body");
			if (body == null) {
				body = event.toJSONString();  // this is only here to make testing easier
			}
		}catch (ParseException pe) {
				logger.log(pe.toString());
				response = new PostResponse("unable to process input", 422);  // unable to process input
				responseJson.put("body", new Gson().toJson(response));
				processed = true;
				body = null;
			}
		
		if (!processed) {
			PostRequest req = new Gson().fromJson(body, PostRequest.class);
			logger.log(req.toString());
			
			// Byte 64 String Encoded? or maybe just add it to the database
					
		}
		
		//last thing we do 
		responseJson.put("body", new Gson().toJson(response));  
//		responseJson.put("statusCode", response.httpCode);
		
		logger.log(responseJson.toJSONString());
		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		writer.write(responseJson.toJSONString());  
		writer.close();
		
	}

}
