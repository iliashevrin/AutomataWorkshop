/**
 * 
 */

var chosenNode = "";
var chosenEdge = "";
var chosenSet = "";
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
		//$(chosenNode).parent().find("ellipse").removeClass("chosen");
		//chosenNode = "";
		
		var json = 'method=deletePair&pair=' + title;
		ajaxAction(json);
	} else {
		$("#warning").text("Please choose a G-R pair to delete");
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
			$("#info").show();
			$("#warning").hide();
			
			tempStateFromName = fromName;
			//$(chosenNode).parent().find("ellipse").removeClass("chosen");
			//chosenNode = "";
			
		} else {
			$("#warning").text("Please choose a state first");
			$("#warning").show();
			$("#info").hide();
		}
	}
}

function completeAddToPair() {
	
	var set = $(chosenSet).parent().find("title").text();
	//$(chosenNode).parent().find("ellipse").removeClass("chosen");
	//chosenNode = "";
	
	var json = 'method=addToPair&set=' + set + '&state=' + tempStateFromName;
	ajaxAction(json);
}

function removeFromPair() {
	
	var set = $(chosenSet).parent().find("title").text();
	var state = $(chosenSet).text();
	
	if (!(state[0] == "R" || state[0] == "G")) {
		var json = 'method=removeFromPair&set=' + set + '&state=' + state;
		ajaxAction(json);
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
			$("#info").show();
			$("#warning").hide();
			
			tempStateFromName = fromName;
			$(chosenNode).parent().find("ellipse").removeClass("chosen");
			chosenNode = "";
			
		} else {
			$("#warning").text("Please choose a source state first");
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

function example(isNba) {
	
	var exampleNum = $("#sel2 option:selected").index();
	var json = 'method=example&num=' + exampleNum + '&nba=' + isNba;
	ajaxAction(json);
}

function ajaxAction(data) {
	
    $.ajax({
    	url: "GetGraph",
    	data: data,
    	type: "POST",
    	success: function(result){
    		
	    	$("#solid").html(result);
	    	if (!dnaMode) {
	    		adjustFunctionality();
	    	}
    		adjustScale(0.66, $('.graph'));
    		clearMessages();
    	    if (dnaMode) {
    	    	adjustTooltip();
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
		    adjustScale(1, response.find(".graph"));
		    return $('<div>').append(response).clone().html();
		}
	});
}

function clearMessages() {
	
    // Restore normal mode
    createTransMode = false;
    addToPairMode = false;
    $("#warning").hide();
    $("#info").hide();
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
	
	if (text == chosenEdge) {
		path.removeClass("chosen");
		polygon.removeClass("chosen");
		chosenEdge = "";
	} else {
		path.addClass("chosen");
		polygon.addClass("chosen");
		$(chosenEdge).parent().find("path").removeClass("chosen");
		$(chosenEdge).parent().find("polygon").removeClass("chosen");
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
		
		chosenSet = text;
		
		//if (text == chosenSet) {
			//ellipse.removeClass("chosen");
		//	chosenSet = "";
		//} else {
			//ellipse.addClass("chosen");
			//$(chosenNode).parent().find("ellipse").removeClass("chosen");
		//	chosenSet = text;
			
			if (addToPairMode) {
				completeAddToPair();
			}	
		//}
	}

	

}

function buildDNA() {
	
	// A way to test if there are starting and accepting states
	if ($("text:contains('start')").size() > 0 &&
			$("ellipse+ellipse").size() > 0) {
		
		$(".container button, .container input, .container select").prop( "disabled", true );
		$("#reset").prop( "disabled", false );

		dnaMode = true;
		
		var json = 'method=buildDNA';
		ajaxAction(json);
		
	} else {
		$("#warning").text("Please make sure that graph contains at least one" +
				" starting state and at least one accepting state");
		$("#warning").show();
	}
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
}