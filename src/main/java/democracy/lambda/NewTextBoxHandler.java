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

import democracy.dao.BoundDAO;
import democracy.dao.CardDAO;
import democracy.dao.ElementDAO;
import democracy.dao.FaceDAO;
import democracy.dao.FontDAO;
import democracy.dao.LayoutDAO;
import democracy.http.NewTextBoxRequest;
import democracy.http.RequestResponse;
import democracy.http.ResponseFieldGenerator;
import democracy.http.VisualElementResponse;
import democracy.model.Bounds;
import democracy.model.Card;
import democracy.model.Face;
import democracy.model.Font;
import democracy.model.Layout;
import democracy.model.VisualElement;

public class NewTextBoxHandler implements RequestStreamHandler{
	
	LambdaLogger logger;
	
	// judge whether we can add the text
	boolean addtextbox(int cardId, int faceId, String text, int fontId, int x, int y, int width, int height) throws Exception {
		if (logger != null) { 
			logger.log("in addTextBox"); 
			}
		
		ElementDAO dao = new ElementDAO();
		
		if (x < 0 || y < 0) { throw new Exception("X and Y parameters may not be negative."); }
		if (width < 0 || height < 0) { throw new Exception("W and H parameters may not be negative."); }
		
		Card card = new CardDAO().getCard(cardId);
		if (card == null) { throw new Exception("Invalid card ID"); }
		
		Layout cardLayout = new LayoutDAO().getLayoutById(card.getLayoutId());
		if (cardLayout == null) { throw new Exception("Card has invalid layout ID"); }
		
		Face face = new FaceDAO().getFace(faceId);
		if (face == null) { throw new Exception("Invalid face ID"); }
		
		String layout = cardLayout.getLayout();
		int maxWidth = 600;
		int maxHeight = 600;
		if (face.getFaceName() != "Back")
		{
			if (layout == "Portrait")
			{
				maxHeight = 600;
			}
			else if (layout == "Landscape")
			{
				maxWidth = 600;
			}
		}
			
		if (x + width > maxWidth) { throw new Exception("Textbox will not fit on page in x direction."); }
		if (y + height > maxHeight) { throw new Exception("Textbox will not fit on page in y direction"); }
		
		// create the bounds and add it to the database
		BoundDAO bdao = new BoundDAO();
		Bounds bounds = new Bounds(0, x, y, width, height);
		int boundId = bdao.addBound(bounds);
		
		boolean validcardId = false;
		boolean validfaceId = false;
		boolean validfontId = false;
		
		CardDAO cdao = new CardDAO();

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
		FontDAO fontdao = new FontDAO();
		for(Font f: fontdao.getAllFonts()) {
			if(f.getId()==fontId) {
				validfontId = true;;
				break;
			}
		}
		if (validcardId && validfaceId && validfontId && boundId > 0 && text.length()>0) {
			VisualElement textbox = new VisualElement(0, cardId, faceId, boundId, text, fontId);
			return dao.addTextbox(textbox);
		}
		bdao.deleteBoundsbyId(boundId);
		return false;
	}
	
	
	
	
	
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
			NewTextBoxRequest req = new Gson().fromJson(body, NewTextBoxRequest.class);
			logger.log(req.toString());
			
			// LOGIC OF LAMBDA FUNCTION	
			try {
				int cardId = Integer.parseInt(req.cardId);
				int faceId = Integer.parseInt(req.faceId);
				String text = req.text;
				int fontId = Integer.parseInt(req.fontId);
				int x = Integer.parseInt(req.x);
				int y = Integer.parseInt(req.y);
				int width = Integer.parseInt(req.width);
				int height = Integer.parseInt(req.height);
				
				if(addtextbox(cardId, faceId, text, fontId, x, y, width, height)) {
					result = ResponseFieldGenerator.getVisualElementResponse(cardId);
					response = new RequestResponse(200, result);
				}
				else {
					response = new RequestResponse(422, "Unable to add textbox");	
				}
			}catch(Exception e) {
				response = new RequestResponse(400, "Unable to add textbox for" + req.cardId + "(" + e.getMessage() + ")");
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
