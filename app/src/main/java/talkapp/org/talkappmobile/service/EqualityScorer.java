package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.model.Word2Tokens;

public interface EqualityScorer {
    int score(String expected, String actual, Word2Tokens word);
}