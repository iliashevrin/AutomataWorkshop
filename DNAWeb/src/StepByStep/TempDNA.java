package StepByStep;

import java.io.File;
import java.util.*;

/**
 * Created by University on 5/27/2016.
 */
public class TempDNA extends DNA {
    public final int TempIndex=-1;
    public Transition t;
    public DnaState qPrime;
    public int step =0;
    public int lastStep =0;
    public HashMap<Integer, Set<State>> SPrimeMap = new HashMap<>();

    public TempDNA(File f) {
        super(f);
    }

    public TempDNA(NBA b) {
        super(b);
    }

    public TempDNA(String s) {
        String[] lines = s.split(System.lineSeparator());
        HashSet<Character> sigmaSet = new HashSet<>();
        for (int i = 0; i < lines.length; i++) {
            try {
                parseLine(lines[i], sigmaSet);
            } catch (Exception e) {
                System.out.println("line error! line is:" + System.lineSeparator() + lines[i]);
            }
        }
        setSigma(charSetToArray(sigmaSet));
    }

    public TempDNA(Transition t, DnaState qPrime, int step, HashMap<Integer, Set<State>> SPrimeMap) {
        this.t = t;
        this.qPrime = qPrime;
        this.step = step;
        this.SPrimeMap = SPrimeMap;
    }

