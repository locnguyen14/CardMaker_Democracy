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
import democracy.http.EditImageRequest;
import democracy.lambda.EditImageHandler;

public class EditImageHandlerTest extends LambdaTest 
{
	 void testSuccessInput(String incoming) throws IOException
	{
		EditImageHandler handler = new EditImageHandler();

        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Edit Image"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals("200", outputNode.get("statusCode").asText());
    }
    
    void testFailInput(String incoming, String statusCode) throws IOException
	{
    	EditImageHandler handler = new EditImageHandler();
		
        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Edit Image"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals(statusCode, outputNode.get("statusCode").asText());
	}
    
    @Test
    public void testEditImage()
    {
    	try 
		{
    		EditImageRequest eir = new EditImageRequest("30", "42", "100", "100", "300", "300");
    		String INPUT_STRING = new Gson().toJson(eir); 
			testSuccessInput(INPUT_STRING);
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
    }
}