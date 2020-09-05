package talkapp.org.talkappmobile.service;

import java.util.List;

import lombok.experimental.Delegate;
import talkapp.org.talkappmobile.exceptions.ObjectNotFoundException;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.repository.SentenceRepository;

public class CachedSentenceProviderDecorator implements SentenceProvider {
    private final SentenceRepository sentenceRepository;
    @Delegate(excludes = ExcludedMethods.class)
    private final SentenceProvider provider;

    public CachedSentenceProviderDecorator(SentenceProvider provider, SentenceRepository sentenceRepository) {
        this.provider = provider;
        this.sentenceRepository = sentenceRepository;
    }

    @Override
    public List<Sentence> find(Word2Tokens word) {
        List<Sentence> sentences;
        try {
            sentences = provider.find(word);
        } catch (InternetConnectionLostException e) {
            return provider.getFromDB(word);
        }
        if (sentences == null || sentences.isEmpty()) {
            return provider.getFromDB(word);
        }
        for (Sentence sentence : sentences) {
            if (wasAlreadySaved(sentence)) {
                continue;
            }
            sentenceRepository.createNewOrUpdate(sentence);
        }
        return sentences;
    }

    private boolean wasAlreadySaved(Sentence sentence) {
        try {
            sentenceRepository.findById(sentence.getId());
            return true;
        } catch (ObjectNotFoundException ignored) {
            return false;
        }
    }

    private interface ExcludedMethods {
        List<Sentence> find(Word2Tokens word);
    }
}