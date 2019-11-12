var apiBaseUrl = "https://yvmlvrpr1m.execute-api.us-east-1.amazonaws.com/alpha/"; 
var htmlBaseUrl = "https://cs509-democracy.s3.amazonaws.com/";

var retrieveCardUrl = apiBaseUrl + "main/retrieve/";

var layoutId = 1;
var faceNumberToFaceId = [];
var faceIdToVisualElements = {};
var faceIdToCanvasName = {};

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
	API_handleVisualElementResponse();
}

function API_parseVisualElementResponse()
{
	faceNumberToFaceId = [0, 1, 2, 3];
	faceIdToVisualElements[0] = [];
	faceIdToVisualElements[1] = [];
	faceIdToVisualElements[2] = [];
	faceIdToVisualElements[3] = [];
	
	faceIdToCanvasName[0] = "frontCanvas";
	faceIdToCanvasName[1] = "leftCanvas";
	faceIdToCanvasName[2] = "rightCanvas";
	faceIdToCanvasName[3] = "backCanvas";
	
	faceIdToVisualElements[2].push("Test");
}

function API_handleVisualElementResponse()
{
	console.log("Resizing canvases");
	var frontCanvas = document.getElementById("frontCanvas");
	var leftCanvas = document.getElementById("leftCanvas");
	var rightCanvas = document.getElementById("rightCanvas");
	if (layoutId == 1)
	{
		frontCanvas.height = "800";
		leftCanvas.height = "800";
		rightCanvas.height = "800";
	} 
	else
	{
		frontCanvas.width = "800";
		leftCanvas.width = "800";
		rightCanvas.width = "800";
	}	
	
	console.log("Drawing visual elements to the screen");
	for (var i = 0; i < 4; i += 1)
	{
		var faceId = faceNumberToFaceId[i];
		var elements = faceIdToVisualElements[faceId];
		for (var j = 0; j < elements.length; j++)
		{
			var elt = elements[j];
			drawElement(faceId, elt);
		}
	}
}

function drawElement(faceId, elt)
{
	var canvas = document.getElementById(faceIdToCanvasName[faceId]);
	var ctx = canvas.getContext("2d");
	
	var img = new Image();
	img.onload = function()
	{
		ctx.drawImage(img, 50, 50);
	}
	img.src = htmlBaseUrl + "images/site_test.png";
	
	
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
