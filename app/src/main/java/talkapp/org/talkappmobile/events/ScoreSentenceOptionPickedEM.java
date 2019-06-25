package talkapp.org.talkappmobile.events;

import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.SentenceContentScore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class ScoreSentenceOptionPickedEM {
    @NonNull
    private SentenceContentScore score;
    @NonNull
    private Sentence sentence;
}