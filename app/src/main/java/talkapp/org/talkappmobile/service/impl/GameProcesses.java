package talkapp.org.talkappmobile.service.impl;

import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import talkapp.org.talkappmobile.activity.PracticeWordSetObserver;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;
import talkapp.org.talkappmobile.service.AuthSign;
import talkapp.org.talkappmobile.service.SentenceSelector;
import talkapp.org.talkappmobile.service.backend.SentenceService;
import talkapp.org.talkappmobile.service.backend.WordSetExperienceService;
import talkapp.org.talkappmobile.service.WordsCombinator;

/**
 * @author Budnikau Aliaksandr
 */
public class GameProcesses extends AsyncTask<Void, Void, Void> {
    private final WordSet wordSet;
    private final WeakReference<PracticeWordSetObserver> observer;
    @Inject
    WordsCombinator wordsCombinator;
    @Inject
    SentenceSelector sentenceSelector;
    @Inject
    WordSetExperienceService wordSetExperienceService;
    @Inject
    SentenceService sentenceService;
    @Inject
    AuthSign authSign;

    public GameProcesses(WordSet wordSet, PracticeWordSetObserver observer) {
        DIContext.get().inject(this);
        this.wordSet = wordSet;
        this.observer = new WeakReference<>(observer);
    }

    public void start() {
        if (wordSet.getExperience() == null) {
            wordSet.setExperience(createExperience(wordSet.getId()));
        }
        observer.get().onInitialise(wordSet);
        Set<String> combinations = wordsCombinator.combineWords(wordSet.getWords());
        try {
            for (final String combination : combinations) {
                List<Sentence> sentences = findSentencesByWords(combination);
                if (sentences.isEmpty()) {
                    observer.get().onSentencesNotFound(combination);
                    continue;
                }
                Sentence sentence = sentenceSelector.getSentence(sentences);
                observer.get().onNextSentence(sentence);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (Thread.currentThread().isInterrupted()) {
                observer.get().onInterruption();
            } else {
                observer.get().onFinish();
            }
        }
    }

    private WordSetExperience createExperience(String wordSetId) {
        try {
            return wordSetExperienceService.create(wordSetId, authSign).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private List<Sentence> findSentencesByWords(String words) {
        try {
            return sentenceService.findByWords(words, 6, authSign).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        start();
        return null;
    }
}