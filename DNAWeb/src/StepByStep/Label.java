package StepByStep;
import java.util.Arrays;

public class Label {
    private Integer [] s;

    public Label(Integer [] s){
        this.s=s;
    }
    public Label(int size){
        this.s=new Integer[size];
    }


    public int getLabel(int state){
        return s[state];
    }

    public int getLabel(State state){
        return getLabel(state.getIndex());
    }


    public void setLabel(int state,int node){
        s[state]=node;
    }

    public void setLabel(State state,int node){
        setLabel(state.getIndex(),node);
    }

    public int size(){
        return s.length;
    }

    public boolean nodeIsEmpty (int treeNode){
        for (Integer unique : s) {
            if (unique == treeNode) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Label label = (Label) o;
        return Arrays.equals(s, label.s);
    }

    @Override
    public String toString() {
        return Arrays.toString(s).replaceAll(", ", " ").replaceAll("-1","\\$");
    }
}
