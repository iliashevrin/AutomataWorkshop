package StepByStep;
import java.io.File;
import java.util.*;

public class DNA extends AbstractAutomaton {
    private DnaState q0;
    private HashMap<Transition, State> delta = new LinkedHashMap<>();

    public DNA(){}

    public DNA(NBA nba) {
        this(new LinkedHashMap<>(),nba.getSigma(), new HashMap<>(), new HashMap<>());
        this.q0=DnaState.startState(nba.getQSize(),nba.getS());
    }

    //should be used only if no NBA is accessible
    private DNA(LinkedHashMap<State, State> Q, char[] sigma, HashMap<Transition, State> delta, HashMap<Transition,
            Integer> N) {
        super(Q, sigma);
        this.delta = delta;
        for (Transition t:N.keySet()){
            t.setNumber(N.get(t));
        }
    }

    public boolean contains(Transition t){
        return delta.containsKey(t);
    }

    public void setQ0(DnaState q){
        q0=q;
    }

    public boolean deltaIsEmpty(){
        return delta.isEmpty();
    }


    public DNA(File f){
        AbstractAutomaton(f);
    }

    public DNA(String s){
        AbstractAutomaton(s);
    }

    public void parseLine(String line,Set<Character> sigmaSet){
        String l = line.trim().replaceAll("[\"]","").replaceAll("\\$","-1");
        if (l.contains("->")) {//edge
            String[] parts = l.split("\\[label=");
            String[] first_part_split=parts[0].split(" -> ");
            int from_index = Integer.parseInt(first_part_split[0].replaceAll("[Q ]", ""));
            int to_index = Integer.parseInt(first_part_split[1].replaceAll("[Q ]", ""));
            Integer numbering = Integer.parseInt(parts[1].substring(parts[1].indexOf("[")+1,parts[1].indexOf("]")));
            char sigma = parts[1].charAt(0);
            DnaState dnastate1= getStateByIndex(from_index);
            DnaState dnastate2= getStateByIndex(to_index);
            Transition trans=new Transition(dnastate1,sigma);
            put(trans,dnastate2);
            setNumber(trans,numbering);
        } else {
            l=l.replaceAll("[\\[\\]]","");
            String[] parts = l.split(" label=");
            String[] second_part_split=parts[1].split(",");
            String [] treeStrArr=second_part_split[0].split(" ");
            List <Integer> tree_lst=new ArrayList<>();
            for (String nodeStr:treeStrArr){
                tree_lst.add(Integer.parseInt(nodeStr));
            }
            String [] labelStrArr=second_part_split[1].split(" ");
            Integer [] labelArr= new Integer[labelStrArr.length];
            for (int i=0;i<labelStrArr.length;i++){
                labelArr[i]=Integer.parseInt(labelStrArr[i]);
            }
            int state_index = Integer.parseInt(parts[0].replace("Q", ""));
            DnaState state = new DnaState(new Tree(tree_lst),new Label(labelArr),state_index);
            add(state);
            if (state_index==0){
                q0=state;
            }
        }
    }


    public DnaState getStateByIndex(int index){
        for (State s:getQ().keySet()){
            if (s.getIndex()==index){
                return (DnaState)s;
            }
        }
        return null;
    }


    public void put(Transition t, State s){
        DnaState valueToPut= !contains(s)? (DnaState)s:(DnaState)getState(s);
        delta.put(t,valueToPut);
    }

    public void setNumber(Transition t, int number){t.setNumber(number); }

    private int getNumber(Transition transition) {
        return transition.getNumber();
    }

    public State getQ0() {
        return q0;
    }


    public List<State> sortedStatesLst(boolean sort){
        List<State> statesLst = new ArrayList<>(getQ().keySet());
        statesLst.remove(getQ0());
        if(sort){
        	Collections.sort(statesLst, (State o1,State o2) -> getStateLabel(o1).compareTo(getStateLabel(o2)));
        }
        for (int i = 0; i < statesLst.size(); i++) {
            DnaState state = (DnaState) statesLst.get(i);
            if (!state.isStarting()) {
                state.setIndex(i+1);
            }
        }
        statesLst.add(0,getQ0());
        return statesLst;
    }

    public List<Map.Entry<Transition, State>> sortedEdgeLst(){
        List<Map.Entry<Transition, State>> edgesLst = new ArrayList<>(delta.entrySet());
        Collections.sort(edgesLst,(Map.Entry<Transition, State> o1, Map.Entry<Transition, State> o2) -> entryToStr(o1).compareTo(entryToStr(o2)));
        return edgesLst;
    }

    public String getStateLabel(State q) {
        return q.getStateLabel();
    }

    public String getTransitionLabel(Transition e) {
        return super.getTransitionLabel(e) + ("[" + getNumber(e)).replace("-1", "?") + "]";
    }


}