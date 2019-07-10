package talkapp.org.talkappmobile.model;

public enum WordSetProgressStatus {
    FIRST_CYCLE(0), SECOND_CYCLE(1), FINISHED(2);

    private final int order;

    WordSetProgressStatus(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public static WordSetProgressStatus next(WordSetProgressStatus current) {
        for (WordSetProgressStatus next : values()) {
            if (next.getOrder() == current.getOrder() + 1) {
                return next;
            }
        }
        return FINISHED;
    }
}