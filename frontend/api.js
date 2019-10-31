var baseUrl = "https://yvmlvrpr1m.execute-api.us-east-1.amazonaws.com/alpha/"; 

var listCardsUrl	= baseUrl + "main/list/cards";   // GET

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
			updateCardList(xhr.responseText);
		}
		else
		{
			console.log("Error during xhr");
		}
	}
}

function parseRequestResponse(xhrResponseText)
{
	var xhrResponse = JSON.parse(jsonResponse);
	var body = JSON.parse(xhrResponse["body"]);
	
	var statusCode = body["httpCode"];
	var errorString = "";
	var response = Null;
	
	if (statusCode == 200)
	{
		response = body["response"];
	} 
	else
	{
		errorString = body["errorString"];
	}
}

function updateCardList(jsonResponse)
{
	var response = JSON.parse(jsonResponse);
	console.log(response["body"])
	var body = JSON.parse(response["body"]);
	
	var cardList = document.getElementById("cardList");
	
	var output = "";
	
	cardList.innerHTML = output;
}