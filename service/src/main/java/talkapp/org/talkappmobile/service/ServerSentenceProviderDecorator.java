package talkapp.org.talkappmobile.service;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

import static java.util.Collections.emptyList;

@RequiredArgsConstructor
public class ServerSentenceProviderDecorator implements SentenceProvider {
    public static final int WORDS_NUMBER = 6;
    @Delegate(excludes = ExcludedMethods.class)
    private final SentenceProvider provider;
    private final DataServer server;

    @Override
    public List<Sentence> find(Word2Tokens word) {
        List<Sentence> sentences = provider.find(word);
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

    private interface ExcludedMethods {
        List<Sentence> find(Word2Tokens word);
    }
}