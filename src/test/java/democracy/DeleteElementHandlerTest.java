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

import democracy.lambda.DeleteCardHandler;
import democracy.lambda.DeleteElementHandler;

public class DeleteElementHandlerTest extends LambdaTest 
{
	void testSuccessInput(String incoming) throws IOException
	{
		DeleteElementHandler handler = new DeleteElementHandler();

        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Delete Element"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        
        Assert.assertEquals("200", outputNode.get("statusCode").asText());
    }

	void testFailInput(String incoming, String statusCode) throws IOException
	{
		DeleteElementHandler handler = new DeleteElementHandler();
		
        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Delete Element"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals(statusCode, outputNode.get("statusCode").asText());
	}
	
	@Test
	public void testDeleteElement()
	{
		try 
		{
			testSuccessInput("{\"pathParameters\":{\"elementId\":\"40\"}}");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
	
	@Test
	public void testDeleteElementInvalidId()
	{
		try 
		{
			testFailInput("{\"pathParameters\":{\"elementId\":\"-1\"}}", "403");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
	
	@Test
	public void testDeleteElementInvalidJson()
	{
		try 
		{
			testFailInput("{\"pathParameters\"{}:\":{\"elementId\":\"40\"}}", "442");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
}
