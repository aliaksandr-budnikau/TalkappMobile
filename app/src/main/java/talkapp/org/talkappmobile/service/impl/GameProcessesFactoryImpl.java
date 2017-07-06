package talkapp.org.talkappmobile.service.impl;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.GameProcessesFactory;
import talkapp.org.talkappmobile.service.SentenceSelector;
import talkapp.org.talkappmobile.service.WordsCombinator;

/**
 * @author Budnikau Aliaksandr
 */
public class GameProcessesFactoryImpl implements GameProcessesFactory {

    private final WordsCombinator wordsCombinator;
    private final SentenceSelector sentenceSelector;

    public GameProcessesFactoryImpl(WordsCombinator wordsCombinator, SentenceSelector sentenceSelector) {
        this.wordsCombinator = wordsCombinator;
        this.sentenceSelector = sentenceSelector;
    }

    @Override
    public GameProcesses createGameProcesses(WordSet currentWordSet, GameProcessCallback callback) {
        return new GameProcesses(wordsCombinator, sentenceSelector, currentWordSet, callback);
    }
}