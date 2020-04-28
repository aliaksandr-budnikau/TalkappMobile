package talkapp.org.talkappmobile.model;

public class WordSetRepetitionTask extends Task {
    private final RepetitionClass repetitionClass;

    public WordSetRepetitionTask(String title, String description,
                                 RepetitionClass repetitionClass) {
        super(title, description);
        this.repetitionClass = repetitionClass;
    }

    public RepetitionClass getRepetitionClass() {
        return repetitionClass;
    }
}