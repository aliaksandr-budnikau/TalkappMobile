package talkapp.org.talkappmobile.service.impl;

import java.util.List;
import java.util.Set;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.SentenceSelector;
import talkapp.org.talkappmobile.service.WordsCombinator;

/**
 * @author Budnikau Aliaksandr
 */
public class GameProcesses {

    private final WordsCombinator wordsCombinator;
    private final SentenceSelector sentenceSelector;
    private final WordSet wordSet;
    private final GameProcessCallback callback;

    public GameProcesses(WordsCombinator wordsCombinator, SentenceSelector sentenceSelector,
                         WordSet wordSet, GameProcessCallback callback) {
        this.wordsCombinator = wordsCombinator;
        this.sentenceSelector = sentenceSelector;
        this.wordSet = wordSet;
        this.callback = callback;
    }

    public void start() {
        Set<String> combinations = wordsCombinator.combineWords(wordSet.getWords());
        for (final String combination : combinations) {
            List<Sentence> sentences = callback.findByWords(combination);
            Sentence sentence = sentenceSelector.getSentence(sentences);
            callback.returnProgress(sentence);
        }
    }
}