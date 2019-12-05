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

import democracy.lambda.GenerateUrlHandler;
import democracy.lambda.ListCardHandler;
import democracy.lambda.RetrieveCardHandler;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class GenerateUrlHandlerTest extends LambdaTest
{
    void testSuccessInput(String incoming) throws IOException
	{
		GenerateUrlHandler handler = new GenerateUrlHandler();

        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Generate URL"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals("200", outputNode.get("statusCode").asText());
    }
    
    void testFailInput(String incoming, String statusCode) throws IOException
	{
		GenerateUrlHandler handler = new GenerateUrlHandler();
		
        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Generate URL"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals(statusCode, outputNode.get("statusCode").asText());
	}
	
	@Test
	public void testGenerateUrl()
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
	
	@Test
	public void testGenerateUrlInvalidJson()
	{
		try 
		{
			testFailInput("{\"pathParameters\":{\"cardId\":\":\"30\"}}", "422");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
	
	/*
	@Test
	public void testGenerateUrlInvalidCardId()
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
	*/
	
	@Test
	public void testGenerateUrlInvalidNumber()
	{
		try 
		{
			testFailInput("{\"pathParameters\":{\"cardId\":\"asdf\"}}", "422");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
}
