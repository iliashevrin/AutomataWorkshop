package StepByStep;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;


public abstract class AbstractAutomaton{
    private Map<State,State> Q=new HashMap<>();
    private char[] Sigma;

    public AbstractAutomaton(){}

    public AbstractAutomaton(Map<State,State> Q, char[] sigma) {
        this.Q = Q;
        this.Sigma = sigma;
    }

    public boolean add(State q) {
        if (!contains(q)) {
            Q.put(q, q);
            return true;
        }
        return false;
    }

    public abstract void parseLine(String line,Set<Character> sigmaSet);

    public void AbstractAutomaton(String s) {
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

    public void AbstractAutomaton(File f){
        String line="";
        try (Scanner s = new Scanner(f)) {
            HashSet<Character> sigmaSet=new HashSet<>();
            while (s.hasNextLine()) {
                line=s.nextLine();
                parseLine(line, sigmaSet);
            }
            setSigma(charSetToArray(sigmaSet));
        }
        catch(FileNotFoundException e){
            System.out.println("File error! path is:"+ System.lineSeparator() + f.getAbsolutePath());
        }catch (Exception e){
            System.out.println("line error! line is:"+ line);
        }
    }

    public static char[] charSetToArray(HashSet<Character> s){
        char [] arr= new char [s.size()];
        int curr=0;
        for (char c:s){
            arr[curr++]=c;
        }
        return arr;
    }


    public abstract void put(Transition t, State s);

    public Map<State,State> getQ() {
        return Q;
    }

    public State getState(State q){
        return Q.get(q);
    }

    public boolean contains(State q) {
        return Q.containsKey(q);
    }

    public int getQSize() {
        return Q.size();
    }

    public char[] getSigma() {
        return Sigma;
    }

    public char getSigma(int i) {
        return Sigma[i];
    }

    public int getSigmaSize() {
        return Sigma.length;
    }

    public int getSigmaIndex(char s) {
        int index=0;
        for (;index<Sigma.length;index++){
            if (Sigma[index]==s){
                break;
            }
        }
        return index;
    }



    protected void setSigma(char[] sigma) {
        this.Sigma = sigma;
    }

    public abstract String getStateLabel(State q);

    public String getTransitionLabel(Transition e){
        return String.valueOf(e.getSigma());
    }

    public void saveText(String name){
        try (PrintWriter writer = new PrintWriter(name, "UTF-8")) {
            writer.println(this.toString());
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private String EdgeToStr(Transition e) {
        return " [label=\"" + getTransitionLabel(e) + "\"]";
    }

    private String stateToStr(State state) {
        return state + " [label=\"" + getStateLabel(state) + "\"]";
    }

    public String entryToStr(Map.Entry<Transition, State> e) {
        return e.getKey().getFrom() + " -> " + e.getValue() + EdgeToStr(e.getKey());
    }

    public abstract List<State> sortedStatesLst(boolean setIndex);

    public abstract List<Map.Entry<Transition, State>> sortedEdgeLst();

    public String toString() {
        List<State> statesLst=sortedStatesLst(true);
        List<Map.Entry<Transition, State>> edgesLst=sortedEdgeLst();
        return listsToStr(statesLst,edgesLst);
    }

    public String listsToStr(List<State> statesLst, List<Map.Entry<Transition, State>> edgesLst){
        StringBuffer res=new StringBuffer();
        for (State s : statesLst) {
            res.append("\t\t" + stateToStr(s));
            if(!edgesLst.isEmpty()){
                res.append(System.lineSeparator());
            }
        }
        for (int i = 0; i < edgesLst.size(); i++) {
            res.append("\t\t\t\t" + entryToStr(edgesLst.get(i)));
            if (i < edgesLst.size() - 1) {
                res.append(System.lineSeparator());
            }
        }
        return res.toString();
    }
}
