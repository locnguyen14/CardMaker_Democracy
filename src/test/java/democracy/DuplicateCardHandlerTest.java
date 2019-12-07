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

import democracy.http.DuplicateCardRequest;
import democracy.lambda.DuplicateCardHandler;

public class DuplicateCardHandlerTest extends LambdaTest 
{
	 void testSuccessInput(String incoming) throws IOException
	{
		DuplicateCardHandler handler = new DuplicateCardHandler();

        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Duplicate Card"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals("200", outputNode.get("statusCode").asText());
    }
    
    void testFailInput(String incoming, String statusCode) throws IOException
	{
    	DuplicateCardHandler handler = new DuplicateCardHandler();
		
        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Duplicate Card"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals(statusCode, outputNode.get("statusCode").asText());
	}
    
    @Test
    public void testDuplicateCard()
    {
    	try 
		{
    		DuplicateCardRequest dcr = new DuplicateCardRequest("30", "DUPLICATERECIPIENT");
    		String INPUT_STRING = new Gson().toJson(dcr); 
			testSuccessInput(INPUT_STRING);
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
    }
    
    @Test
    public void testDuplicateCardInvalidId()
    {
    	try 
		{
    		DuplicateCardRequest dcr = new DuplicateCardRequest("-1", "DUPLICATERECIPIENT");
    		String INPUT_STRING = new Gson().toJson(dcr); 
			testFailInput(INPUT_STRING, "400");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
    }
}
