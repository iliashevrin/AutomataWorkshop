package StepByStep;

import java.util.ArrayList;
import java.util.List;

public class Tree {
    private List<Integer> T=new ArrayList<>();

    public Tree(List<Integer> T){
        this.T=T;
    }

    public Tree(Tree tree){
        for (int i=0;i<tree.size();i++){
            T.add(tree.getT(i+1));
        }
    }

    public Tree(int treeSize){
        for (int i=0;i<treeSize;i++){
            T.add(0);
        }
    }

    public int getT(int node){
        return T.get(node-1);
    }


    public void setT(int child,int parent){
        T.set(child-1,parent-1);
    }


    public void expandTree(){
        int k=T.size();
        for(int i=0;i<k+1;i++){
            T.add(i);
        }
    }

    private List<Integer> getT(){
        return T;
    }

    public int size(){
        return T.size();
    }

    public List<Integer> children(int node){
        List <Integer> children= new ArrayList<>();
        for (int i=1;i<=T.size();i++){
            if (getT(i)==node){
                children.add(i);
            }
        }
        return children;
    }

    public List<Integer> siblings(int node){
        List <Integer> siblings= new ArrayList<>();
        if (node!=0) {
            for (int i = 1; i < T.size() + 1; i++) {
                if (getT(i) == getT(node) && i != node) {
                    siblings.add(i);
                }
            }
        }
        return siblings;
    }

    public List<Integer> descendants(int node){
        List <Integer> descendants= new ArrayList<>();
        recDescendants(node,descendants);
        return descendants;
    }

    private void recDescendants(int node,List<Integer> descendants){
        List <Integer> childrenLst= children(node);
        if (childrenLst.isEmpty()){
            return;
        }
        descendants.addAll(childrenLst);
        for(Integer i:childrenLst){
            recDescendants(i,descendants);
        }
    }

    public String toString() {
        return T.toString().replaceAll(", ", " ");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tree tree = (Tree) o;
        return T.equals(tree.getT());

    }
}
