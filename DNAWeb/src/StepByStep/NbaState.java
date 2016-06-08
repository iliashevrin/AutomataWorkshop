package StepByStep;

public class NbaState extends State {

    NbaState(int index){
        super(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NbaState nbaState = (NbaState) o;
        return nbaState.getIndex()==getIndex();
    }

    @Override
    public int hashCode() {
        return getIndex();
    }

    @Override
    public String getStateLabel() {
        return toString();
    }

    @Override
    public String toString() {
        return "q"+getIndex();
    }
}
