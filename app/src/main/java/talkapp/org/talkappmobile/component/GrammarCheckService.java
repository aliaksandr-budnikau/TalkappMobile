package talkapp.org.talkappmobile.component;

import talkapp.org.talkappmobile.model.Sentence;

public interface GrammarCheckService {
    boolean score(Sentence sentence);
}