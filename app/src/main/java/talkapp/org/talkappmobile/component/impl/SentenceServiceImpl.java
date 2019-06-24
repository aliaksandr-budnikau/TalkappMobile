package talkapp.org.talkappmobile.component.impl;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import talkapp.org.talkappmobile.component.SentenceService;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.impl.LocalCacheIsEmptyException;
import talkapp.org.talkappmobile.component.database.WordRepetitionProgressService;
import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Word2Tokens;

import static org.talkappmobile.model.SentenceContentScore.POOR;

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
    public List<Sentence> fetchSentencesFromServerByWordAndWordSetId(Word2Tokens word) {
        List<Sentence> result;
        try {
            result = new LinkedList<>(server.findSentencesByWords(word, WORDS_NUMBER, word.getSourceWordSetId()));
        } catch (LocalCacheIsEmptyException e) {
            server.initLocalCacheOfAllSentencesForThisWordset(word.getSourceWordSetId(), WORDS_NUMBER);
            List<Sentence> cached = server.findSentencesByWords(word, WORDS_NUMBER, word.getSourceWordSetId());
            result = new LinkedList<>(cached);
        }
        return getRidOfDuplicates(result);
    }

    @Override
    public List<Sentence> fetchSentencesNotFromServerByWordAndWordSetId(Word2Tokens word) {
        return getRidOfDuplicates(new ArrayList<>(exerciseService.findByWordAndWordSetId(word)));
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
    public List<Sentence> selectSentences(List<Sentence> sentences) {
        if (sentences.isEmpty()) {
            throw new IllegalArgumentException("The list of sentences is empty");
        }
        if (sentences.size() == 1) {
            return sentences;
        }

        LinkedList<Sentence> badSentences = new LinkedList<>();
        LinkedList<Sentence> okSentences = new LinkedList<>();
        LinkedList<Sentence> otherSentences = new LinkedList<>();

        for (Sentence sentence : sentences) {
            if (sentence.getContentScore() == null) {
                okSentences.add(sentence);
            } else if (sentence.getContentScore() == POOR) {
                otherSentences.add(sentence);
            } else {
                badSentences.add(sentence);
            }
        }
        if (!okSentences.isEmpty()) {
            return okSentences;
        }

        if (!otherSentences.isEmpty()) {
            return otherSentences;
        }

        return badSentences;
    }

    @Override
    public void orderByScore(List<Sentence> sentences) {
        Collections.sort(sentences, new Comparator<Sentence>() {
            @Override
            public int compare(Sentence o1, Sentence o2) {
                if (o1.getContentScore() == null) {
                    if (o2.getContentScore() == null) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else {
                    if (o2.getContentScore() == null) {
                        return 1;
                    }
                }
                return o1.getContentScore().compareTo(o2.getContentScore());
            }
        });
    }
}
