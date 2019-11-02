var baseUrl = "https://yvmlvrpr1m.execute-api.us-east-1.amazonaws.com/alpha/"; 

var listCardsUrl	= baseUrl + "main/list/cards";   // GET
var deleteCardUrl	= baseUrl + "main/delete";
var createCardUrl 	= baseUrl + "main/create";

/*
 *		CARD SELECTION CODE
 */

var selectedCardId = null;

function selectCard(event) 
{
    if (event.target.tagName != "LI") return;
    
    console.log(event);
    
    // Get list of event lists
    var cardList = document.getElementById("cardList").getElementsByTagName("ul");
 
    // For each event list, deselect all selected 
    for (var i = 0; i < cardList.length; i++)
    {
    	var eventCardList = cardList[i].getElementsByTagName("li");
    	var selectedCards = cardList[i].querySelectorAll('.selected');
    	for (let elt of selectedCards)
    	{
    		elt.classList.remove('selected');
    	}
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
	var eventChoiceList = document.getElementById("eventChoice");
	var layoutChoiceList = document.getElementById("layoutChoice");
	
	var responseCardList = lambdaResponse["cards"];
	var responseEventList = lambdaResponse["events"];
	var responseLayoutList = lambdaResponse["layouts"];
	
	events = responseEventList;
	layouts = responseLayoutList;
	
	var output = "";
	var eventChoiceOutput = "";
	var layoutChoiceOutput = "";
	
	for (var i = 0; i < responseEventList.length; i++)
	{
		var event= responseEventList[i];
		var eventId = event.id;
		var eventName = event.name;
		
		output += "<h3>" + eventName + "</h3><ul id=\"event-" + eventId + "\" onclick=selectCard(event) onmousedown=doNothing()></ul>";
		eventChoiceOutput += "<option value =\"" + eventId + "\">" + eventName + "</option>";
	}
	
	cardList.innerHTML = output;
	eventChoiceList.innerHTML = eventChoiceOutput;
		
	for (var i = 0; i < responseCardList.length; i++)
	{
		var card = responseCardList[i];
		var cardId = card.id;
		var eventId = card.eventId;
		var recipient = card.recipientName;
		
		var eventList = document.getElementById("event-"+eventId);
		
		console.log(eventList);
		var cardEntry = eventList.innerHTML;
		cardEntry += "<li><b>ID:</b> " + cardId + "         <b>Recipient:</b> " + recipient + "</li>";
		eventList.innerHTML = cardEntry;
	}
	
	console.log(layouts);
	for (var i = 0; i < layouts.length; i++)
	{
		var layoutId = layouts[i].id;
		var layoutName = layouts[i].layout;
		layoutChoiceOutput += "<option value =\"" + layoutId + "\">" + layoutName + "</option>";
	}
	layoutChoiceList.innerHTML = layoutChoiceOutput;
	
}

function handleCreateCardClick(e)
{
	var eventSelect = document.getElementById("eventChoice");
	var eventId = events[eventSelect.selectedIndex].id;
	var layoutSelect = document.getElementById("layoutChoice");
	var layoutId = layouts[layoutSelect.selectedIndex].id;
	console.log(eventId);
	console.log(layoutId);
}

function handleDeleteCardClick()
{
	
}