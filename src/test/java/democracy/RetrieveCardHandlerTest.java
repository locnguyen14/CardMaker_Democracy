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

import democracy.lambda.RetrieveCardHandler;

public class RetrieveCardHandlerTest extends LambdaTest 
{
	void testSuccessInput(String incoming) throws IOException
	{
		RetrieveCardHandler handler = new RetrieveCardHandler();

        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Retrieve Card"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals("200", outputNode.get("statusCode").asText());
    }
	
	void testFailInput(String incoming, String statusCode) throws IOException
	{
		RetrieveCardHandler handler = new RetrieveCardHandler();
		
        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Retrieve Card"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals(statusCode, outputNode.get("statusCode").asText());
	}
	
	
	@Test
	public void testRetrieveCardBadJson()
	{
		try 
		{
			testFailInput("aksfdas", "422");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
	
	@Test
	public void testRetrieveCardIdNaN()
	{
		try 
		{
			testFailInput("{\"pathParameters\":{\"cardId\":\"NOTANUMBER\"}}", "422");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
	
	@Test
	public void testRetrieveCardInvalidId()
	{
		try 
		{
			testFailInput("{\"pathParameters\":{\"cardId\":\"-1\"}}", "403");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
	
	
	@Test
	public void testRetrieveCardValidId()
	{
		try 
		{
			testSuccessInput("{\"pathParameters\":{\"cardId\":\"30\"}}");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
}
