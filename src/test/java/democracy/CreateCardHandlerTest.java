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

import democracy.http.CreateCardRequest;
import democracy.lambda.CreateCardHandler;

public class CreateCardHandlerTest extends LambdaTest 
{
	void testSuccessInput(String incoming) throws IOException
	{
		CreateCardHandler handler = new CreateCardHandler();

        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Create Card"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        
        Assert.assertEquals("200", outputNode.get("statusCode").asText());
    }
	
	void testFailInput(String incoming, String statusCode) throws IOException
	{
		CreateCardHandler handler = new CreateCardHandler();
		
        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Create Card"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals(statusCode, outputNode.get("statusCode").asText());
	}
	
	@Test
	public void testCreateCard()
	{
		try 
		{
			CreateCardRequest ccr = new CreateCardRequest("TestDummy", "13", "1");
			String INPUT_STRING = new Gson().toJson(ccr); 
			testSuccessInput(INPUT_STRING);
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
}
