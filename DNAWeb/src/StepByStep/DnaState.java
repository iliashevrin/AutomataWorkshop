package StepByStep;
import java.util.HashSet;

public class DnaState extends State {
    private Label s;
    private Tree T;
    private int g=Integer.MAX_VALUE;//TODO determine the smallest value possible for these
    private int b=Integer.MAX_VALUE;

    public DnaState(Tree T,Label label,int index) {
        super(index);
        this.s = label;
        this.T = T;
    }

    public DnaState(Tree T,Label label) {//start a DnaState with index TBD
        super(-1);
        this.s = label;
        this.T = T;
    }

    public static DnaState startState(int n, HashSet<State> starting){
        Tree T=new Tree(n-1);Label s=new Label(n);
        for (int i=0;i<n;i++){
            s.setLabel(i,-1);
        }
        for (State state : starting){
            s.setLabel(state,0);
        }
        return new DnaState(T,s,0);
    }

    public boolean isStarting(){
        return getIndex()==0;
    }

    public Label getLabel() {
        return s;
    }

    public void setLabel(Label s) {
        this.s = s;
    }

    public Tree getT() {
        return T;
    }

    public void setT(Tree t) {
        T = t;
    }

    public int getB() {
        return b;
    }

    public void updateB(int b) {
        this.b = Math.min(b,this.b);
    }

    public int getG() {
        return g;
    }

    public void updateG(int g) {
        this.g = Math.min(g,this.g);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DnaState dnaState = (DnaState) o;
        return s.equals(dnaState.s) && T.equals(dnaState.T);
    }

    public String getStateLabel(){
        return T.toString()+","+s.toString();
    }

    @Override
    public int hashCode() {
        return getStateLabel().hashCode();
    }

    @Override
    public String toString() {
    	if (getIndex()==-1){
    		return "Qp";
    	}
        return "Q"+getIndex();
    }

    public HashSet<Integer> S(int node){
        HashSet<Integer> res= new HashSet<>();
        HashSet<Integer> descendants=new HashSet<>(T.descendants(node));
        descendants.add(node);
        for (int i=0;i<s.size();i++){
            if(descendants.contains(s.getLabel(i))){
                res.add(i);
            }
        }
        return res;
    }

}
