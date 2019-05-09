package talkapp.org.talkappmobile.component.impl;

import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public class SentenceProviderImpl implements SentenceProvider {
    private final BackendSentenceProviderStrategy backendStrategy;
    private final SentenceProviderRepetitionStrategy repetitionStrategy;
    private SentenceProviderStrategy currentStrategy;

    public SentenceProviderImpl(BackendSentenceProviderStrategy backendStrategy, SentenceProviderRepetitionStrategy repetitionStrategy) {
        this.backendStrategy = backendStrategy;
        this.repetitionStrategy = repetitionStrategy;
        disableRepetitionMode();
    }

    @Override
    public List<Sentence> findByWordAndWordSetId(Word2Tokens word, int wordSetId) {
        List<Sentence> sentences = currentStrategy.findByWordAndWordSetId(word, wordSetId);
        return getRidOfDuplicates(sentences);
    }

    @NonNull
    private LinkedList<Sentence> getRidOfDuplicates(List<Sentence> sentences) {
        Set<String> texts = new HashSet<>();
        LinkedList<Sentence> result = new LinkedList<>();
        for (Sentence sentence : sentences) {
            if (texts.add(sentence.getTranslations().get("russian"))) {
                result.add(sentence);
            }
        }
        return result;
    }

    @Override
    public void enableRepetitionMode() {
        currentStrategy = repetitionStrategy;
    }

    @Override
    public void disableRepetitionMode() {
        currentStrategy = backendStrategy;
    }
}