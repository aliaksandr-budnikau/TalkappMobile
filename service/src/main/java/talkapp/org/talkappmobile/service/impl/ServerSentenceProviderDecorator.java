package talkapp.org.talkappmobile.service.impl;

import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.SentenceProvider;

import static java.util.Collections.emptyList;

class ServerSentenceProviderDecorator extends SentenceProviderDecorator {
    public static final int WORDS_NUMBER = 6;
    private final DataServer server;

    public ServerSentenceProviderDecorator(SentenceProvider provider, DataServer server) {
        super(provider);
        this.server = server;
    }

    @Override
    public List<Sentence> find(Word2Tokens word) {
        List<Sentence> sentences = super.find(word);
        if (!sentences.isEmpty()) {
            return sentences;
        }

        Map<String, List<Sentence>> sentencesByWord = server.findSentencesByWordSetId(word.getSourceWordSetId(), WORDS_NUMBER);
        if (sentencesByWord != null && sentencesByWord.get(word.getWord()) != null && !sentencesByWord.get(word.getWord()).isEmpty()) {
            return sentencesByWord.get(word.getWord());
        }

        sentences = server.findSentencesByWord(word.getWord(), WORDS_NUMBER);
        if (sentences == null || sentences.isEmpty()) {
            return emptyList();
        }
        return sentences;
    }
}