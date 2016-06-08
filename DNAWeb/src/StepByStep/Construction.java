package StepByStep;
import java.util.*;

public class Construction {
    protected final int undefined=-1;
    protected final NBA b;
    protected final int n;
    protected DNA d;

    public Construction(NBA b){
        this.b=b;
        this.d=new DNA(b);
        this.n=b.getQSize();
    }

    public DNA construct (){
        recConstruct(d.getQ0());
        return d;
    }

    private void recConstruct(State currState){
        if (d.add(currState)){
            constructOutEdges(currState);
        }
    }

    private void constructOutEdges(State currState){
        for (int i=0;i<d.getSigmaSize();i++){
            Transition newTransition=new Transition(currState,d.getSigma(i));
            State newState=applyDeltaD(newTransition);
            d.put(newTransition,newState);
            recConstruct(newState);
        }
    }

    //returns next state numbers transition
    private State applyDeltaD(Transition t){//TODO make g and b calculation more elegant
        DnaState q=(DnaState)t.getFrom();DnaState qPrime=nextState(q);
        HashMap<Integer,Set<State>> SPrimeMap;

        SPrimeMap=spawn(t,q);
        Seniority(qPrime,SPrimeMap);
        Uniqueness(qPrime,SPrimeMap);
        pack(qPrime,SPrimeMap);
        assignTransitionNumber(t,qPrime);
        return qPrime;
    }

    //returns a map with key:Treenode,value:states it contains
    protected Set<State> SPrime(int u,Transition t){
        DnaState q=(DnaState)t.getFrom(); char a=t.getSigma();
        HashSet<Integer> S=q.S(u);
        Set<State> SPrime=new HashSet<>();
        for (Integer qPrime :S) {
            Transition newTrans = new Transition(b.getState(new NbaState(qPrime)), a);
            if (b.contains(newTrans)){
                SPrime.addAll(b.get(newTrans));
            }
        }
        return SPrime;
    }

    protected DnaState nextState  (DnaState q){
        Tree TPrime=new Tree(q.getT());
        TPrime.expandTree();
        Label sPrime=new Label(0);
        return new DnaState(TPrime,sPrime);
    }

    public boolean addStartState(DNA d){
        return d.add(d.getQ0());
    }

    protected HashMap<Integer,Set<State>> spawn(Transition t,DnaState q) {
        HashMap<Integer, Set<State>> SPrimeMap = new HashMap<>();Label s=q.getLabel();
        for (int u = 0; u < n; u++) {
//            if (s.nodeIsEmpty(u)){
//                continue;
//            }
            Set<State> SPrime = SPrime(u, t);
            Set<State> SPrimeIntersectsF = new HashSet<>(SPrime);
            SPrimeIntersectsF.retainAll(b.getF());
            SPrimeMap.put(u, SPrime);
            SPrimeMap.put(n+u, SPrimeIntersectsF);
            
        }
        return SPrimeMap;
    }

    protected void Seniority(DnaState qPrime, HashMap<Integer, Set<State>> SPrimeMap){
        recEnforceSeniority(0,qPrime.getT(),SPrimeMap,new HashSet<>());
        qPrime.setLabel(new Label(n));
        constructLabeling(qPrime.getLabel(),SPrimeMap);
    }

    protected void recEnforceSeniority(int treeNode, Tree TPrime, HashMap<Integer
            , Set<State>> SPrimeMap, Set<State> parentsNodes) {//TODO improve function
        List<Integer> children = TPrime.children(treeNode);
        int childrenSize = children.size();
        if (childrenSize == 0) {
            SPrimeMap.get(treeNode).removeAll(parentsNodes);
            return;
        }
        Collections.sort(children);
        Set<State> toRemove = new HashSet<>(parentsNodes);
        for (Integer child:children) {
            if (!SPrimeMap.containsKey(child) || SPrimeMap.get(child).isEmpty()) continue; //node is empty
            //get child's current SPrime
            Set<State> childSPrime = SPrimeMap.get(child);
            //build toRemoveNext- parentNodes U (child sPrime intersects toRemove)
            HashSet<State> newParentNodes = new HashSet<>(parentsNodes);
            HashSet<State> tobeRemovedFromChild = new HashSet<>(childSPrime);
            tobeRemovedFromChild.retainAll(toRemove);
            newParentNodes.addAll(tobeRemovedFromChild);
            //remove from childSPrime all that is needed (parent Nodes + {S(u)| u is a senior sibling})
            childSPrime.removeAll(toRemove);
            //add to toRemove (to the list of nodes to be removed from next siblings) the nodes that are currently in childSPrime
            toRemove.addAll(childSPrime);
            //continue recursively
            recEnforceSeniority(child, TPrime, SPrimeMap, newParentNodes);
        }
    }

    protected void Uniqueness(DnaState qPrime, HashMap<Integer, Set<State>> SPrimeMap){
        Tree TPrime=qPrime.getT();
        for (int u=0;u<2*n;u++){
            if (!SPrimeMap.containsKey(u) || SPrimeMap.get(u).isEmpty()){
                qPrime.updateG(u);
                continue;
            }
            Set<State> uSPrime= SPrimeMap.get(u);
            List<Integer> children=TPrime.children(u);
            if (!children.isEmpty()){
                Set<State> ChildrenSPrime=new HashSet<>();
                children.stream().filter(SPrimeMap::containsKey).forEach(child -> ChildrenSPrime.addAll(SPrimeMap.get(child)));
                if (uSPrime.equals(ChildrenSPrime)){
                    qPrime.updateG(u);
                    List<Integer> uDescendants=TPrime.descendants(u);
                    uDescendants.forEach(SPrimeMap::remove);
                }
            }
        }
        constructLabeling(qPrime.getLabel(),SPrimeMap);
    }


    protected void constructLabeling(Label sPrime, HashMap<Integer, Set<State>> SPrimeMap){
        for (int stateIndex=0;stateIndex<n;stateIndex++){
            int label=undefined;
            for(int node=2*n-1;node>=0;node--){
                if (!SPrimeMap.containsKey(node)) continue;
                if (SPrimeMap.get(node).contains(new NbaState(stateIndex))){
                    label=node;
                    break;
                }
            }
            sPrime.setLabel (stateIndex,label);
        }
    }

    protected void pack(DnaState qPrime, HashMap<Integer, Set<State>> SPrimeMap){
        Tree TPrime= qPrime.getT(); Label s=qPrime.getLabel();
        int orders []= new int[2*n];
        int order=0;
        for (int u=0;u<2*n;u++){
            if (s.nodeIsEmpty(u)){
                orders[u]=undefined;
                qPrime.updateB(u);
            }else{
                orders[u]=order++;
            }
        }
        Tree newTree= new Tree(n-1);
        for (int u=1;u<2*n;u++){
            int parent =TPrime.getT(u);
            if (!s.nodeIsEmpty(u)){
                newTree.setT(orders[u],orders[parent]+1);
            }
        }
        qPrime.setT(newTree);

        for (int q=0;q<n;q++) {
            int newLabel = undefined;
            for (int i = 2 * n; i >=0; i--) {//TODO check if should be 2n-1
                if (!SPrimeMap.containsKey(i)) {
                    continue;
                }
                if (SPrimeMap.get(i).contains(new NbaState(q))) {
                    newLabel=orders[i];
                    break;
                }
            }
            s.setLabel(q, newLabel);
        }
    }

    protected void assignTransitionNumber(Transition t,DnaState qPrime){
        int g=qPrime.getG(); int b=qPrime.getB();
        int k = Math.min(g, b);
        t.setNumber(b <= g ?  2*k : 2*k+1);
    }
}
