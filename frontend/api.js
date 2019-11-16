var baseUrl = "https://yvmlvrpr1m.execute-api.us-east-1.amazonaws.com/alpha/"; 
var htmlBaseUrl = "https://cs509-democracy.s3.amazonaws.com/";

var listCardsUrl	= baseUrl + "main/list/cards";   // GET
var deleteCardUrl	= baseUrl + "main/delete";
var createCardUrl 	= baseUrl + "main/create";
var generateCardUrl = baseUrl + "main/generateUrl";

var editorUrl		= htmlBaseUrl + "editor.html";

/*
 *		CARD SELECTION CODE
 */
var selectedCardId = null;

function selectCard(event) 
{
    if (event.target.tagName != "LI") return;
    
    if (event.target.classList.contains('selected')) 
    {
    	event.target.classList.remove('selected');
    	selectedCardId = null;
    	return;
    }
    
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
    selectedCardId = event.target.value;
}

// prevent unneeded selection of list elements on clicks
function doNothing() { return false; };

/*
 *		API CODE
 */

masterCardList = {};
masterEventList = {};
masterLayoutList = {};

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
		    	alert(requestResponse[1]);
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
	} 
	else
	{
		errorMessage = body["errorMessage"];
	}
	
	return [statusCode, errorMessage, response];
}

function updateCardList(lambdaResponse)
{	
	var responseCardList = lambdaResponse["cards"];
	var responseEventList = lambdaResponse["events"];
	var responseLayoutList = lambdaResponse["layouts"];
	
	for (var i = 0; i < responseEventList.length; i++)
	{
		var jsonEvent = responseEventList[i];
		masterEventList[jsonEvent.name] = jsonEvent.id;
		masterCardList[jsonEvent.id] = [];
	}
	
	for (var i = 0; i < responseCardList.length; i++)
	{
		var responseCard = responseCardList[i];
		masterCardList[responseCard.eventId].push(responseCard);
	}
	
	for (var i = 0; i < responseLayoutList.length; i++)
	{
		var responseLayout = responseLayoutList[i];
		masterLayoutList[responseLayout.layout] = responseLayout.id;
	}
	
	displayCardList();
}

function displayCardList()
{
	// Set the events in CardList and EventChoice
	var cardList = document.getElementById("cardList");
	var cardListOutput = "";
	var eventChoiceList = document.getElementById("eventChoice");
	var eventChoiceOutput = "";
	for (let [eventName, eventId] of Object.entries(masterEventList))
	{
		cardListOutput += "<h3>" + eventName + "</h3><ul id=\"event-" + eventId + "\" onclick=selectCard(event) onmousedown=doNothing()></ul>";
		eventChoiceOutput += "<option value=\"" + eventId + "\">" + eventName + "</option>";
	}
	cardList.innerHTML = cardListOutput;
	eventChoiceList.innerHTML = eventChoiceOutput;
	
	// Set the card li elements for each event ul
	for (let [eventId, cardArray] of Object.entries(masterCardList))
	{
		var eventUl = document.getElementById("event-" + eventId);
		var eventUlOutput = "";
		for (var i = 0; i < cardArray.length; i++)
		{
			var currCard = cardArray[i];
			eventUlOutput += "<li value=\"" + currCard.id + "\">ID: " + currCard.id + "   Recipient: " + currCard.recipientName + "</li>";
		}
		eventUl.innerHTML = eventUlOutput;
	}
	
	// Set the layout choices for create card dialog
	var layoutChoiceList = document.getElementById("layoutChoice");
	var layoutChoiceOutput = "";
	for (let [layoutName, layoutId] of Object.entries(masterLayoutList))
	{
		layoutChoiceOutput += "<option value=\"" + layoutId + "\">" + layoutName + "</option>";
	}
	layoutChoiceList.innerHTML = layoutChoiceOutput;
}

