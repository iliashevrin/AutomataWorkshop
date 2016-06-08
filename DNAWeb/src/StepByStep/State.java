package StepByStep;

public abstract class State {
    private int index;

    public State(int index) {
        this.index = index;
    }

    public abstract String getStateLabel();

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


}
