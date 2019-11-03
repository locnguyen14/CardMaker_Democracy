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
		cardEntry += "<li>ID: " + cardId + "\tRecipient: " + recipient + "</li>";
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
	var recipientName = document.getElementById("recipientName").value;
	var eventSelect = document.getElementById("eventChoice");
	var eventId = events[eventSelect.selectedIndex].id.toString();
	var layoutSelect = document.getElementById("layoutChoice");
	var layoutId = layouts[layoutSelect.selectedIndex].id.toString();
	console.log(eventId);
	console.log(layoutId);
	console.log(recipientName);
	data = {};
	data["recipientName"] = recipientName;
	data["eventId"] = eventId;
	data["layoutId"] = layoutId;
	var js = JSON.stringify(data);
	console.log("JS:" + js);
	var xhr = new XMLHttpRequest();
	xhr.open("POST", createCardUrl, true);
	xhr.send(js);
	console.log("Sent request");
	xhr.onloadend = function ()
	{
		if (xhr.readyState == XMLHttpRequest.DONE)
		{
			console.log("Received request");
			console.log(xhr.responseText);
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

function handleDeleteCardClick()
{
	// Get a list of event list
	var cardList = document.getElementById("cardList").getElementsByTagName("ul");
	
	//Extract out the selected card. I.E elements with class attribute = "selected"
	var count = 0
	for (var i =0; i < cardList.length; i++)
	{
		if(cardList[i].querySelector(".selected") !== null) 
		{
			count++;
			selectedCardId = cardList[i].querySelector(".selected").innerHTML.split("\t")[0].split(" ")[1];
		}
		else {continue;}
	}
	
	// Check if anything is selected
	if(count > 0){
		if(confirm("Do you want to delete Card " + selectedCardId + "?"))
		{
			console.log("Confirm to delete " + selectedCardId);
			processDeleteCard(selectedCardId);
		}
	}
	else
	{
		alert("You did not select antyhing");
	}
	
}

function processDeleteCard(cardId)
{
//	var data = {}
//	data["cardId"] = cardId;
//	var js = JSON.stringify(data);
//	console.log("JS:" + js);
	var xhr = new XMLHttpRequest();
	var newDeleteCardUrl = `${deleteCardUrl}/${cardId}`;
	xhr.open("GET", newDeleteCardUrl, true);
	xhr.send();
	console.log("Sent request");
//	console.log(newDeleteCardUrl);
	xhr.onloadend = function ()
	{
		if (xhr.readyState == XMLHttpRequest.DONE)
		{
			console.log("Received request");
			console.log(xhr.responseText);
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
