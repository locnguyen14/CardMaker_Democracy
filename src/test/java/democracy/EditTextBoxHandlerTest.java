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
import democracy.http.EditTextBoxRequest;
import democracy.lambda.EditTextBoxHandler;

public class EditTextBoxHandlerTest extends LambdaTest 
{
    void testSuccessInput(String incoming) throws IOException
	{
		EditTextBoxHandler handler = new EditTextBoxHandler();

        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Edit Text Box"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals("200", outputNode.get("statusCode").asText());
    }
    
    void testFailInput(String incoming, String statusCode) throws IOException
	{
    	EditTextBoxHandler handler = new EditTextBoxHandler();
		
        InputStream input = new ByteArrayInputStream(incoming.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("Edit Text Box"));

        JsonNode outputNode = Jackson.fromJsonString(output.toString(), JsonNode.class);
        Assert.assertEquals(statusCode, outputNode.get("statusCode").asText());
	}
    
    @Test
    public void testEditTextBox()
    {
    	try 
		{
    		EditTextBoxRequest etbr = new EditTextBoxRequest("30", "41", "ReplacementText", "2", "100", "100", "200", "200");
    		String INPUT_STRING = new Gson().toJson(etbr); 
			testSuccessInput(INPUT_STRING);
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
    }
    
    @Test
    public void testEditTextBoxBackElement()
    {
    	try 
		{
    		EditTextBoxRequest etbr = new EditTextBoxRequest("30", "38", "ReplacementText", "2", "100", "100", "200", "200");
    		String INPUT_STRING = new Gson().toJson(etbr); 
			testFailInput(INPUT_STRING, "400");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
    }
    
    @Test
    public void testEditTextBoxNewBoundsInvalid()
    {
    	try 
		{
    		EditTextBoxRequest etbr = new EditTextBoxRequest("30", "41", "ReplacementText2", "1", "600", "600", "100", "100");
    		String INPUT_STRING = new Gson().toJson(etbr); 
			testFailInput(INPUT_STRING, "400");
		}
		catch (IOException io)
		{
			Assert.fail("Invalid: " + io.getMessage());
		}
    }
}
