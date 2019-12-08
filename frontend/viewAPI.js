var apiBaseUrl = "https://yvmlvrpr1m.execute-api.us-east-1.amazonaws.com/alpha/"; 
var htmlBaseUrl = "https://cs509-democracy.s3.amazonaws.com/";

var retrieveCardUrl 	= apiBaseUrl + "main/retrieve";

var layoutId = null;
var faceNumberToFaceId = [];
var faceIdToVisualElements = {};
var faceIdToCanvasName = {};

var currFaceIndex = 0;
var faceIndexDict = {};

var bounds = {};
var fonts = {};
var layouts = {};

var globalCardId = null;
/*
 * 		API FUNCTIONS
 */

function API_retrieveCard()
{
	var requestUrl = `${retrieveCardUrl}/${cardId}`;
	console.log(requestUrl);
	var xhr = new XMLHttpRequest();
	xhr.open("GET", requestUrl, true);
	xhr.send();
	console.log("API - RETRIEVE_CARD: Sent request");
	xhr.onloadend = function()
	{
		if (xhr.readyState == XMLHttpRequest.DONE)
		{
			console.log("API - RETRIEVE_CARD: Received response");
			console.log(xhr.responseText);
			
			var requestResponse = JSON.parse(xhr.responseText);
			
			if (requestResponse["statusCode"] == 200)
			{
				console.log(requestResponse["response"]);
				API_parseVisualElementResponse(requestResponse["response"]);
				API_handleVisualElementResponse();
				refreshCardFacePanel();
			}
			else
			{
				alert(requestResponse["errorMessage"]);
			}
		}
	}
}

function API_parseVisualElementResponse(response)
{
	var canvasNames = ["frontCanvas", "leftCanvas", "rightCanvas", "backCanvas"];
	
	var faces = response["faces"];
	for (var i = 0; i < faces.length; i++)
	{
		faceNumberToFaceId.push(faces[i].id)
		faceIdToVisualElements[faces[i].id] = [];
		faceIdToCanvasName[faces[i].id] = canvasNames[i];
		faceIndexDict[i] = faces[i].faceName;
	}
	
	layoutsJSON = response["layouts"];
	for (var i = 0; i < layoutsJSON.length; i++)
	{
		var layout = layoutsJSON[i];
		layouts[layout.id] = layout.layout;
	}
	layoutId = response["layoutId"];
	
	boundsJSON = response["bounds"];
	for (var i = 0; i < boundsJSON.length; i++)
	{
		var bound = boundsJSON[i];
		bounds[bound.id] = bound;
	}
	
	fontsJSON = response["fonts"];
	for (var i = 0; i < fontsJSON.length; i++)
	{
		var font = fontsJSON[i];
		fonts[font.id] = font;
	}
	
	var textboxes = response["textboxes"];
	for (var i = 0; i < textboxes.length; i++)
	{
		var tb = textboxes[i];
		var t = new Object();
		t.id = tb.id;
		t.cardId = tb.cardId
		t.faceId = tb.faceId;
		t.boundsId = tb.boundId;
		t.content = tb.content;
		t.fontId = tb.fontId;
		
		var bound = bounds[t.boundsId];
		if (t.fontId == undefined) { continue; }
		t.x = bound.x;
		t.y = bound.y;
		t.w = bound.width;
		t.h = bound.height;
		
		faceIdToVisualElements[t.faceId].push(t);
	}
	
	var images = response["images"];
	for (var i = 0; i < images.length; i++)
	{
		var img = images[i];
		var j = new Object();
		j.id = img.id;
		j.cardId = img.cardId;
		j.faceId = img.faceId;
		j.boundsId = img.boundId;
		j.content = img.content;
		
		var bound = bounds[j.boundsId];
		j.x = bound.x;
		j.y = bound.y;
		j.w = bound.width;
		j.h = bound.height;
		
		faceIdToVisualElements[j.faceId].push(j);
	}
}

function API_handleVisualElementResponse()
{
	console.log("Resizing canvases");
	var frontCanvas = document.getElementById("frontCanvas");
	var leftCanvas = document.getElementById("leftCanvas");
	var rightCanvas = document.getElementById("rightCanvas");
	console.log(layoutId);
	console.log(layouts);
	console.log(layouts[layoutId]);
	if (layouts[layoutId] == "Portrait")
	{
		frontCanvas.height = "800";
		leftCanvas.height = "800";
		rightCanvas.height = "800";
		drawCard();
		return;
	} 
	else if (layouts[layoutId] == "Landscape")
	{
		frontCanvas.width = "800";
		leftCanvas.width = "800";
		rightCanvas.width = "800";
		drawCard();
		return;
	}	
	
	alert("ERROR: Invalid layout ID.");
}

/*
 * 		DRAW TO CANVAS
 */

function drawCard()
{
	for (var i = 0; i < 4; i += 1)
	{
		var faceId = faceNumberToFaceId[i];
		drawFace(faceId);
	}
}

function drawFace(faceId)
{
	var elements = faceIdToVisualElements[faceId];
	
	var canvas = document.getElementById(faceIdToCanvasName[faceId]);
	var ctx = canvas.getContext("2d");
	ctx.clearRect(0, 0, canvas.width, canvas.height);
	
	for (var j = 0; j < elements.length; j++)
	{
		var elt = elements[j];
		drawElement(faceId, elt);
	}
}

function drawElement(faceId, elt)
{
	var canvas = document.getElementById(faceIdToCanvasName[faceId]);
	var ctx = canvas.getContext("2d");
	
	if (elt.fontId == undefined)
	{
		var img = new Image();
		img.onload = function()
		{
			ctx.drawImage(img, elt.x, elt.y, elt.w, elt.h);
		}
		img.src = elt.content;
	}
	else
	{
		var fontSize = 30;
		var font = fonts[elt.fontId];
		if (font == undefined) { return; }
		var textFont = font.style + " " + fontSize + "px " + font.name;
		console.log(textFont);
		ctx.font = textFont;

		// Fit to text box height;
		var lineHeight = ctx.measureText("M").width;
		if (lineHeight < elt.h)
		{
			while (lineHeight < elt.h)
			{
				fontSize += 1;
				var textFont = font.style + " " + fontSize + "px " + font.name;
				ctx.font = textFont;
				lineHeight = ctx.measureText("M").width;
			}
		}
		else 
		{
			while (lineHeight > elt.h)
			{
				fontSize -= 1;
				var textFont = font.style + " " + fontSize + "px " + font.name;
				ctx.font = textFont;
				lineHeight = ctx.measureText("M").width;
			}
		}
		ctx.font = textFont;
		ctx.fillText(elt.content, elt.x, elt.y + elt.h, elt.w);
		
	}
}

function toggleVisualSelection(e)
{
	var mouseX = e.layerX;
	var mouseY = e.layerY;
	
	var faceId = faceNumberToFaceId[currFaceIndex];
	
	var visualElements = faceIdToVisualElements[faceId];
	
	var clickedId = null;
	for (var i = 0; i < visualElements.length; i++)
	{
		var elt = visualElements[i];
		if (elt.x < mouseX && mouseX < elt.x + elt.w && elt.y < mouseY && mouseY < elt.y + elt.h)
		{
			clickedId = elt.id;
		}
	}
	
	selectedVisualId = clickedId;
	drawCard();
}

/*
 * 		FACE DISPLAY AND BUTTONS
 */


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