    public void parseLine(String line, Set<Character> sigmaSet) {
        String l = line.trim().replaceAll("[\"]", "").replaceAll("\\$", "-1");
        if(l.contains(":")) {

        }
        else if (l.contains("@")) {
            l = l.replaceAll("@", "");
            step =Integer.parseInt(l);
        }
        if (l.contains("->")) {//edge
            String[] parts = l.split("\\[label=");
            String[] first_part_split = parts[0].split(" -> ");
            int from_index = Integer.parseInt(first_part_split[0].replaceAll("[Q ]", ""));
            int to_index = Integer.parseInt(first_part_split[1].replaceAll("[Q ]", ""));
            Integer numbering = Integer.parseInt(parts[1].substring(parts[1].indexOf("[") + 1, parts[1].indexOf("]")));
            char sigma = parts[1].charAt(0);
            DnaState dnastate1 = getStateByIndex(from_index);
            Transition trans = new Transition(dnastate1, sigma);
            t.setNumber(numbering);
            if (numbering==TempIndex){
                t=trans;
            }else{
                DnaState dnastate2 = getStateByIndex(to_index);
                put(trans, dnastate2);
            }

        } else {
            l = l.replaceAll("[\\[\\]]", "");
            String[] parts = l.split(" label=");
            String[] second_part_split = parts[1].split(",");
            String[] treeStrArr = second_part_split[0].split(" ");
            List<Integer> tree_lst = new ArrayList<>();
            for (String nodeStr : treeStrArr) {
                tree_lst.add(Integer.parseInt(nodeStr));
            }
            String[] labelStrArr = second_part_split[1].split(" ");
            Integer[] labelArr = new Integer[labelStrArr.length];
            for (int i = 0; i < labelStrArr.length; i++) {
                labelArr[i] = Integer.parseInt(labelStrArr[i]);
            }
            int state_index = Integer.parseInt(parts[0].replace("Q", ""));
            DnaState state = new DnaState(new Tree(tree_lst), new Label(labelArr), state_index);
            if (state_index == TempIndex){
                qPrime=state;
            }else{
                add(state);
                if (state_index == 0) {
                    setQ0(state);
                }
            }
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String stepRep(){
        return "X"+lastStep+System.lineSeparator();
    }

    public StringBuffer tempStateS(){
        StringBuffer res=new StringBuffer();
        List<Map.Entry<Integer, List<State>>> edgesLst= new ArrayList<>();
        for (Map.Entry<Integer, Set<State>> node:SPrimeMap.entrySet()){
            List<State> lst= new ArrayList<>(node.getValue());
            Collections.sort(lst,(State o1,State o2) -> o1.getIndex()-o2.getIndex());
            edgesLst.add(new AbstractMap.SimpleEntry<>(node.getKey(),lst));
        }
        Collections.sort(edgesLst,(Map.Entry<Integer, List<State>> o1, Map.Entry<Integer, List<State>> o2) -> o1.getKey().compareTo(o2.getKey()));
        
        for (int i=0;i<edgesLst.size();i++){
            Map.Entry<Integer, List<State>> node=edgesLst.get(i);
            int index=node.getKey();
            List<State> l=node.getValue();
            res.append(index);
            res.append(" [");
            res.append("label=\"");
            res.append(index);
            res.append(": ");
            res.append("{");
            for (int j=0;j<l.size();j++){
            	res.append("q");
            	res.append(l.get(j).getIndex());
            	if (j<l.size()-1){
            		res.append(", ");
            	}
            }
            res.append("}\"]");
            res.append(System.lineSeparator());
        }
        return res;
    }

    public StringBuffer tempStateT(){
    	StringBuffer res= new StringBuffer();
    	Tree t=qPrime.getT();
    	for (int i=1;i<t.size()+1;i++){
    		res.append(t.getT(i));
    		res.append(" -> ");
    		res.append(i);
    		res.append(System.lineSeparator());
    	}
    	return res;
    	
    }

    //assumes sigmaSize>0
    //if there is a state for which some of the edges were built (but not all), return it, and the next char.
    //else if there is a state for which no edges were built and the first char.
    //else return null
    public Transition getNextTransition() {
        if (t == null) {//beginning of construction
            return new Transition(getQ0(), getSigma(0));
        }
        int sigmaSize = getSigmaSize();
        char currChar = t.getSigma();
        State currState = t.getFrom();
        if (currChar != getSigma(sigmaSize - 1)) {//still have edges to build
            return new Transition(currState, getSigma(getSigmaIndex(currChar)+1));
        } else {
            Iterator<State> states = getQ().keySet().iterator();
            while (states.hasNext()) {
                if (currState.equals(states.next())) {
                    if (states.hasNext()){
                        return new Transition(states.next(), getSigma(0));
                    }break;

                }
            }
        }
        return null;
    }

    @Override
    public List<State> sortedStatesLst(boolean sort){
        List<State> statesLst = super.sortedStatesLst(lastStep==6);
        if (qPrime!=null && lastStep !=5){
            statesLst.add(0,qPrime);
        }
        return statesLst;
    }

    @Override
    public List<Map.Entry<Transition, State>> sortedEdgeLst(){
        List<Map.Entry<Transition, State>> edgesLst = super.sortedEdgeLst();
        if (t!=null && lastStep !=5){
            edgesLst.add(0,new AbstractMap.SimpleEntry<>(t,qPrime));
        }
        return edgesLst;
    }

    public static boolean isTempStateLine(String s){
    	return s.contains("Qp") && !s.contains(" -> ");
    }
    
    public static boolean isStateLine(String s){
    	return !s.contains(" -> ") && s.contains("Q") && !s.contains("Qp");
    }
    
    public static boolean isTempStateSLine(String s){
    	return s.contains(":");
    }
    
    public static boolean isTempStateTLine(String s){
    	return s.contains(" -> ") && !s.contains("Q");
    }
    
    public static boolean isStepLine(String s){
    	return s.contains("X");
    }
    
    public static boolean isEdgeLine(String s){
    	return s.contains(" -> ") && s.contains("Q");
    }
    
    public static boolean isTempEdgeLine(String s){
        return s.contains(" -> ") && s.contains("Qp");
    }
    
    public String lastStepStr(){
    	switch(lastStep){
    		case 0:
    			return "Add Start State";
    		case 1:
    			return "Spawn";
    		case 2:
    			return "Hereditary";
    		case 3:
    			return "Uniqueness";
    		case 4:
    			return "Pack";
    		case 5:
    			return "Assign transition number";
    		case 6:
    			return "Complete";
    		default:
    			return "";
    	}
    }
    
}
