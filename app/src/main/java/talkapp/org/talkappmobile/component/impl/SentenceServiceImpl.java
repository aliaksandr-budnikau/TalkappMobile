package talkapp.org.talkappmobile.component.impl;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import talkapp.org.talkappmobile.component.SentenceService;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.impl.LocalCacheIsEmptyException;
import talkapp.org.talkappmobile.component.database.WordRepetitionProgressService;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public class SentenceServiceImpl implements SentenceService {
    public static final int WORDS_NUMBER = 6;
    private final DataServer server;
    private final WordRepetitionProgressService exerciseService;

    public SentenceServiceImpl(DataServer server, WordRepetitionProgressService exerciseService) {
        this.server = server;
        this.exerciseService = exerciseService;
    }

    @Override
    public boolean classifySentence(Sentence sentence) {
        return server.saveSentenceScore(sentence);
    }

    @Override
    public List<Sentence> fetchSentencesFromServerByWordAndWordSetId(Word2Tokens word, int wordSetId) {
        List<Sentence> result;
        try {
            result = new LinkedList<>(server.findSentencesByWords(word, WORDS_NUMBER, wordSetId));
        } catch (LocalCacheIsEmptyException e) {
            server.initLocalCacheOfAllSentencesForThisWordset(wordSetId, WORDS_NUMBER);
            List<Sentence> cached = server.findSentencesByWords(word, WORDS_NUMBER, wordSetId);
            result = new LinkedList<>(cached);
        }
        return getRidOfDuplicates(result);
    }

    @Override
    public List<Sentence> fetchSentencesNotFromServerByWordAndWordSetId(Word2Tokens word, int wordSetId) {
        return getRidOfDuplicates(new ArrayList<>(exerciseService.findByWordAndWordSetId(word, word.getSourceWordSetId())));
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
}
