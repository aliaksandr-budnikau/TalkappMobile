package talkapp.org.talkappmobile.component.impl;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.Word2SentenceCache;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.Sentence;

import static java.util.Arrays.asList;

public class SentenceProviderRepetitionStrategy extends SentenceProviderStrategy {

    @Inject
    Word2SentenceCache sentenceCache;

    public SentenceProviderRepetitionStrategy() {
        DIContext.get().inject(this);
    }

    @Override
    public List<Sentence> findByWord(String word) {
        return asList(sentenceCache.findByWord(word));
    }
}