function handleCreateCardClick(e)
{
	// Check that recipient field has a value
	var recipientName = document.getElementById("recipientName").value;
	if (recipientName == "") { return; }
	
	// Extract selected eventId and layoutId
	var eventSelect = document.getElementById("eventChoice");
	var eventId = eventSelect.options[eventSelect.selectedIndex].value;
	var layoutSelect = document.getElementById("layoutChoice");
	var layoutId = layoutSelect.options[layoutSelect.selectedIndex].value;
	
	// Create JSON object for request
	data = {};
	data["recipientName"] = recipientName;
	data["eventId"] = eventId;
	data["layoutId"] = layoutId;
	var js = JSON.stringify(data);
	
	// Send request
	var xhr = new XMLHttpRequest();
	xhr.open("POST", createCardUrl, true);
	xhr.send(js);
	console.log("Sent request");
	xhr.onloadend = function ()
	{
		if (xhr.readyState == XMLHttpRequest.DONE)
		{
			console.log("Received response");
		    var requestResponse = parseRequestResponse(xhr.responseText);
		    if (requestResponse[2] != null)
		    {
		    	updateCardList(requestResponse[2])
		    }
		    else
		    {
		    	alert(requestResponse[1]);
		    }
		}
		else
		{
			console.log("Error during xhr");
		}
	}
}

function handleEditCardClick()
{
	if (selectedCardId == null) { alert("Please select a card to edit."); return; }
	
	sessionStorage.setItem("editCardId", selectedCardId);
	
	location.replace(editorUrl);
}

function handleDeleteCardClick()
{
	if (selectedCardId == null)
	{
		alert("Please select a card to delete.");
	} 
	else if(confirm("Do you want to delete Card " + selectedCardId + "?"))
	{
		processDeleteCard(selectedCardId);
	}
}

function processDeleteCard(cardId)
{
	var xhr = new XMLHttpRequest();
	var newDeleteCardUrl = `${deleteCardUrl}/${cardId}`
	xhr.open("GET", newDeleteCardUrl, true);
	xhr.send();
	console.log("Sent request");
	xhr.onloadend = function ()
	{
		if (xhr.readyState == XMLHttpRequest.DONE)
		{
			console.log("Received response");
			console.log(xhr.responseText);
		    var requestResponse = JSON.parse(xhr.responseText)["response"];
		    if (requestResponse != null)
		    {
		    	updateCardList(requestResponse);
		    	selectedCardId = null;
		    }
		    else
		    {
		    	alert("Error no card existed anymore ");
		    }
		}
		else
		{
			console.log("Error during xhr");
		}
	}
}

function handleGenerateCardURLClick()
{
	if (selectedCardId == null)
	{
		alert("Please select a card.");
	} 
	else
	{
		document.getElementById('generateUrlModal').style.display='block';
		processGenerateCardUrl(selectedCardId);
	}
}

function processGenerateCardUrl(cardId)
{
	var xhr = new XMLHttpRequest();
	var newGenerateUrlUrl = `${generateCardUrl}/${cardId}`;
	xhr.open("GET", newGenerateUrlUrl, true);
	xhr.send();
	console.log("API - GENERATE URL: Sent request");
	xhr.onloadend = function()
	{
		if (xhr.readyState == XMLHttpRequest.DONE)
		{
			console.log("API - GENERATE URL: Received response");
			
			var requestResponse = JSON.parse(xhr.responseText);
		   	
	    	if (requestResponse["statusCode"] == 200)
			{
	    		var urlDisplay = document.getElementById("urlDisplay");
	    		urlDisplay.innerHTML = requestResponse["response"]["url"];
			}
			else
			{
				alert(requestResponse["errorString"]);
			}
		}
		else
		{
			console.log("Error during xhr");
		}
	}
}

function handleCloseGenerateCardURLModal(e)
{
	document.getElementById('generateUrlModal').style.display='none';
	var urlDisplay = document.getElementById("urlDisplay");
	urlDisplay.innerHTML = "Waiting for server...";
}
