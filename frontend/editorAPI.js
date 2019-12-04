var apiBaseUrl = "https://yvmlvrpr1m.execute-api.us-east-1.amazonaws.com/alpha/"; 
var htmlBaseUrl = "https://cs509-democracy.s3.amazonaws.com/";

var retrieveCardUrl 	= apiBaseUrl + "main/retrieve";
var retrieveImagesUrl 	= apiBaseUrl + "editor/list/images";
var addTextBoxUrl		= apiBaseUrl + "editor/newtextbox";
var addImageUrl			= apiBaseUrl + "editor/newimage";
var deleteVisualUrl		= apiBaseUrl + "editor/delete";

var layoutId = null;
var faceNumberToFaceId = [];
var faceIdToVisualElements = {};
var faceIdToCanvasName = {};

var currFaceIndex = 0;
var selectedVisualId = null;
var faceIndexDict = {};

var bounds = {};
var fonts = {};
var layouts = {};

/*
 * 		API FUNCTIONS
 */

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
			console.log(xhr.responseText);
			
			var requestResponse = JSON.parse(xhr.responseText);
			
			if (requestResponse["statusCode"] == 200)
			{
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
	
	// Insert fonts into font selection of createTable
	var fontSelectList = document.getElementById("fontChoice");
	var fontSelectList2 = document.getElementById("fontChoiceEditTextBox");
	clearSelectList(fontSelectList);
	var fontSelectOutput = ""
	for (let [fontId, font] of Object.entries(fonts))
	{
		fontSelectOutput += "<option value=\"" + fontId + "\">" + font.name + ", " + font.style + "</option>";
	}
	fontSelectList.innerHTML = fontSelectOutput;
	fontSelectList2.innerHTML = fontSelectOutput;
	
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
}

