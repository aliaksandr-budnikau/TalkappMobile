package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.service.SentenceProvider;
import talkapp.org.talkappmobile.repository.SentenceRepository;

class CachedSentenceProviderDecorator extends SentenceProviderDecorator {
    private final SentenceRepository sentenceRepository;

    public CachedSentenceProviderDecorator(SentenceProvider provider, SentenceRepository sentenceRepository) {
        super(provider);
        this.sentenceRepository = sentenceRepository;
    }

    @Override
    public List<Sentence> find(Word2Tokens word) {
        List<Sentence> sentences;
        try {
            sentences = super.find(word);
        } catch (InternetConnectionLostException e) {
            return super.getFromDB(word);
        }
        if (sentences == null || sentences.isEmpty()) {
            return super.getFromDB(word);
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
        return sentenceRepository.findById(sentence.getId()) != null;
    }
}