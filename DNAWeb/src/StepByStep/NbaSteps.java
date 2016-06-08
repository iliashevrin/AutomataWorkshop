package StepByStep;

import java.util.Arrays;

/**
 * Created by University on 5/26/2016.
 */
public class NbaSteps extends Construction {

    //All current step variables besides DNA d
    public TempDNA tempD;

    int addStartState=0;
    int spawn=1;
    int seniority=2;
    int uniqueness=3;
    int pack=4;
    int assignTransition =5;
    int noMoreSteps=6;

    //private Stack history = new Stack<String>();//history of step variables only TODO?

    public NbaSteps(NBA b) {
        super(b);
        super.d=null;
        this.tempD=new TempDNA(b);
    }
    
    public NbaSteps(String s) {
        super(new NBA(s));
        super.d=null;
        this.tempD=new TempDNA(b);
    }

    public void finishConstruction() {
        while (makeNextStep()) ;
    }


    public boolean makeNextSteps(int numberOfSteps) {
        for (int stepsMade = 0; stepsMade < numberOfSteps; stepsMade++) {
            if (!makeNextStep()) {
                return false;
            }
        }
        return true;
    }

    public boolean prepareNextTransition(){
        tempD.t = tempD.getNextTransition();
        if (null == tempD.t) {//DNA is finished
            tempD.qPrime=null;
            tempD.t=null;
            tempD.SPrimeMap.clear();
            tempD.step =noMoreSteps;
            return false;
        }
        tempD.qPrime = nextState((DnaState) tempD.t.getFrom());
        return true;
    }

    @Override
    public boolean addStartState(DNA d){
        return super.addStartState(d);
    }

    public DnaState getQ(){
        return (DnaState) tempD.t.getFrom();
    }

    public void setStep(int step){
        tempD.lastStep=tempD.step;
        tempD.step=step;
    }

    //returns next state numbers transition
    public boolean makeNextStep() {
        if(isAddStartState()){
            addStartState(tempD);
            setStep(spawn);
        }else if(isSpawn()){
            if(!prepareNextTransition()) {
                tempD.lastStep=6;
                return false;
            }
            tempD.SPrimeMap = spawn(tempD.t, getQ());
            setStep(seniority);
        }else if(isSeniority()){
            Seniority(tempD.qPrime, tempD.SPrimeMap);
            setStep(uniqueness);
        }else if(isUniqueness()){
            Uniqueness(tempD.qPrime, tempD.SPrimeMap);
            setStep(pack);
        }else if(isPack()){
            pack(tempD.qPrime, tempD.SPrimeMap);
            setStep(assignTransition);
        }else if(isAssignTransition()){
            setStep(spawn);
            assignTransitionNumber(tempD.t, tempD.qPrime);
            tempD.add(tempD.qPrime);
            tempD.put(tempD.t, tempD.qPrime);
            tempD.qPrime=null;
            tempD.SPrimeMap.clear();
        }
        return true;
    }
    
    
    public String getGString(){
    	DnaState s=tempD.qPrime;
    	if (s==null){
    		return "";
    	}
    	
    	return (s.getG()+"").replace(Integer.MAX_VALUE+"", "inf");
    }
    
    public String getBString(){
    	DnaState s=tempD.qPrime;
    	if (s==null){
    		return "";
    	}
    	return (s.getB()+"").replace(Integer.MAX_VALUE+"", "inf");
    }
    
    public String toString(){
    	return tempD.toString();
    }
    
    public boolean isAddStartState(){
        return tempD.step==addStartState;
    }

    public boolean isSpawn(){
        return tempD.step==spawn;
    }

    public boolean isSeniority(){
        return tempD.step==seniority;
    }

    public boolean isUniqueness(){
        return tempD.step==uniqueness;
    }

    public boolean isPack(){
        return tempD.step==pack;
    }

    public boolean isAssignTransition(){
        return tempD.step== assignTransition;
    }

    public boolean isNoMoreSteps(){
        return tempD.step==6;
    }

