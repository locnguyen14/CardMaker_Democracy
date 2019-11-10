var apiBaseUrl = "https://yvmlvrpr1m.execute-api.us-east-1.amazonaws.com/alpha/"; 
var htmlBaseUrl = "https://cs509-democracy.s3.amazonaws.com/";

var retrieveCardUrl = apiBaseUrl + "main/retrieve/";

function API_retrieveCard()
{
	var requestUrl = `${retrieveCardUrl}/${editorCardId}`;
	var xhr = new XMLHttpRequest();
	xhr.open("GET", requestUrl, true);
	xhr.send();
	console.log("API - RETRIEVE_CARD: Sent request");
	xhr.onloadend = function()
	{
		if (xhr.readyState == XMLHttpRequest.DONE)
		{
			console.log("API - RETRIEVE_CARD: Received response");
			var requestResponse = JSON.parse();
		}
	}
}

/*
 * 		FACE DISPLAY AND BUTTONS
 */

var currFaceIndex = 0;
var faceIndexDict = {
	0 : "Front",
	1 : "Left",
	2 : "Right",
	3 : "Back"
};

function moveCardLeft(condition)
{
	if (condition == true && currFaceIndex > 0)
	{
		currFaceIndex -= 1;		
		refreshCardFacePanel();
	}
	else if (condition == false && currFaceIndex < 3)
	{
		currFaceIndex += 1;
		refreshCardFacePanel();
	}
}

function refreshCardFacePanel()
{
	var currFaceName = document.getElementById("currFaceName");
	currFaceName.innerHTML = "Current Face: " + faceIndexDict[currFaceIndex];	
	
	changeCanvas();
	
	var leftButton = document.getElementById("leftButton");
	var rightButton = document.getElementById("rightButton");
	
	if (currFaceIndex == 0)
	{
		if(!leftButton.classList.contains("hiddenButton"))
		{
			leftButton.classList.add("hiddenButton");
		}
	}
	else
	{
		if(leftButton.classList.contains("hiddenButton"))
		{
			leftButton.classList.remove("hiddenButton");
		}
	}
	
	if (currFaceIndex == 3)
	{
		if(!rightButton.classList.contains("hiddenButton"))
		{
			rightButton.classList.add("hiddenButton");
		}
	}
	else
	{
		if(rightButton.classList.contains("hiddenButton"))
		{
			rightButton.classList.remove("hiddenButton");
		}
	}
}

function changeCanvas()
{
	var frontCanvas = document.getElementById("frontCanvas");
	var leftCanvas = document.getElementById("leftCanvas");
	var rightCanvas = document.getElementById("rightCanvas");
	var backCanvas = document.getElementById("backCanvas");
	canvases = [frontCanvas, leftCanvas, rightCanvas, backCanvas];
	for (var i = 0; i < 4; i += 1)
	{
		if (i == currFaceIndex) 
		{
			canvases[i].style.display = "block";
		}
		else
		{
			canvases[i].style.display = "none";
		}
	}
}

/*
 * 		CANVAS DRAWING
 */
