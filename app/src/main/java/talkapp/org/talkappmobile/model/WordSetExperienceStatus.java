package talkapp.org.talkappmobile.model;

public enum WordSetExperienceStatus {
    STUDYING(0), REPETITION(1), FINISHED(2);

    private final int order;

    WordSetExperienceStatus(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public static WordSetExperienceStatus next(WordSetExperienceStatus current) {
        for (WordSetExperienceStatus next : values()) {
            if (next.getOrder() == current.getOrder() + 1) {
                return next;
            }
        }
        return FINISHED;
    }
}