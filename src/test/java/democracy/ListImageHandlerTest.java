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

import democracy.lambda.ListImageHandler;

public class ListImageHandlerTest extends LambdaTest 
{
	void testSuccessInput(String incoming) throws IOException
	{
		ListImageHandler handler = new ListImageHandler();

        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("List Images"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals("200", outputNode.get("statusCode").asText());
    }
	
	@Test
	public void testListImageHandler()
	{
		try 
		{
			testSuccessInput("");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
	}
}
