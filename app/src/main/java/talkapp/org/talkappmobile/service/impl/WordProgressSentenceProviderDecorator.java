package talkapp.org.talkappmobile.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordRepetitionProgress;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.SentenceProvider;
import talkapp.org.talkappmobile.repository.WordRepetitionProgressRepository;
import talkapp.org.talkappmobile.repository.WordSetRepository;

import static java.util.Collections.shuffle;
import static talkapp.org.talkappmobile.model.SentenceContentScore.POOR;

class WordProgressSentenceProviderDecorator extends SentenceProviderDecorator {
    private final WordRepetitionProgressRepository progressRepository;
    private final WordSetRepository wordSetRepository;

    public WordProgressSentenceProviderDecorator(SentenceProvider provider, WordSetRepository wordSetRepository, WordRepetitionProgressRepository progressRepository) {
        super(provider);
        this.wordSetRepository = wordSetRepository;
        this.progressRepository = progressRepository;
    }

    @Override
    public List<Sentence> find(Word2Tokens word) {
        List<Sentence> sentences = super.find(word);
        orderByScore(sentences);
        sentences = selectSentences(sentences);
        save(word, sentences);
        return sentences;
    }

    public void save(Word2Tokens word, List<Sentence> sentences) {
        List<Sentence> shuffledSentences = new ArrayList<>(sentences);
        shuffle(shuffledSentences);
        WordSet wordSet = wordSetRepository.findById(word.getSourceWordSetId());
        WordRepetitionProgress exercise = progressRepository.findByWordIndexAndWordSetId(wordSet.getWords().indexOf(word), word.getSourceWordSetId()).get(0);
        List<String> ids = new LinkedList<>();
        for (Sentence sentence : sentences) {
            ids.add(sentence.getId());
        }
        exercise.setSentenceIds(ids);
        exercise.setUpdatedDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
        progressRepository.createNewOrUpdate(exercise);
    }

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
}