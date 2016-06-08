/**
 * 
 */

var chosenNode = "";
var chosenEdge = "";
var chosenSet = "";
var chosenSetElem = "";
var createTransMode = false;
var addToPairMode = false;
var tempStateFromName = "";
var dnaMode = false;


$(function() {
    reset();
});

function createPair() {
	
	var json = 'method=createPair';
	ajaxAction(json);
}

function deletePair() {
	
	createTransMode = false;
	addToPairMode = false;
	
	if (chosenSet != "") {
		
		var title = $(chosenSet).parent().find("title").text();
		
		$(chosenSet).parent().find("polygon").removeClass("chosen");
		$(chosenSet).parent().find("path").removeClass("chosen");
		$(chosenSet).parent().find("text").removeClass("chosen");
		chosenSet = "";
		
		var json = 'method=deletePair&pair=' + title;
		ajaxAction(json);
	} else {
		$("#warning").text("Please choose a G-R pair to delete");
		$("#upper").css("height", "210px");
		$("#warning").show();
		$("#info").hide();
	}
}

function addToPair() {
	
	createTransMode = false;
	
	if (addToPairMode == false) {
		
		if (chosenNode != "") {

			addToPairMode = true;
			var fromName = $(chosenNode).parent().find("title").text();
			$("#info").text("Please choose a set to add state " + fromName + " to");
			$("#upper").css("height", "210px");
			$("#info").show();
			$("#warning").hide();
			
			tempStateFromName = fromName;
			
			$(chosenSet).parent().find("polygon").removeClass("chosen");
			$(chosenSet).parent().find("path").removeClass("chosen");
			$(chosenSet).parent().find("text").removeClass("chosen");
			chosenSet = "";
			
		} else {
			$("#warning").text("Please choose a state first");
			$("#upper").css("height", "210px");
			$("#warning").show();
			$("#info").hide();
		}
	}
}

function completeAddToPair() {
	
	var set = $(chosenSet).parent().find("title").text();
	
	$(chosenSet).parent().find("polygon").removeClass("chosen");
	$(chosenSet).parent().find("path").removeClass("chosen");
	$(chosenSet).parent().find("text").removeClass("chosen");
	chosenSet = "";
	
	var json = 'method=addToPair&set=' + set + '&state=' + tempStateFromName;
	ajaxAction(json);
}

function removeFromPair() {
	
	if (chosenSet != "") {

		var set = $(chosenSet).parent().find("title").text();
		var state = $(chosenSet).text();
	
		$(chosenSet).parent().find("polygon").removeClass("chosen");
		$(chosenSet).parent().find("path").removeClass("chosen");
		$(chosenSet).parent().find("text").removeClass("chosen");
		chosenSet = "";
		
		if (!(state[0] == "R" || state[0] == "G")) {
			var json = 'method=removeFromPair&set=' + set + '&state=' + state;
			ajaxAction(json);
		}
	} else {
		$("#warning").text("Please choose a state to remove from a set");
		$("#upper").css("height", "210px");
		$("#warning").show();
		$("#info").hide();
	}
}


function createState() {
	
	var accepting = false;
	var starting = false;
	
	if ($("#accepting:checked").size() > 0) {
		accepting = true;
	}
	
	if ($("#starting:checked").size() > 0) {
		starting = true;
	}
	
	var json = 'method=createState&accepting=' + accepting + '&starting=' + starting;
	ajaxAction(json);
}

function deleteState() {
	
	createTransMode = false;
	addToPairMode = false;
	
	if (chosenNode != "") {
		
		var title = $(chosenNode).parent().find("title").text();
		$(chosenNode).parent().find("ellipse").removeClass("chosen");
		chosenNode = "";
		
		var json = 'method=deleteState&state=' + title;
		ajaxAction(json);
	} else {
		$("#warning").text("Please choose a state to delete");
		$("#upper").css("height", "210px");
		$("#warning").show();
		$("#info").hide();
	}
}

function deleteTrans() {
	
	createTransMode = false;
	addToPairMode = false;
	
	if (chosenEdge != "") {
		
		var letter = $(chosenEdge).text();
		var trans = $(chosenEdge).parent().find("title").text();
		$(chosenEdge).parent().find("path").removeClass("chosen");
		$(chosenEdge).parent().find("polygon").removeClass("chosen");
		chosenEdge = "";
		
		var json = 'method=deleteTrans&letter=' + letter + '&trans=' + trans;
		ajaxAction(json);
	} else {
		$("#warning").text("Please choose a transition to delete");
		$("#upper").css("height", "210px");
		$("#warning").show();
		$("#info").hide();
	}
}

