package graphviz;

import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.helpers.NotIdentifiableEventImpl;

import dataobjects.DNA;
import dataobjects.DNAState;
import dataobjects.DNATreeNode;
import dataobjects.NBA;
import dataobjects.NBAState;
import dataobjects.StateLetterPair;

public class GraphVizHelper {

	
	public static NBA NBAFromGraphViz(String graphViz) {
		return null;
	}
	
	public static String graphVizFromNBA(NBA nba) {
		StringBuilder text = new StringBuilder();
		
		text.append("node [shape = doublecircle]; ");
		for (NBAState q : nba.getAcceptingStates()) {
			text.append(q.getName());
			text.append(" ");
		}
		text.append(System.getProperty("line.separator"));
		text.append("node [shape = circle]; ");
		for (NBAState q : nba.getStates()) {
			text.append(q.getName());
			text.append(" ");
		}
		text.append(System.getProperty("line.separator"));
		text.append("node [style = invisible]; ");
		text.append(System.getProperty("line.separator"));
		for (NBAState q : nba.getStartingStates()) {
			int number = Integer.parseInt(q.getName().split("q")[1]);
			text.append(number);
			text.append(" -> ");
			text.append(q.getName());
			text.append("[label=\"start\"]");
			text.append(System.getProperty("line.separator"));
		}
		
		TreeSet<NBAState> sortedStates = new TreeSet<NBAState>(nba.getStates());
		for (NBAState q : sortedStates){
        	
        	text.append(q.getName() + " [label=\"");
        	//if (q.isStarting()){
        	//	text.append("*");
        	//}
        	text.append(q.getName());
        	//if (q.isAccepting()){
        	//	text.append("$");
        	//}
    		text.append("\"]");
    		text.append(System.getProperty("line.separator"));
        }
        for (NBAState q : sortedStates){
        	
        	Set<NBAState> endState;
        	
        	for (Character c : nba.getAlphabet()) {
        		endState = nba.getTransition(q, c);
        		for (NBAState st : endState){
        			text.append(q.getName());
            		text.append(" -> ");
            		text.append(st.getName());
            		text.append(" [label=");
            		text.append(c.toString());
            		text.append("]");
            		text.append(System.getProperty("line.separator"));
        		}
        	}
        	
        }
		return text.toString();

	}
	
	public static DNA DNAFromGraphViz(String graphViz) {
		return null;
	}
	
	public static String graphVizFromDNA(DNA dna) {
		int n = dna.k / 2;
		StringBuilder text = new StringBuilder();
		
		
//		text.append("node [shape = circle]; ");
//		for (DNAState q : dna.getStates()) {
//			text.append(q.getName());
//			text.append(" ");
//		}
//		text.append(System.getProperty("line.separator"));
//		text.append("node [style = invisible]; ");
//		text.append(System.getProperty("line.separator"));
//		for (DNAState q : dna.getStartingStates()) {
//			int number = Integer.parseInt(q.getName().split("Q")[1]);
//			text.append(number);
//			text.append(" -> ");
//			text.append(q.getName());
//			text.append("[label=\"start\"]");
//			text.append(System.getProperty("line.separator"));
//		}
		
		TreeSet<DNAState> sortedStates = new TreeSet<DNAState>(dna.getStates());
        for (DNAState q : sortedStates){
        	
        		
//        	// For GUI version
//        	text.append(q.getName() + " [label=\"");
//        	text.append(q.getName());
//        	text.append("\"]");
        	
        	
        	text.append(q.getName() + " [label=\"[");
        	for (int i = 1; i < n-1; i++){
        		text.append(q.getDNATree()[i].getParent());
        		text.append(" ");
        	}
        	text.append(q.getDNATree()[n-1].getParent());
        	text.append("],[");
        	for (int i = 0; i < q.getUniquenessArray().length - 1; i++){
        		if (q.getUniquenessArray()[i] == -1){
        			text.append("$");
        		}
        		else {
        			text.append(q.getUniquenessArray()[i]);
        		}
        		text.append(" ");
        	}
        	if (q.getUniquenessArray()[q.getUniquenessArray().length - 1] == -1){
    			text.append("$");
    		}
    		else {
    			text.append(q.getUniquenessArray()[q.getUniquenessArray().length - 1]);
    		}
    		text.append("]\"]");
    		

    		text.append(System.getProperty("line.separator"));
        }
        for (DNAState q : sortedStates){
        	
        	Set<DNAState> endState;
        	TreeSet<StateLetterPair<DNAState>> possibleStates = new TreeSet<StateLetterPair<DNAState>>();
        	
        	for (Character c : dna.getAlphabet()) {
        		endState = dna.getTransition(q, c);
        		possibleStates.add(new StateLetterPair<DNAState>(endState.iterator().next(), c));
        	}
        	
        	for (StateLetterPair<DNAState> stateLetter : possibleStates) {
        		text.append(q.getName());
        		text.append(" -> ");
        		text.append(stateLetter.getState().getName());
        		text.append(" [label=\"");
        		text.append(stateLetter.getLetter());
        		text.append("[");
        		text.append(dna.getNumber(q, stateLetter.getLetter()));
        		text.append("]\"]");
        		text.append(System.getProperty("line.separator"));
        	}
        	
        }
		return text.toString();

	}
	
	public static String graphVizFromDNAState(DNAState dnaState) {
		
		StringBuilder sb = new StringBuilder();
		boolean isEliminated = false;
		DNATreeNode currNode = null;
		
		for (int i = 0; i < dnaState.getMaxTreeSize() && !isEliminated; i++) {
			
			currNode = dnaState.getDNATree()[i];
			
			if (currNode.getParent() == 0 && currNode.getStates().size() == 0) {
				isEliminated = true;
			} else {
				sb.append(i);
				sb.append(" [label=\"");
				sb.append(i);
				sb.append(": {");
				
				for (NBAState state : currNode.getStates()) {
					sb.append(state.getName());
					sb.append(", ");
				}
				
				if (currNode.getStates().size() > 0) {
					sb.setLength(sb.length() - 2);
				}
				
				sb.append("}\"]");
				sb.append(System.getProperty("line.separator"));
				
				if (currNode.getParent() != -1) {
					sb.append(currNode.getParent());
					sb.append(" -> ");
					sb.append(i);
					sb.append(System.getProperty("line.separator"));
				}
			}
		}
		
		return sb.toString();
	}
}