	public void makeNextTrans() {
		if (isSpawn()){
			makeNextStep();
		}
		while(!isSpawn() && !isNoMoreSteps()){
			makeNextStep();
		}
	}
	
	
	public String BuildDnaGUIString(){
    	String dnaTransitions="";
		String dnaStates="";
		String str=toString();
    	String [] lines=str.split(System.lineSeparator());
		for (int i=0;i<lines.length;i++){
			String s=lines[i];
			if (TempDNA.isEdgeLine(s) || TempDNA.isTempEdgeLine(s)){
				dnaTransitions+=s;
				if (i<lines.length-1){
					dnaTransitions+=System.lineSeparator();
				}
			}
			else if (TempDNA.isStateLine(lines[i]) || TempDNA.isTempStateLine(s)){
				dnaStates+=lines[i];
				if (i<lines.length-1){
					dnaStates+=System.lineSeparator();
				}
			}
		}
    	StringBuilder graphViz = new StringBuilder();
		lines = dnaStates.split(System.lineSeparator());
		graphViz.append("rankdir=LR");
		graphViz.append(System.getProperty("line.separator"));
		graphViz.append("node [shape = circle]; ");
		int i,j;
		for (i=j=0 ; i < lines.length;i++) {
			graphViz.append("Q");
			if (TempDNA.isTempStateLine(lines[i])){
				graphViz.append("p");
			}else{
				graphViz.append(j++);
			}graphViz.append(" ");
		}
		
		graphViz.append("node [fixedsize = true, width = 0, height = 0, style = invisible]; 0");
		graphViz.append(System.lineSeparator());
		graphViz.append("0 -> Q0 [label=\"start\"]");
		graphViz.append(System.lineSeparator());
		for (i=j=0; i < lines.length;i++) {
			String s=lines[i];
			graphViz.append("Q");
			String index=TempDNA.isTempStateLine(s)?"p":""+j++;
			graphViz.append(index);			
			graphViz.append(" [label=\"");
			graphViz.append("Q");
			graphViz.append(index);
			
			graphViz.append("\"");
			if (index.equals("p")){
				graphViz.append(" fillcolor=Yellow style=filled");
			}
			graphViz.append("]");
			graphViz.append(System.getProperty("line.separator"));
		}
		graphViz.append(dnaTransitions);
    	return graphViz.toString(); 
    }
    
    public String addTitle(){
    	StringBuffer res=new StringBuffer();
    	res.append("labelloc=\"t\"");
    	res.append(System.lineSeparator());
    	res.append("label=\"");
    	res.append(tempD.lastStepStr());
    	res.append(" g=");
    	res.append(getGString());
    	res.append(" b=");
    	res.append(getBString());
    	res.append(" \"");
		return res.toString();
    }
    
    public String ShowInnerGraph(String state){
    	if (state.equals("Qp") || state.equals("")){
    		if(! isPack() && ! isAssignTransition()){
    			StringBuffer res=new StringBuffer();
    			res.append(tempD.tempStateS());
    			res.append(System.lineSeparator());
    			res.append(tempD.tempStateT());
    			res.append(System.lineSeparator());
    			res.append(addTitle());
    			return res.toString();
    		}
    	}
		String dnaStates="";
    	String [] lines=toString().split(System.lineSeparator());
		for (int i=0;i<lines.length;i++){
			String s=lines[i];
			if (TempDNA.isStateLine(lines[i]) || TempDNA.isTempStateLine(s)){
				dnaStates+=lines[i];
				if (i<lines.length-1){
					dnaStates+=System.lineSeparator();
				}
			}
		}
		lines = dnaStates.split(System.lineSeparator());
		String innerTree = "";
		// Parsing of the inner graph
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].contains(state)) {
				innerTree = lines[i].substring(lines[i].indexOf("\"") + 1, lines[i].lastIndexOf("\""));
				break;
			}
		}
		String parents[] = innerTree.substring(1, innerTree.indexOf("]")).split(" ");
		String nba[] = innerTree.substring(innerTree.indexOf(",") + 2, innerTree.length() - 1).split(" ");
		String innerStates[] = new String[parents.length + 1];
		Arrays.fill(innerStates, "");
		for (int i = 0; i < nba.length; i++) {
			if (!nba[i].equals("$")) {
				int node = Integer.parseInt(nba[i]);
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
				sb.append(": {");
				sb.append(innerStates[i]);
				sb.append("}\"]");
				sb.append(System.lineSeparator());
			}
		}
		for (int i = 0; i < parents.length; i++) {
			if (!innerStates[i + 1].equals("")) {
				sb.append(parents[i]);
				sb.append(" -> ");
				sb.append(i + 1);
				sb.append(System.getProperty("line.separator"));
			}
		}
		if(state.equals("Qp")){
			sb.append(System.lineSeparator());
			sb.append(addTitle());
		}
				
		
		
		return sb.toString();
    }
	
	
}


