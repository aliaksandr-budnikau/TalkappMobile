package talkapp.org.talkappmobile.component;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.UncheckedAnswer;

public interface RefereeService {
    boolean checkAnswer(UncheckedAnswer answer);

    boolean scoreCurrentSentence(Sentence sentence);
}