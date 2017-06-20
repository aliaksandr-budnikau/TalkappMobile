package talkapp.org.talkappmobile.service.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.DataSource;
import talkapp.org.talkappmobile.service.EventHandler;
import talkapp.org.talkappmobile.service.SentenceNotFoundException;
import talkapp.org.talkappmobile.service.SentenceSelector;
import talkapp.org.talkappmobile.service.TranslationExercise;
import talkapp.org.talkappmobile.service.WordSetNotFoundException;
import talkapp.org.talkappmobile.service.WordsCombinator;

/**
 * @author Budnikau Aliaksandr
 */
public class TranslationExerciseImpl implements TranslationExercise {

    final SentenceSelector sentenceSelector;
    final WordsCombinator wordsCombinator;
    private Semaphore semaphore = new Semaphore(0);
    private WordSet wordSet;
    private DataSource dataSource;
    private EventHandler eventHandler;

    public TranslationExerciseImpl(SentenceSelector selector, WordsCombinator combinator) {
        this.sentenceSelector = selector;
        this.wordsCombinator = combinator;
    }

    @Override
    public void run() {
        try {
            String wordSetId = dataSource.getWordSetId();
            checkWordSetId(wordSetId);
            try {
                wordSet = dataSource.findWordSetById(wordSetId);
            } catch (WordSetNotFoundException e) {
                eventHandler.onWordSetNotFound(wordSetId);
                return;
            }
            Set<String> combinations = wordsCombinator.combineWords(wordSet.getWords());
            for (String combination : combinations) {
                List<Sentence> sentences;
                try {
                    sentences = dataSource.findSentencesByWords(combination);
                } catch (SentenceNotFoundException e) {
                    eventHandler.onSentenceNotFound(combination, wordSet);
                    continue;
                }
                Sentence sentence = sentenceSelector.getSentence(sentences);
                eventHandler.onNewSentenceGot(sentence, combination, wordSet);
                semaphore.acquire();
            }
        } catch (Exception e) {
            eventHandler.onException(e);
        } finally {
            eventHandler.onDestroy();
        }
    }

    private void checkWordSetId(String wordSetId) {
        if (wordSetId == null || "".equals(wordSetId)) {
            throw new IllegalStateException("The word set id cannot be empty or null");
        }
    }

    @Override
    public void analyzeCheckingResult(AnswerCheckingResult result) {
        if (result.getErrors().isEmpty()) {
            if (result.getCurrentTrainingExperience() == wordSet.getMaxTrainingExperience()) {
                eventHandler.onWin(result, wordSet);
            }
            eventHandler.onNextTask(result, wordSet);
            semaphore.release();
        } else {
            eventHandler.onErrors(result, wordSet);
        }
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
}
