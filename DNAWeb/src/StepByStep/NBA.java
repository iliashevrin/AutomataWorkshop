package StepByStep;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


public class NBA extends AbstractAutomaton{
    private HashSet<State> S=new HashSet<>();
    private HashSet<State> F=new HashSet<>();
    private HashMap<Transition,HashSet<State>> delta=new HashMap<>();

    public NBA(HashMap<State,State> Q, char[] sigma, HashMap<Transition,HashSet<State>> delta, HashSet<State> F, HashSet<State> S) { // Explicit init
        super(Q,sigma);
        this.F = F;
        this.S= S;
        this.delta=delta;
    }

    public NBA(String s) {
        AbstractAutomaton(s);
    }

    public NBA(File f) {
        AbstractAutomaton(f);
    }

    public void parseLine(String line,Set<Character> sigmaSet){
        String l = line.replaceAll("\\t", "");
        String[] parts = l.split(" ");
        if (l.contains("->")) {//edge
            int from_index = Integer.parseInt(parts[0].substring(1));
            int to_index = Integer.parseInt(parts[2].substring(1));
            char sigma = parts[3].substring(parts[3].indexOf("=") + 1, parts[3].indexOf("]")).charAt(0);
            sigmaSet.add(sigma);
            NbaState from=new NbaState(from_index);
            NbaState to =new NbaState(to_index);
            add(from);
            add(to);
            put(new Transition(from, sigma), to);
        } else {
            int state_index = Integer.parseInt(parts[0].substring(1));
            NbaState state = (new NbaState(state_index));
            add(state);
            if (l.contains("*")) {
                S.add(state);
            }
            if (l.contains("$")) {
                F.add(state);
            }
        }
    }

    public HashSet<State> get(Transition transition){
        return delta.get(transition);
    }

    public void put(Transition transition, State to){
        if (!delta.containsKey(transition)) {
            delta.put(transition,new HashSet<>());
        }delta.get(transition).add(to);
    }

    public boolean contains(Transition t){
        return delta.containsKey(t);
    }

    public HashSet<State> getS() {
        return S;
    }

    public HashSet<State> getF() {
        return F;
    }


    public String getStateLabel(State q){
        String strQ=q.getStateLabel();
        if (S.contains(q)){
            strQ="*"+strQ;
        }if (F.contains(q)){
            strQ=strQ+"$";
        }
        return strQ;
    }


    public List<State> sortedStatesLst(boolean setIndex) {
        List<State> statesLst = new ArrayList<>(getQ().keySet());
        Collections.sort(statesLst, (State o1,State o2) -> o1.getIndex()-o2.getIndex());
        return statesLst;
    }

    public List<Map.Entry<Transition, State>> sortedEdgeLst(){
        List<Map.Entry<Transition, State>> edgesLst= new ArrayList<>();
        for (Map.Entry<Transition, HashSet<State>> e:delta.entrySet()){
            edgesLst.addAll(e.getValue().stream().map(s -> new AbstractMap.SimpleEntry<>(e.getKey(), s)).collect(Collectors.toList()));
        }
        Collections.sort(edgesLst,(Map.Entry<Transition, State> o1, Map.Entry<Transition, State> o2) -> entryToStr(o1).compareTo(entryToStr(o2)));
        return edgesLst;
    }

}
