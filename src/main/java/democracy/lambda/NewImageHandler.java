package democracy.lambda;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.gson.Gson;

import democracy.dao.BoundDAO;
import democracy.dao.CardDAO;
import democracy.dao.ElementDAO;
import democracy.dao.FaceDAO;
import democracy.http.NewImageRequest;
import democracy.http.RequestResponse;
import democracy.http.ResponseFieldGenerator;
import democracy.http.VisualElementResponse;
import democracy.model.Bounds;
import democracy.model.Card;
import democracy.model.Face;
import democracy.model.VisualElement;

public class NewImageHandler implements RequestStreamHandler {
	LambdaLogger logger;
	


	boolean addImage(int cardId, int faceId, String image, int x, int y, int width, int height, boolean isBase64) throws Exception {
		if (logger != null) { 
			logger.log("in addImage"); 
			}
		
		BoundDAO bdao = new BoundDAO();
		CardDAO cdao = new CardDAO();
		Bounds bounds = new Bounds(0, x, y, width, height);
		int boundId = bdao.addBound(bounds);		
		boolean validcardId = false;
		boolean validfaceId = false;
		for(Card c: cdao.getAllCards()) {
			if (c.getId()==cardId) {
				validcardId = true;
				break;
			}
		}
		FaceDAO fdao = new FaceDAO();
		for(Face f: fdao.getAllFaces()) {
			if (f.getId()==faceId) {
				validfaceId = true;
				break;
			}
		}
		if (validcardId && validfaceId && boundId > 0) {
			ElementDAO dao = new ElementDAO();
			if (isBase64 == true) {
				byte[] bI = java.util.Base64.getDecoder().decode(image.substring(image.indexOf(",")+1));
				InputStream fis = new ByteArrayInputStream(bI);
				AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
				ObjectMetadata metadata = new ObjectMetadata();
				metadata.setContentLength(bI.length);
				metadata.setContentType(image.substring(image.indexOf(":")+1, image.indexOf(";")));
				metadata.setCacheControl("public, max-age=31536000");
				try {
					String filename = UUID.randomUUID().toString();
					s3.putObject("cs509-democracy", filename, fis, metadata);
					s3.setObjectAcl("cs509-democracy", filename, CannedAccessControlList.PublicRead);
					String url = s3.getUrl("cs509-democracy", filename).toString();
					VisualElement Image = new VisualElement(0, cardId, faceId, boundId, url);
					return dao.addImage(Image);
					
				}	catch (AmazonServiceException e) {
				    	System.err.println(e.getErrorMessage());
				    	System.exit(1);
				}
			}
			
			else {
				VisualElement Image = new VisualElement(0, cardId, faceId, boundId, image);
				return dao.addImage(Image);
			}
		}
		
		bdao.deleteBoundsbyId(boundId);
		return false;
	}

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
		
		VisualElementResponse result;
		
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
			NewImageRequest req = new Gson().fromJson(body, NewImageRequest.class);
			logger.log(req.toString());
			
			try {
				int cardId = Integer.parseInt(req.cardId);
				int faceId = Integer.parseInt(req.faceId);
				String image = req.image;
				int x = Integer.parseInt(req.x);
				int y = Integer.parseInt(req.y);
				int width = Integer.parseInt(req.width);
				int height = Integer.parseInt(req.height);
				boolean isBase64 = req.isBase64;
				
				if(addImage(cardId, faceId, image, x, y, width, height, isBase64)) {
					result = ResponseFieldGenerator.getVisualElementResponse(cardId);
					response = new RequestResponse(200, result);
				}
				else {
					response = new RequestResponse(422, "Unable to add image");	
				}
			}catch(Exception e) {
				response = new RequestResponse(400, "Unable to add image for" + req.cardId + "(" + e.getMessage() + ")");
			}
		}
		
		responseJson.put("body", new Gson().toJson(response));  
		responseJson.put("statusCode", response.statusCode);
		
		logger.log(responseJson.toJSONString());
		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		writer.write(responseJson.toJSONString());  
		writer.close();
    }

}