function API_handleVisualElementResponse()
{
	console.log("Resizing canvases");
	var frontCanvas = document.getElementById("frontCanvas");
	var leftCanvas = document.getElementById("leftCanvas");
	var rightCanvas = document.getElementById("rightCanvas");
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
	
	if (selectedVisualId != null && selectedVisualId == elt.id)
	{
		ctx.strokeRect(elt.x, elt.y, elt.w, elt.h);
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
 * 		BUTTON FUNCTIONS
 */

function clearSelectList(selectList)
{
    for(var i = selectList.options.length - 1 ; i >= 0 ; i--)
    {
        selectList.remove(i);
    }
}

function displayAddVisualForm()
{
	document.getElementById('addVisual').style.display='block';
	
	var xhr = new XMLHttpRequest();
	xhr.open("GET", retrieveImagesUrl, true);
	xhr.send();
	console.log("API - RETRIEVE_IMAGES: Sent request");
	xhr.onloadend = function ()
	{
		if (xhr.readyState == XMLHttpRequest.DONE)
		{
			console.log("API - RETRIEVE_CARD: Received response");
			var requestResponse = parseRequestResponse(xhr.responseText);
			
			if (requestResponse[0] == 200)
			{
				var imageUrls = requestResponse[2]["imageS3URL"];
				
				var imageChoice = document.getElementById("imageChoice");
				var imageChoiceOutput = "";
				for (var i = 0; i < imageUrls.length; i++)
				{
					var imageUrl = imageUrls[i];
					var shortUrl = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
					console.log(shortUrl);
					imageChoiceOutput += "<option value=\"" + imageUrl + "\">" + shortUrl + "</option>";
				}
				imageChoice.innerHTML = imageChoiceOutput;
			}
			else 
			{
				alert("ERROR: " + requestResponse[1]);
			}
		}
	}
}

function changeAddVisualFormView()
{
	var addVisualRadios = document.getElementsByName("visualType");
	var selected = "";
	for (var i = 0; i < addVisualRadios.length; i += 1)
	{
		if (addVisualRadios[i].checked)
		{
			selected = addVisualRadios[i].value;
			break;
		}
	}
	
	var textBoxDiv = document.getElementById("addVisualTextBox");
	var imageDiv = document.getElementById("addVisualImage");
	if (selected == "textbox")
	{
		imageDiv.style.display = "none";
		textBoxDiv.style.display = "block";
	}
	else 
	{
		textBoxDiv.style.display = "none";
		imageDiv.style.display = "block";
	}
}

function resetAddVisualForm()
{
	document.getElementById('addVisual').style.display = 'none';
	document.getElementById("addVisualForm").reset();
	imageBase64 = null;
}

function displayEditVisualForm()
{
	if (selectedVisualId == null) { alert("Please select a visual element first."); return; }
	
	var faceId = faceNumberToFaceId[currFaceIndex];
	var visualElements = faceIdToVisualElements[faceId];	
	var selectedElement = null;
	for (var i = 0; i < visualElements.length; i++)
	{
		if (visualElements[i].id == selectedVisualId)
		{
			selectedElement = visualElements[i];
			break;
		}
	}
	if (selectedElement == null)
	{
		alert("Front-end is not synchronized with database."); return;
	}
	
	console.log(selectedElement);
	
	// If the selected element is a textbox, fill and present the Edit Textbox form
	if (selectedElement.hasOwnProperty('fontId'))
	{
		document.getElementById('textBoxTextEditTextBox').value = selectedElement.content;
		document.getElementById('boundsXEditTextBox').value = selectedElement.x;
		document.getElementById('boundsYEditTextBox').value = selectedElement.y;
		document.getElementById('boundsWEditTextBox').value = selectedElement.w;
		document.getElementById('boundsHEditTextBox').value = selectedElement.h;
		
		var selectedFont = document.getElementById("fontChoiceEditTextBox");
	    selectedFont.value = selectedElement.fontId.toString();
		
		document.getElementById('editTextBox').style.display = 'block';
	}
	else
	{
		document.getElementById('boundsXEditImage').value = selectedElement.x;
		document.getElementById('boundsYEditImage').value = selectedElement.y;
		document.getElementById('boundsWEditImage').value = selectedElement.w;
		document.getElementById('boundsHEditImage').value = selectedElement.h;
		
		document.getElementById('editImage').style.display = 'block';
	}
}

function handleEditImageFormClick(event)
{
	console.log("handleEditImageFormClick");
}
	
function resetEditImageForm()
{
	document.getElementById('editImage').style.display = 'none';
	document.getElementById('editImageForm').reset();
}

function handleEditTextBoxFormClick(event)
{
	console.log("handleEditTextBoxFormClick");
}

function resetEditTextBoxForm()
{
	document.getElementById('editTextBox').style.display = 'none';
	document.getElementById('editTextBoxForm').reset();
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

var imageBase64 = null;

function readImageFile()
{
	var acceptedExtensions = [".png", ".jpg", ".jpeg"];
	
	var fileUpload = document.getElementById("imageUpload");
	
	if (fileUpload.files && fileUpload.files[0]) 
	{    
		var fileName = fileUpload.files[0].name;
		var acceptable = false;
		for (var i = 0; i < acceptedExtensions.length; i++)
		{
			if (fileName.endsWith(acceptedExtensions[i]))
			{
				acceptable = true;
				break;
			}	
		}
		if (!acceptable) 
		{ 
			alert("Acceptable file extensions are: .jpg, .jpeg, .png."); 
			fileUpload.value = null; 
			return;
		}
		
		// Load file into memory
		var reader = new FileReader();
	    reader.addEventListener("load", function(e) 
	    {
	    	// e.target.result is of the form "data:img/png;base64,STARTDATAHERE"
	    	var idx = e.target.result.indexOf("base64,");
	    	imageBase64 = e.target.result.substring(idx + 7);
	    	console.log(imageBase64);
	    }); 
	    reader.readAsDataURL(fileUpload.files[0]);
    }
}

function handleAddVisualFormClick(event)
{
	// Validate that the fields are properly filled out
	var x = document.getElementById("boundsX").value;
	var y = document.getElementById("boundsY").value;
	var w = document.getElementById("boundsW").value;
	var h = document.getElementById("boundsH").value;
	
	if (x == "" || y == "" || w == "" || h == "")
	{
		return;
	}
	
	var visualTypes = document.getElementsByName("visualType"); 
	var selection = "";
     
	for(var i = 0; i < visualTypes.length; i++) 
	{ 
		if(visualTypes[i].checked) 
        {
			selection = visualTypes[i].value;
        }
    } 
	
	if (selection == "textbox")
	{
		// We are creating a textbox
		var text = document.getElementById("textBoxText").value;
		if (text == "") { return; }
		
		var fontSelect = document.getElementById("fontChoice");
		var fontId = fontSelect.options[fontSelect.selectedIndex].value;
		
		var data = {};
		data["cardId"] = editorCardId.toString();
		data["faceId"] = faceNumberToFaceId[currFaceIndex].toString();
		data["text"] = text;
		data["fontId"] = fontId.toString();
		data["x"] = x;
		data["y"] = y;
		data["width"] = w;
		data["height"] = h;
		
		var jsonString = JSON.stringify(data);
		console.log(jsonString);
	
		// Send request
		var xhr = new XMLHttpRequest();
		xhr.open("POST", addTextBoxUrl, true);
		xhr.send(jsonString);
		console.log("API - CREATE TEXTBOX: Sent request");
		xhr.onloadend = function ()
		{
			if (xhr.readyState == XMLHttpRequest.DONE)
			{
				console.log("API - CREATE TEXTBOX: Received response");
			    console.log(xhr.responseText);
			    var requestResponse = parseRequestResponse(xhr.responseText);
			    if (requestResponse[0] == 200)
			    {	
			    	API_parseVisualElementResponse(requestResponse[2]);
					API_handleVisualElementResponse();
					refreshCardFacePanel();
					
					resetAddVisualForm();
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
	else if (selection == "image")
	{	
		var data = {};
		data["cardId"] = editorCardId.toString();
		data["faceId"] = faceNumberToFaceId[currFaceIndex].toString();
		
		if (imageBase64 != null)
		{
			var newImageName = document.getElementById("imageUploadName").value;
			if (!newImageName)
			{
				alert("Please provide a name for the image to upload.");
				return;
			}
			data["imageName"] = newImageName;
			data["image"] = imageBase64;
		}
		else 
		{
			// Get data["image"] as selection from select input
			var imageChoice = document.getElementById("imageChoice");
			data["image"] = imageChoice.options[imageChoice.selectedIndex].value;
			data["imageName"] = "";
		}
		
		console.log(data);
		return;
		
		var jsonString = JSON.stringify(data);
		console.log(jsonString);
	
		// Send request
		var xhr = new XMLHttpRequest();
		xhr.open("POST", addImageUrl, true);
		xhr.send(jsonString);
		console.log("API - CREATE IMAGE: Sent request");
		xhr.onloadend = function ()
		{
			if (xhr.readyState == XMLHttpRequest.DONE)
			{

			}
		}
	}
}

function handleDeleteVisualClick()
{
	if (selectedVisualId == null)
	{
		alert("Please select a visual element to delete.");
	} 
	else if(confirm("Do you want to delete this visual element?"))
	{
		processDeleteVisual(selectedVisualId);
	}
}

function processDeleteVisual(visualId)
{
	var requestUrl = `${deleteVisualUrl}/${visualId}`;
	var xhr = new XMLHttpRequest();
	xhr.open("GET", requestUrl, true);
	xhr.send();
	console.log("API - DELETE_VISUAL: Sent request");
	xhr.onloadend = function()
	{
		if (xhr.readyState == XMLHttpRequest.DONE)
		{
			console.log("API - DELETE_VISUAL: Received response");
			console.log(xhr.responseText);
			
			var requestResponse = JSON.parse(xhr.responseText);
			
			if (requestResponse["statusCode"] == 200)
			{
				console.log(requestResponse);
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