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

import democracy.http.NewTextBoxRequest;
import democracy.lambda.NewTextBoxHandler;

public class NewTextBoxHandlerTest extends LambdaTest 
{

	void testSuccessInput(String incoming) throws IOException
	{
		NewTextBoxHandler handler = new NewTextBoxHandler();

        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("New Text Box"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        
        Assert.assertEquals("200", outputNode.get("statusCode").asText());
    }

	void testFailInput(String incoming, String statusCode) throws IOException
	{
		NewTextBoxHandler handler = new NewTextBoxHandler();
		
        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("New Text Box"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals(statusCode, outputNode.get("statusCode").asText());
	}

	@Test 
	public void testNewTextBox()
	{
		try 
		{
			NewTextBoxRequest ntbr = new NewTextBoxRequest("30", "1", "Testing.", "2", "0", "0", "100", "100");
			String INPUT_STRING = new Gson().toJson(ntbr); 
			testSuccessInput(INPUT_STRING);
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
	
}

