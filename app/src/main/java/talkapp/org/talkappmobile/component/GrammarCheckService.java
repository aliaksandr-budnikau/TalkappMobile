package talkapp.org.talkappmobile.component;

import java.util.List;

import talkapp.org.talkappmobile.model.GrammarError;

public interface GrammarCheckService {
    List<GrammarError> check(String text);
}