function createTrans() {
	
	addToPairMode = false;
	
	if (createTransMode == false) {
		
		if (chosenNode != "") {

			createTransMode = true;
			var fromName = $(chosenNode).parent().find("title").text();
			$("#info").text("Please choose a destination state to create transition from " + fromName);
			$("#upper").css("height", "210px");
			$("#info").show();
			$("#warning").hide();
			
			tempStateFromName = fromName;
			$(chosenNode).parent().find("ellipse").removeClass("chosen");
			chosenNode = "";
			
		} else {
			$("#warning").text("Please choose a source state first");
			$("#upper").css("height", "210px");
			$("#warning").show();
			$("#info").hide();
		}
	}
}

function completeCreateTrans() {
	
	var letter = $("#sel1 option:selected").text();
	
	var toName = $(chosenNode).parent().find("title").text();
	$(chosenNode).parent().find("ellipse").removeClass("chosen");
	chosenNode = "";
	
	var json = 'method=createTrans&letter=' + letter + '&from=' + tempStateFromName + '&to=' + toName;
	ajaxAction(json);
}

function example() {
	
	var exampleNum = $("#sel2 option:selected").index();
	var json = 'method=example&num=' + exampleNum;
	ajaxAction(json);
}

function ajaxAction(data, build) {
	
	data += "&type=" + $("#type").html();
    
	// Hide tooltips fix
	$("div[role='tooltip']").hide();
	
    $.ajax({
    	url: "GetGraph",
    	data: data,
    	type: "POST",
    	success: function(result){
    		if (result.indexOf("#####") == 0) {
    			$("#first-step, #prev-step, #next-step, #last-step").prop( "disabled", false );
    			result = result.substring(5);
    			if(result.substring(0, 1) === "1") {
    				//We are in first step
    				$("#first-step, #prev-step").prop( "disabled", true );
    			} if(result.substring(1, 2) === "1") {
    				//We are in last step
    				$("#next-step, #last-step").prop( "disabled", true );
    			}
    			result = result.substring(2);
    			var comma = result.indexOf(",");
    			var curr = result.substring(0, comma);
    			var size = result.substring(comma + 1);
    			updateSteps(curr, size);
    		} else {
		    	$("#solid").html(result);
		    	if (!dnaMode) {
		    		adjustFunctionality();
		    	}
	    		adjustScale(0.66, $('.graph'));
	    		clearMessages();
	    	    if (dnaMode) {
	    	    	adjustTooltip();
	    	    }
	    	    if (build) {
	    	    	checkStep();
	    	    	$("#firs-step, #prev-step, #next-step, #last-step").prop( "disabled", false );
	    	    }
    		}
    	}
    });
}

function adjustFunctionality() {
	
    $("text").click( function() {
    	
    	var element = $(this).parent();
    	
    	if (element.hasClass("edge")) {
    		clickEdge(this, element);
    	} else if (element.hasClass("node")) {
    		clickNode(this, element);
    	}
    });
}

function adjustTooltip() {
	
	$(document).tooltip({
		items: "#solid .node text",
        show: null, // show immediately 
        hide: { effect: "" }, //fadeOut
        position: { my: "left center", at: "right center" },
        close: function(event, ui){
            ui.tooltip.hover(
                function () {
                    $(this).stop(true).fadeTo(400, 1); 
                },
                function () {
                    $(this).fadeOut("400", function(){
                        $(this).remove(); 
                    })
                }
            );
        },
		content: function() {
			// Currently use sync ajax call
			var title = $(this).parent().find("title").text();
		    var response = $.ajax({
		    	url: "GetGraph",
		    	data: 'method=showInnerGraph&state=' + title,
		    	async: false,
		    	type: "POST"
		    }).responseText;
		    response = $(response);
		    adjustScale(0.5, response.find(".graph"));
		    var container = $('<div>').append(response);
		    $(container).css("height", 350);
		    $(container).css("width", 280);
		    $(container).css("overflow", "scroll");
		    $(container).css("background", "white");
		    return container;
		}
	});
}

function clearMessages() {
	
    // Restore normal mode
    createTransMode = false;
    addToPairMode = false;
    $("#warning").hide();
    $("#info").hide();
    $("#upper").css("height", "200px");
}

function adjustScale(scale, graph) {
	
    // Adjust scale
	var transform = graph.attr("transform");
    var rotate = transform.indexOf("rotate");
    transform = transform.substr(rotate);
    transform = "scale(" + scale + "," + scale + ") " + transform;
    graph.attr("transform", transform);
    return graph;
}

