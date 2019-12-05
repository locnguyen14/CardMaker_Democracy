package democracy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import democracy.http.NewImageRequest;
import democracy.http.NewTextBoxRequest;
import democracy.lambda.NewImageHandler;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class NewImageHandlerTest extends LambdaTest
{

	void testSuccessInput(String incoming) throws IOException
	{
		NewImageHandler handler = new NewImageHandler();

        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("New Image"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        
        Assert.assertEquals("200", outputNode.get("statusCode").asText());
    }

	void testFailInput(String incoming, String statusCode) throws IOException
	{
		NewImageHandler handler = new NewImageHandler();
		
        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("New Image"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals(statusCode, outputNode.get("statusCode").asText());
	}

	@Test 
	public void testNewImageFromS3()
	{
		try 
		{
			NewImageRequest ntbr = new NewImageRequest("30", "2", "https://cs509-democracy.s3.amazonaws.com/test_images/site_test.png", "200", "200", "100", "100", "");
			String INPUT_STRING = new Gson().toJson(ntbr); 
			testSuccessInput(INPUT_STRING);
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
	
	/*
	@Test 
	public void testNewImageFromS3DoesNotExist()
	{
		try 
		{
			NewImageRequest ntbr = new NewImageRequest("30", "2", "https://cs509-democracy.s3.amazonaws.com/test_images/site_test2.png", "0", "0", "100", "100", false);
			String INPUT_STRING = new Gson().toJson(ntbr); 
			testFailInput(INPUT_STRING, "422");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
	
	@Test 
	public void testNewImageBase64()
	{
		try 
		{
			NewImageRequest ntbr = new NewImageRequest("30", "2", "data:image/png;base64,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", "0", "0", "100", "100", true);
			String INPUT_STRING = new Gson().toJson(ntbr); 
			testSuccessInput(INPUT_STRING);
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}*/
	
	@Test 
	public void testNewImageInvalidCardId()
	{
		try 
		{
			NewImageRequest ntbr = new NewImageRequest("-1", "2", "https://cs509-democracy.s3.amazonaws.com/test_images/site_test.png", "0", "0", "100", "100", "");
			String INPUT_STRING = new Gson().toJson(ntbr); 
			testFailInput(INPUT_STRING, "422");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
   
}
