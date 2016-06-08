package StepByStep;


public class Transition {
    private final State from;
    private final char sigma;
    private int number=-1;

    public Transition(State from, char second) {
        this.from = from;
        this.sigma = second;
    }

    public Transition(State from, char second,int number) {
        this(from,second);
        this.number=number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public char getSigma() {
        return sigma;
    }

    public State getFrom() {
        return from;
    }

    @Override
    public String toString() {
        return "(q is "+from.getStateLabel()+") and sigma="+sigma+" -> ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transition transition = (Transition) o;
        if (sigma != transition.sigma) return false;
        return from != null ? from.equals(transition.from) : transition.from == null;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
