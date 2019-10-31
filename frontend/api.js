var baseUrl = "https://yvmlvrpr1m.execute-api.us-east-1.amazonaws.com/alpha/"; 

var listCardsUrl	= baseUrl + "main/list/cards";   // GET
var deleteCardUrl	= baseUrl + "main/delete";       // GET
var createCardUrl 	= baseUrl + "main/create";		 // POST

function refreshCardList()
{
	var xhr = new XMLHttpRequest();
	xhr.open("GET", listCardsUrl, true);
	xhr.send();
	
	xhr.onloadend = function ()
	{
		if (xhr.readyState == XMLHttpRequest.DONE)
		{
		    var requestResponse = parseRequestResponse(xhr.responseText);
		    if (requestResponse[2] != null)
		    {
		    	updateCardList(requestResponse[2])
		    }
		    else
		    {
		    	console.log(requestResponse[1]);
		    }
		}
		else
		{
			console.log("Error during xhr");
		}
	}
}

function parseRequestResponse(xhrResponseText)
{
	var xhrResponse = JSON.parse(xhrResponseText);
	var body = JSON.parse(xhrResponse["body"]);
	
	var statusCode = body["statusCode"];
	var errorMessage = "";
	var response = null;
	
	if (statusCode == 200)
	{
		response = body["response"];
		console.log(response);
	} 
	else
	{
		errorMessage = body["errorMessage"];
	}
	
	return [statusCode, errorMessage, response];
}

function updateCardList(lambdaResponse)
{	
	var cardList = document.getElementById("cardList");
	
	var responseCardList = lambdaResponse["cards"]
	
	var output = "";
	
	for (var i = 0; i < responseCardList.length; i++)
	{
		var card = responseCardList[i];
		var cardId = card.id;
		var eventId = card.eventId;
		var recipient = card.recipientName;
		var layoutId = card.layoutId;
		console.log(card);
		
		output += "<div id=\"card" + cardId + "\"><b> CARD: " + cardId + 
			", " + recipient + ": " + eventId + "</b><br></div>";
	}
	
	cardList.innerHTML = output;
}

function handleCreateCardClick()
{

}

function handleDeleteCardClick(val)
{
	// put this here, work on later
//	var data = {};
//	data["name"] = val;
//	
//	var js = JSON.stringify(data);
//	console.log("JS:" + js);
	var xhr = new XMLHttpRequest();
	xhr.open("GET", deleteCardUrl, true);
	
	//Send the collected data as json. In this case nothing
	xhr.send()
	
	xhr.onloadend = function()
	{
		console.log(xhr);
		console.log(xhr.request);
		if (xhr.readyState == XMLHttpRequest.DONE)
		{
			var RequestResponse = parseRequestResponse(xhr.responseText);
			if (requestResponse[2] != null)
			{
				updateCardList(requestResponse[2]);
			}
			else
			{
				console.log(requestResponse[1]);
			}
		}
		else
		{
			console.log("Error during xhr");
		}
	}
	
}