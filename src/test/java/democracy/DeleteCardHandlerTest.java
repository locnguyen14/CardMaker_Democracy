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
import democracy.lambda.DeleteCardHandler;

public class DeleteCardHandlerTest extends LambdaTest
{
	void testSuccessInput(String incoming) throws IOException
	{
		DeleteCardHandler handler = new DeleteCardHandler();

        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Delete Card"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        
        Assert.assertEquals("200", outputNode.get("statusCode").asText());
    }

	void testFailInput(String incoming, String statusCode) throws IOException
	{
		DeleteCardHandler handler = new DeleteCardHandler();
		
        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Delete Card"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals(statusCode, outputNode.get("statusCode").asText());
	}
	
	@Test
	public void testDeleteCard()
	{
		try 
		{
			testSuccessInput("{\"pathParameters\":{\"cardId\":\"31\"}}");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
	
	@Test
	public void testDeleteCardInvalidCardId()
	{
		try 
		{
			testFailInput("{\"pathParameters\":{\"cardId\":\"-1\"}}", "422");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
	
	@Test
	public void testDeleteCardInvalidJson()
	{
		try 
		{
			testFailInput("{\"pathParameters\":{:\"{\"cardId\":\"-1\"}}", "422");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
	
	@Test
	public void testDeleteCardCardIdNaN()
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
}
