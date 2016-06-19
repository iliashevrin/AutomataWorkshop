package servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dataobjects.NBAtoDNA;
import dataobjects.NSAtoDNA;
import graphviz.GraphViz;
import StepByStep.NbaSteps;
import NonEmptiness.NonEmpty;

/**
 * Servlet implementation class TestServlet
 */
//@WebServlet(name="GetGraph", urlPatterns={"/GetGraph"})
//@WebServlet("/GetGraph")
public class GetGraph extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String TYPE = "svg";
	private static final String REP_TYPE = "dot";

	private int stateNumber = 0;
	private int grNumber = 0;
	
	private boolean isNba = true;
	
	private String states;
	private String transitions;
	private String grPairs;
	private String dnaStates;
	private String dnaTransitions;
	private NbaSteps NbaStepGen=null;//an object for nba_steps
	
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetGraph() {
		super();
		resetGraph();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String type = request.getParameter("type");
		String method = request.getParameter("method");

        if ("checkEmptiness".equals(method)) {
            String text = NonEmpty.checkEmptiness(dnaStates + dnaTransitions);
			response.setContentType("text/plain");
			response.getWriter().write(text);
            return;
        }

		if ("checkStep".equals(method)) {
			String text = "#####";
			//Is this the first step?
			text += (NSAtoDNA.curr_step == 0) ? "1" : "0";
			//Is this the last step?
			text += (NSAtoDNA.curr_step == NSAtoDNA.num_steps-1) ? "1" : "0";
			text += (NSAtoDNA.curr_step + 1) + "," + NSAtoDNA.num_steps;
			response.setContentType("text/plain");
			response.getWriter().write(text);
		} else {
			
			if ("nba".equals(type)) {
				isNba = true;
			} else if ("nsa".equals(type)) {
				isNba = false;
			}
			
			if (method == null) {
				return;
			}
			
			String parseString = "";
			if ("buildDNA".equals(method)) {
				
				// buildDNA is the method that takes a nba/nsa string in graph viz format
				// and outputs a dna string in graph viz format
				// Replace it if necessary with your implementation
				
		
				String nbaGraphVizString = states + transitions;
				String dnaGraphVizString;
				
				if (!isNba) {
					nbaGraphVizString += grPairs;
					
					// NSA to DNA
					dnaGraphVizString = NSAtoDNA.buildDNA(nbaGraphVizString);
				} else {
					
					// NBA to DNA
					dnaGraphVizString = NBAtoDNA.buildDNA(nbaGraphVizString);
				}
				
				loadDNA(dnaGraphVizString);
				parseString = buildDNAGUIString();
					
			} else if ("firstStep".equals(method)) {
				
				loadDNA(NSAtoDNA.firstStep());
				parseString = buildDNAGUIString();
				
			} else if ("prevStep".equals(method)) {
	
				loadDNA(NSAtoDNA.prevStep());
				parseString = buildDNAGUIString();
				
			} else if ("nextStep".equals(method)) {
	
				loadDNA(NSAtoDNA.nextStep());
				parseString = buildDNAGUIString();
				
			} else if ("lastStep".equals(method)) {
	
				loadDNA(NSAtoDNA.lastStep());
				parseString = buildDNAGUIString();
				
			} else if ("showInnerGraph".equals(method)) {
				if (NbaStepGen!=null){
					//nba steps is active. graphviz is made independently
					parseString=NbaStepGen.ShowInnerGraph(request.getParameter("state"));
				}else{
					parseString = showInnerGraph(request);
				}
				
			}else if ("smallStep".equals(method)) {
				//nba small step. graphviz is made independently
				if (NbaStepGen == null){
					NbaStepGen = new StepByStep.NbaSteps(states + transitions);
				}								
				NbaStepGen.makeNextStep();
				parseString = NbaStepGen.BuildDnaGUIString();
				
			}else if ("bigStep".equals(method)) {
				//nba big step. graphviz is made independently
				if (NbaStepGen == null){
					NbaStepGen = new StepByStep.NbaSteps(states + transitions);
				}				
				NbaStepGen.makeNextTrans();
				parseString = NbaStepGen.BuildDnaGUIString();
			}
			
			else {
				
				if ("createState".equals(method)) {
					
					createState(request);
					
				} else if ("deleteState".equals(method)) {
					
					deleteState(request);
					
				} else if ("createTrans".equals(method)) {
					
					createTrans(request);
					
				} else if ("deleteTrans".equals(method)) {
					
					deleteTrans(request);
				
				} else if ("reset".equals(method)) {
					
					resetGraph();
					
				} else if ("example".equals(method)) {
					
					loadExample(request);
					
				} else if ("createPair".equals(method)) {
					
					isNba = false;
					createPair(request);
					
				} else if ("deletePair".equals(method)) {
					
					isNba = false;
					deletePair(request);
					
				} else if ("addToPair".equals(method)) {
					
					isNba = false;
					addToPair(request);
					
				} else if ("removeFromPair".equals(method)) {
					
					isNba = false;
					removeFromPair(request);
				}
				
				parseString = buildGUIString();
			}
			GraphViz gv = new GraphViz();
			gv.addln(gv.start_graph());
			Scanner scanner = new Scanner(parseString);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				gv.addln(line);
			}
			scanner.close();
            gv.addln(gv.end_graph());
			byte[] graph = gv.getGraph(gv.getDotSource(), TYPE, REP_TYPE);
			String text = new String(graph, "UTF-8");
			response.setContentType("text/plain");
			response.getWriter().write(text);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private String showInnerGraph(HttpServletRequest request) {
		
		String state = request.getParameter("state");
		String[] lines = dnaStates.split(System.getProperty("line.separator"));
		String innerTree = "";
		
		// Parsing of the inner graph
		
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].contains(state)) {
				innerTree = lines[i].substring(lines[i].indexOf("\"") + 1, lines[i].lastIndexOf("\""));
				break;
			}
		}
		
		String[] arrays = innerTree.split(",");
		String[] parents;
		String[] states;
		String[] annotations = null;
		String[] innerStates;
		
		if (isNba) {
			parents = arrays[0].substring(1, arrays[0].length() - 1).split(" ");
			states = arrays[1].substring(1, arrays[1].length() - 1).split(" ");
			//String parents[] = innerTree.substring(1, innerTree.indexOf("]")).split(" ");
			//String states[] = innerTree.substring(innerTree.indexOf(",") + 2, innerTree.length() - 1).split(" ");
		} else {
			parents = arrays[0].substring(1, arrays[0].length() - 1).split(" ");
			states = arrays[1].substring(1, arrays[1].length() - 1).split(" ");
			annotations = arrays[2].substring(1, arrays[2].length() - 1).split(" ");
		}
		
		innerStates = new String[parents.length + 1];
		Arrays.fill(innerStates, "");
		
		for (int i = 0; i < states.length; i++) {
			if (!states[i].equals("$")) {
				int node = Integer.parseInt(states[i]);
				innerStates[node] += "q" + i + ", ";
				
				while (node != 0) {
					node = Integer.parseInt(parents[node - 1]);
					innerStates[node] += "q" + i + ", ";
				}
			}
		}
		
		for (int i = 0; i < innerStates.length; i++) {
			if (!innerStates[i].equals("")) {
				innerStates[i] = innerStates[i].substring(0, innerStates[i].length() - 2);
			}
		}
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < innerStates.length; i++) {
			if (!innerStates[i].equals("")) {
				sb.append(i);
				sb.append(" [label=\"");
				sb.append(i);
				if (!isNba) {
					sb.append(", (");
					sb.append(annotations[i]);
					sb.append(")");
				}
				sb.append(": {");
				sb.append(innerStates[i]);
				sb.append("}\"]");
				sb.append(System.getProperty("line.separator"));
			}
		}
		
		for (int i = 0; i < parents.length; i++) {
			if (!innerStates[i + 1].equals("")) {
				sb.append(parents[i]);
				sb.append(" -> ");
				sb.append(i + 1);
				sb.append("[dir = none]");
				sb.append(System.getProperty("line.separator"));
			}
		}
		
		return sb.toString();
	}
	
	private void createState(HttpServletRequest request) {
		
		boolean isAccepting = Boolean.valueOf(request.getParameter("accepting"));
		boolean isStarting = Boolean.valueOf(request.getParameter("starting"));
		
		StringBuilder state = new StringBuilder();
		state.append("q");
		state.append(stateNumber);
		state.append(" [label=\"");
		if (isStarting){
			state.append("*");
        }
		state.append("q");
		state.append(stateNumber);
        if (isAccepting){
        	state.append("$");
        }
    	state.append("\"]");
    	state.append(System.getProperty("line.separator"));
    	states += state.toString();
    	
    	stateNumber++;
	}
	
	private void deleteState(HttpServletRequest request) {
		
		String state = request.getParameter("state");
		int num = Integer.parseInt(state.split("q")[1]);
		stateNumber--;
		
		String[] lines = states.split(System.getProperty("line.separator"));
		
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].contains(state)) {
				states = states.replace(lines[i] + System.getProperty("line.separator"), "");
			}
		}
		
		lines = transitions.split(System.getProperty("line.separator"));
		
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].contains(state)) {
				transitions = transitions.replace(lines[i] + System.getProperty("line.separator"), "");
			}
		}
		
		lines = grPairs.split(System.getProperty("line.separator"));

		
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].contains(" " + num)) {
				String removed = lines[i].replace(" " + num, "");
				grPairs = grPairs.replace(lines[i], removed);
			}
		}
		
		// Update states and transitions to keep graph packed
		states = states.replace("q" + stateNumber, "q" + num);
		transitions = transitions.replace("q" + stateNumber, "q" + num);
	}

	
	private void createTrans(HttpServletRequest request) {
		
		Character letter = request.getParameter("letter").toCharArray()[0];
		String from = request.getParameter("from");
		String to = request.getParameter("to");
		
		StringBuilder trans = new StringBuilder();
		trans.append(from);
		trans.append(" -> ");
		trans.append(to);
		trans.append(" [label=");
		trans.append(letter);
		trans.append("]");
		trans.append(System.getProperty("line.separator"));
		String transString = trans.toString();
		
		if (transitions.indexOf(transString) < 0) {
			transitions += transString;
		}
	}
	
	private void deleteTrans(HttpServletRequest request) {
		
		Character letter = request.getParameter("letter").toCharArray()[0];
		String transString = request.getParameter("trans");
		
		String[] fromTo = transString.split("->");

		StringBuilder trans = new StringBuilder();
		trans.append(fromTo[0]);
		trans.append(" -> ");
		trans.append(fromTo[1]);
		trans.append(" [label=");
		trans.append(letter);
		trans.append("]");
		trans.append(System.getProperty("line.separator"));
		
		transitions = transitions.replace(trans.toString(), "");
	}
	
	private void resetGraph() {

		stateNumber = 0;
		grNumber = 0;
		states = "";
		transitions = "";
		dnaStates = "";
		dnaTransitions = "";
		grPairs = "";
		NbaStepGen=null;
	}
	
	private void loadExample(HttpServletRequest request) {
		
		int num = Integer.parseInt(request.getParameter("num"));
		
		if (isNba) {
			
			String nba = Examples.NBA_EXAMPLES[num];
		
			int divide = nba.substring(0, nba.indexOf("->")).lastIndexOf(System.getProperty("line.separator"));
			states = nba.substring(0, divide + System.getProperty("line.separator").length());
			transitions = nba.substring(divide + System.getProperty("line.separator").length(), nba.length());
		
			stateNumber = states.split(System.getProperty("line.separator")).length;
		} else {
			String nsa = Examples.NSA_EXAMPLES[num];
			
			// Divide states and rest
			int divide1 = nsa.substring(0, nsa.indexOf("->")).lastIndexOf(System.getProperty("line.separator"));
			int divide2 = nsa.substring(0, nsa.indexOf("_")).lastIndexOf(System.getProperty("line.separator"));
			states = nsa.substring(0, divide1 + System.getProperty("line.separator").length());
			transitions = nsa.substring(divide1 + System.getProperty("line.separator").length(), divide2 + System.getProperty("line.separator").length());
			grPairs = nsa.substring(divide2 + System.getProperty("line.separator").length(), nsa.length());
			
			stateNumber = states.split(System.getProperty("line.separator")).length;
			grNumber = grPairs.split(System.getProperty("line.separator")).length / 2;
		}
	}
	
	private void createPair(HttpServletRequest request) {
		
		StringBuilder pair = new StringBuilder();
		
		pair.append("G_");
		pair.append(grNumber);
		pair.append(System.getProperty("line.separator"));
		pair.append("R_");
		pair.append(grNumber);
		pair.append(System.getProperty("line.separator"));
		
		grNumber++;
		
		grPairs += pair.toString();
	}
	
	private void deletePair(HttpServletRequest request) {
		
		String pair = request.getParameter("pair");
		
		int num = Integer.parseInt(pair.split("(R|G)_")[1]);
		grNumber--;
		
		String[] lines = grPairs.split(System.getProperty("line.separator"));
		
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].contains(pair)) {
				
				if (pair.contains("G")) {
					
					grPairs = grPairs.replace(lines[i] + System.getProperty("line.separator") + 
						lines[i+1] + System.getProperty("line.separator"), "");
					
				} else if (pair.contains("R")) {
					
					grPairs = grPairs.replace(lines[i-1] + System.getProperty("line.separator") + 
						lines[i] + System.getProperty("line.separator"), "");
				}

			}
		}
		
		// Update pairs string to keep packed
		grPairs = grPairs.replace("R_" + grNumber, "R_" + num);
		grPairs = grPairs.replace("G_" + grNumber, "G_" + num);
	}
	
	
	private void addToPair(HttpServletRequest request) {
		
		String set = request.getParameter("set");
		String state = request.getParameter("state");
		String num = state.split("q")[1];
		
		String[] lines = grPairs.split(System.getProperty("line.separator"));
		
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].contains(set)) {
				
				if (!lines[i].contains(" " + num)) {
					
					grPairs = grPairs.replace(lines[i], lines[i] + " " + num);
				}
			}
		}
	}
	
	
	private void removeFromPair(HttpServletRequest request) {
		
		String set = request.getParameter("set");
		String state = request.getParameter("state");
		String num = state.split("q")[1];
		
		String[] lines = grPairs.split(System.getProperty("line.separator"));
		
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].contains(set)) {
				
				if (lines[i].contains(" " + num)) {
					
					String removed = lines[i].replace(" " + num, "");
					grPairs = grPairs.replace(lines[i], removed);
				}
			}
		}
	}
	
	
	private void loadDNA(String dna) {
		dna = dna.trim(); // Safety measures
		int ind_arrow = dna.indexOf("->");
		if (ind_arrow == -1) {
			dnaStates = dna;
			dnaTransitions = "";
		} else {
			int divide = dna.substring(0, ind_arrow).lastIndexOf(System.getProperty("line.separator"));
			dnaStates = dna.substring(0, divide + System.getProperty("line.separator").length());
			dnaTransitions = dna.substring(divide + System.getProperty("line.separator").length(), dna.length());
		}
		
//		System.out.println("After the parsing of DNA");
//		System.out.println("Dna: " + dna);
//		System.out.println("States: " + dnaStates);
//		System.out.println("Trans: " + dnaTransitions);
	}

	private String buildGUIString() {
		
		// No states and no transitions
		//if (states.equals("")) {
		//	return "";
		//}
		
		StringBuilder accepting = new StringBuilder();
		StringBuilder starting = new StringBuilder();
		StringBuilder startingLines = new StringBuilder();
		StringBuilder regular = new StringBuilder();
		StringBuilder pairs = new StringBuilder();
		StringBuilder pairNodes = new StringBuilder();
		
		accepting.append("node [shape = doublecircle]; ");
		starting.append("node [fixedsize = true, width = 0, height = 0, style = invisible]; ");
		pairs.append("node [shape = Mrecord, style=filled, fillcolor=white]; ");
		regular.append("node [shape = circle]; ");
		
		String stateName = "";
		
		String[] lines = states.split(System.getProperty("line.separator"));
		
		if (!(lines.length > 0 && lines[0].equals(""))) {
			for (int i = 0; i < lines.length; i++) {
		
				stateName = lines[i].substring(0, lines[i].indexOf("["));
				
				// State name without the q
				if (lines[i].contains("*")) {
					starting.append(stateName.substring(1));
					
					startingLines.append(stateName.substring(1));
					startingLines.append(" -> ");
					startingLines.append(stateName);
					startingLines.append("[label=\"start\"]");
					startingLines.append(System.getProperty("line.separator"));
				}
				
				if (lines[i].contains("$")) {
					accepting.append(stateName);
				} else {
					regular.append(stateName);
				}
			}
		}

		
		lines = grPairs.split(System.getProperty("line.separator"));
		if (!(lines.length > 0 && lines[0].equals(""))) {
			for (int i = 0; i < lines.length; i++) {
				
				String[] setData = lines[i].split(" ");
				
				String setName = setData[0];
				pairs.append(setName + " ");
				pairNodes.append(setName);
				pairNodes.append(" [label=\"{");
				pairNodes.append(setName);
				pairNodes.append(": ");
				
				for (int j = 1; j < setData.length; j++) {
					pairNodes.append("|q");
					pairNodes.append(setData[j]);
				}
				
				pairNodes.append("}\"]");
				pairNodes.append(System.getProperty("line.separator"));
			}
		}
		
		StringBuilder result = new StringBuilder();
		result.append("rankdir=LR");
		result.append(System.getProperty("line.separator"));
		result.append(regular.toString());
		result.append(System.getProperty("line.separator"));
		result.append(accepting.toString());
		result.append(System.getProperty("line.separator"));
		
		// This is a NSA
		if (!isNba) {
			result.append(pairs.toString());
			result.append(System.getProperty("line.separator"));
		}
		
		result.append(starting.toString());
		result.append(System.getProperty("line.separator"));
		
		// This is a NSA
		if (!isNba) {
			result.append(pairNodes.toString());
		}
		
		result.append(states.replace("*", "").replace("$", ""));
		result.append(transitions);
		result.append(startingLines.toString());
		
		return result.toString();
	}
	
	private String buildDNAGUIString() {
		
		StringBuilder graphViz = new StringBuilder();
		
		String[] lines = dnaStates.split(System.getProperty("line.separator"));
		
		graphViz.append("rankdir=LR");
		graphViz.append(System.getProperty("line.separator"));
		graphViz.append("node [shape = circle]; ");
		for (int i = 0; i < lines.length; i++) {
			
			graphViz.append("Q");
			graphViz.append(i);
			graphViz.append(" ");
		}
		
		graphViz.append("node [fixedsize = true, width = 0, height = 0, style = invisible]; 0");
		graphViz.append(System.getProperty("line.separator"));
		graphViz.append("0 -> Q0 [label=\"start\"]");
		graphViz.append(System.getProperty("line.separator"));
		
		// State name for DNA GUI
		for (int i = 0; i < lines.length; i++) {
			graphViz.append("Q");
			graphViz.append(i);
			graphViz.append(" [label=\"");
			graphViz.append("Q");
			graphViz.append(i);
			graphViz.append("\"]");
			graphViz.append(System.getProperty("line.separator"));
		}
		
		graphViz.append(dnaTransitions);
		
		return graphViz.toString();
	}
}
