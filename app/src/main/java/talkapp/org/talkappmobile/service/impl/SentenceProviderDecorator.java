package talkapp.org.talkappmobile.service.impl;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.service.SentenceProvider;

class SentenceProviderDecorator implements SentenceProvider {
    private final SentenceProvider provider;

    public SentenceProviderDecorator(SentenceProvider provider) {
        this.provider = provider;
    }

    @Override
    public List<Sentence> find(Word2Tokens word) {
        return provider.find(word);
    }
}