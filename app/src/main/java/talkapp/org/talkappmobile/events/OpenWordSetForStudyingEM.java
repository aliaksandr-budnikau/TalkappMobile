package talkapp.org.talkappmobile.events;

import android.support.annotation.NonNull;

import org.talkappmobile.model.Topic;
import org.talkappmobile.model.WordSet;

public class OpenWordSetForStudyingEM {
    private final Topic topic;
    @NonNull
    private final WordSet wordSet;
    private final boolean repetitionMode;

    public OpenWordSetForStudyingEM(Topic topic, @NonNull WordSet wordSet, boolean repetitionMode) {
        this.topic = topic;
        this.wordSet = wordSet;
        this.repetitionMode = repetitionMode;
    }

    public Topic getTopic() {
        return topic;
    }

    @NonNull
    public WordSet getWordSet() {
        return wordSet;
    }

    public boolean isRepetitionMode() {
        return repetitionMode;
    }
}