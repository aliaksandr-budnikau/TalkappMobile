package talkapp.org.talkappmobile.component;

import java.util.List;

import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;

public interface GrammarCheckService {
    List<GrammarError> check(String text);

    boolean score(Sentence sentence);
}
