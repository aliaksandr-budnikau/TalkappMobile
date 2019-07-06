package org.talkappmobile.model;

import android.support.annotation.NonNull;

public class WordSetRepetitionTask extends Task {
    private final RepetitionClass repetitionClass;

    public WordSetRepetitionTask(@NonNull String title, @NonNull String description,
                                 @NonNull RepetitionClass repetitionClass) {
        super(title, description);
        this.repetitionClass = repetitionClass;
    }

    public RepetitionClass getRepetitionClass() {
        return repetitionClass;
    }
}