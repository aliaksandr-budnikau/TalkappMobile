package talkapp.org.talkappmobile.events;

public class UserExpUpdatedEM {
    private final double newExpScore;

    public UserExpUpdatedEM(double newExpScore) {
        this.newExpScore = newExpScore;
    }

    public double getNewExpScore() {
        return newExpScore;
    }
}