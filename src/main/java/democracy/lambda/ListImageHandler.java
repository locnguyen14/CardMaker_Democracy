package democracy.lambda;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;

import democracy.http.ListImageResponse;
import democracy.http.RequestResponse;
import democracy.http.ResponseFieldGenerator;



public class ListImageHandler implements RequestStreamHandler {

    private AmazonS3 s3 = null;

    // Return a list of base64 encoded string for each image in S3
    List<String> getImagesFromBucket(String filename) throws Exception
    {
        if(s3 == null)
        {
            s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        }

        // List all the objects within the buckets
        ObjectListing result;
        result = s3.listObjects(filename);
 
        List<String> imageS3URL = new ArrayList<String>();
        for (S3ObjectSummary objectSummary : result.getObjectSummaries()) 
        {
        	String imageURL = "https://cs509-democracy.s3.amazonaws.com/" + objectSummary.getKey();
        	imageS3URL.add(imageURL);
        }
        
        return imageS3URL;

    }
    
//    // Encode an image to base64 
//    String encodeBase64URL(BufferedImage imgBuf) throws IOException
//    {
//    	String base64;
//		if (imgBuf == null) 
//		{
//			base64 = null;
//		}
//		else 
//		{
//			Base64 encoder = new Base64();
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			ImageIO.write(imgBuf, "png", out);
//			byte[] bytes = out.toByteArray();
//			base64 = "data:image/png;base64," + new String(encoder.encode(bytes), "UTF-8");
//		}
//		
//		return base64;
//	}


    LambdaLogger logger;
    
    @SuppressWarnings("unchecked")
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
		
		ListImageResponse result;
		
		try 
		{
			String filename = "cs509-democracy/images";
			List<String> imageS3URL = getImagesFromBucket(filename);
			result = ResponseFieldGenerator.getListImageResponse(imageS3URL);
			response = new RequestResponse(200, result);
		}
		catch (Exception e)
		{
			response = new RequestResponse(500, e.getMessage());
		}
		
		//last thing we do 
		responseJson.put("body", new Gson().toJson(response));  
		responseJson.put("statusCode", response.statusCode);

		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		writer.write(responseJson.toJSONString());  
		writer.close();
    }

}
