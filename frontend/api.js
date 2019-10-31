var baseUrl = "https://yvmlvrpr1m.execute-api.us-east-1.amazonaws.com/alpha/"; 

var listCardsUrl	= baseUrl + "main/list/cards";   // GET
var deleteCardUrl	= baseUrl + "main/delete";       // GET
var createCardUrl 	= baseUrl + "main/create";		 // POST

/*
 *		CARD SELECTION CODE
 */

var selectedCardId = null;

function selectCard(event) 
{
    if (event.target.tagName != "LI") return;
    
    console.log(event);
    
    var cardList = document.getElementById("cardList").getElementsByTagName("ul");
    
    for (var i = 0; i < cardList.length; i++)
    {
    	
    }
    
    let selected = event.target.parentElement.querySelectorAll('.selected');
    for(let elem of selected) 
    {
      elem.classList.remove('selected');
    }
    event.target.classList.add('selected');
    console.log(event.target.innerHTML);
}

// prevent unneeded selection of list elements on clicks
function doNothing() 
{
	return false;
};

/*
 *		API CODE
 */

events = null;
layouts = null;

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
	console.log(body);
	
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
	
	var responseCardList = lambdaResponse["cards"];
	var responseEventList = lambdaResponse["events"];
	var responseLayoutList = lambdaResponse["layouts"];
	
	events = responseEventList;
	layouts = responseLayoutList;
	
	var output = "";
	
	for (var i = 0; i < responseEventList.length; i++)
	{
		var event= responseEventList[i];
		var eventId = event.id;
		var eventName = event.name;
		
		output += "<h3>" + eventName + "</h3><ul id=\"event-" + eventId + "\" onclick=selectCard(event) onmousedown=doNothing()></ul>";
	}
	
	cardList.innerHTML = output;
		
	for (var i = 0; i < responseCardList.length; i++)
	{
		var card = responseCardList[i];
		var cardId = card.id;
		var eventId = card.eventId;
		var recipient = card.recipientName;
		
		var eventList = document.getElementById("event-"+eventId);
		
		console.log(eventList);
		var cardEntry = eventList.innerHTML;
		cardEntry += "<li>ID: " + cardId + "\tRecipient: " + recipient + "</li>";
		eventList.innerHTML = cardEntry;
	}
}

function handleCreateCardClick()
{

}

function handleDeleteCardClick()
{
	// Validate on selecting the card when hitting the delete card button
	if (document)
	
	
	
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