function clickEdge(text, edge) {
	var path = edge.find("path");
	var polygon = edge.find("polygon");
	var title = edge.find("text");
	
	if (text == chosenEdge) {
		path.removeClass("chosen");
		polygon.removeClass("chosen");
		title.removeClass("chosen");
		chosenEdge = "";
	} else {
		path.addClass("chosen");
		polygon.addClass("chosen");
		title.addClass("chosen");
		$(chosenEdge).parent().find("path").removeClass("chosen");
		$(chosenEdge).parent().find("polygon").removeClass("chosen");
		$(chosenEdge).parent().find("text").removeClass("chosen");
		chosenEdge = text;
	}
}

function clickNode(text, node) {
	var ellipse = node.find("ellipse");
	
	// Regular state
	if (ellipse.size() > 0) {
		
		if (text == chosenNode) {
			ellipse.removeClass("chosen");
			chosenNode = "";
		} else {
			ellipse.addClass("chosen");
			$(chosenNode).parent().find("ellipse").removeClass("chosen");
			chosenNode = text;
			
			if (createTransMode) {
				completeCreateTrans();
			}
		}
		
	} else  {   // NSA set

		var polygon = node.find("polygon");
		var paths = node.find("path");
		
		if (text == chosenSet) {
			polygon.removeClass("chosen");
			paths.removeClass("chosen");
			$(text).removeClass("chosen");
			chosenSet = "";
		} else {

			$(chosenSet).parent().find("polygon").removeClass("chosen");
			$(chosenSet).parent().find("path").removeClass("chosen");
			$(chosenSet).parent().find("text").removeClass("chosen");
			chosenSet = text;
			polygon.addClass("chosen");
			paths.addClass("chosen");
			$(chosenSet).addClass("chosen");
			
			if (addToPairMode) {
				completeAddToPair();
			}
		}
	}

	

}

function buildDNA() {
	
	var type = $("#type").html();
	
	// A way to test if there are starting and accepting states
	if ($("text:contains('start')").size() > 0 && ((type == "nsa") || ((type == "nba") &&
			$("ellipse+ellipse").size() > 0))) {
		
		$(".container button, .container input, .container select").prop( "disabled", true );
		$("#reset").prop( "disabled", false );

		dnaMode = true;
		
		var json = 'method=buildDNA';
		ajaxAction(json, true);
	} else {
		$("#warning").text("Please make sure that graph contains at least one" +
				" starting state and in case of a NBA at least one accepting state");
		$("#upper").css("height", "270px");
		$("#warning").show();
		
	}
}

function bigStep() {
	
	// A way to test if there are starting and accepting states
	if (dnaMode==false) {
		if (!($("text:contains('start')").size() > 0 &&
			$("ellipse+ellipse").size() > 0)) {
			$("#warning").text("Please make sure that graph contains at least one" +
			" starting state and at least one accepting state");
			$("#warning").show();
			return;
		}
		dnaMode=true;
	}
	
	//$(".container button, .container input, .container select").prop( "disabled", true );
	
	$("#reset").prop( "disabled", false );
	$("#small-step").prop( "disabled", false );
	$("#big-step").prop( "disabled", false );
	
	var json = 'method=bigStep';
	ajaxAction(json,false);	
}

function smallStep() {
	
	// A way to test if there are starting and accepting states
	if (dnaMode==false) {
		if (!($("text:contains('start')").size() > 0 &&
			$("ellipse+ellipse").size() > 0)) {
			$("#warning").text("Please make sure that graph contains at least one" +
			" starting state and at least one accepting state");
			$("#warning").show();
			return;
		}
		dnaMode=true;
	}
			
	$(".container button, .container input, .container select").prop( "disabled", true );
	$("#reset").prop( "disabled", false );
	$("#small-step").prop( "disabled", false );
	$("#big-step").prop( "disabled", false );
	
	var json = 'method=smallStep';
	ajaxAction(json,false);	
}


function checkStep() {
	var json = 'method=checkStep';
	ajaxAction(json);
}

function firstStep() {
	var json = 'method=firstStep';
	ajaxAction(json);
	checkStep();
}

function prevStep() {
	var json = 'method=prevStep';
	ajaxAction(json);
	checkStep();
}

function nextStep() {
	var json = 'method=nextStep';
	ajaxAction(json);
	checkStep();
}

function lastStep() {
	var json = 'method=lastStep';
	ajaxAction(json);
	checkStep();
}

function updateSteps(curr, size) {
	//TODO: Implement to change the step-string in the page to <curr>/<size>.
	//Both curr and size are STRINGS!
	
	$("#step-data").html(curr + " / " + size);
}


function reset() {
	$(".container button, .container input, .container select").prop( "disabled", false );

	dnaMode = false;

	$(document).tooltip({
		items: "#solid .node text",
		content: function() {}
	});
	
	var json = 'method=reset';
	ajaxAction(json);
	$("#first-step, #prev-step, #next-step, #last-step").prop( "disabled", true );
	updateSteps("??", "??");
}