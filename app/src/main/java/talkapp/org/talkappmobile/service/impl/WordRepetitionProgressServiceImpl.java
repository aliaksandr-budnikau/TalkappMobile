package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TimeZone;
import java.util.TreeMap;

import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordRepetitionProgress;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.repository.SentenceRepository;
import talkapp.org.talkappmobile.repository.WordRepetitionProgressRepository;
import talkapp.org.talkappmobile.repository.WordSetRepository;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;

import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.util.Collections.sort;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.next;

public class WordRepetitionProgressServiceImpl implements WordRepetitionProgressService {
    private final SentenceRepository sentenceRepository;
    private final WordSetRepository wordSetRepository;
    private final WordRepetitionProgressRepository progressRepository;
    private int wordSetSize = 12;

    public WordRepetitionProgressServiceImpl(WordRepetitionProgressRepository progressRepository, WordSetRepository wordSetRepository, SentenceRepository sentenceRepository) {
        this.progressRepository = progressRepository;
        this.wordSetRepository = wordSetRepository;
        this.sentenceRepository = sentenceRepository;
    }

    @Override
    public void save(Word2Tokens word, List<Sentence> sentences) {
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

    @Override
    public void shiftSentences(Word2Tokens word) {
        WordSet wordSet = wordSetRepository.findById(word.getSourceWordSetId());
        WordRepetitionProgress exercise = progressRepository.findByWordIndexAndWordSetId(wordSet.getWords().indexOf(word), word.getSourceWordSetId()).get(0);
        List<String> ids = exercise.getSentenceIds();
        for (int i = 0; i < ids.size(); i++) {
            if (isEmpty(ids.get(i), wordSet.getWords().get(exercise.getWordIndex()).getWord())) {
                ids.remove(i);
                i--;
            } else {
                String first = ids.remove(i);
                ids.add(first);
                break;
            }
        }
        exercise.setSentenceIds(ids);
        progressRepository.createNewOrUpdate(exercise);
    }

    private boolean isEmpty(String sentenceId, String word) {
        return StringUtils.isEmpty(sentenceId) || StringUtils.isEmpty(word);
    }

    @Override
    public void cleanByWordSetId(int wordSetId) {
        progressRepository.cleanByWordSetId(wordSetId);
    }

    @Override
    public void createSomeIfNecessary(List<Word2Tokens> words) {
        List<WordRepetitionProgress> wordsEx = new LinkedList<>();
        for (int wordIndex = 0; wordIndex < words.size(); wordIndex++) {
            Word2Tokens word = words.get(wordIndex);
            List<WordRepetitionProgress> alreadyCreatedWord = progressRepository.findByWordIndexAndWordSetId(wordIndex, word.getSourceWordSetId());
            if (alreadyCreatedWord != null && !alreadyCreatedWord.isEmpty()) {
                continue;
            }
            WordRepetitionProgress exercise = new WordRepetitionProgress();
            exercise.setStatus(FIRST_CYCLE.name());
            exercise.setWordIndex(wordIndex);
            exercise.setWordSetId(word.getSourceWordSetId());
            exercise.setUpdatedDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
            wordsEx.add(exercise);
        }
        progressRepository.createAll(wordsEx);
    }

    @Override
    public List<WordSet> findFinishedWordSetsSortByUpdatedDate(long limit, int olderThenInHours) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        List<WordRepetitionProgress> exercises = progressRepository.findWordSetsSortByUpdatedDateAndByStatus(limit * wordSetSize, cal.getTime(), FINISHED.name());
        NavigableMap<Integer, List<Word2TokensAndAvailableInHours>> tree = getSortedTreeByRepetitionCount(exercises, olderThenInHours);
        List<WordSet> wordSets = new LinkedList<>();
        for (RepetitionClass clazz : RepetitionClass.values()) {
            insertWordSetGroup(wordSets, tree.subMap(clazz.getFrom(), clazz.getTo()).values(), clazz);
        }
        sort(wordSets, new Comparator<WordSet>() {
            @Override
            public int compare(WordSet o1, WordSet o2) {
                return o1.getAvailableInHours() - o2.getAvailableInHours();
            }
        });
        return wordSets;
    }

    @NonNull
    private NavigableMap<Integer, List<Word2TokensAndAvailableInHours>> getSortedTreeByRepetitionCount(List<WordRepetitionProgress> exercises, int olderThenInHours) {
        NavigableMap<Integer, List<Word2TokensAndAvailableInHours>> tree = new TreeMap<>();
        for (WordRepetitionProgress exercise : exercises) {
            Word2Tokens word2Tokens = getWord2Tokens(exercise);
            if (tree.get(exercise.getRepetitionCounter()) == null) {
                tree.put(exercise.getRepetitionCounter(), new LinkedList<Word2TokensAndAvailableInHours>());
            }
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.add(Calendar.HOUR, -(countHours(olderThenInHours, exercise.getRepetitionCounter())));
            tree.get(exercise.getRepetitionCounter()).add(
                    new Word2TokensAndAvailableInHours(word2Tokens, getDifferenceInHours(cal.getTime(), exercise.getUpdatedDate()))
            );
        }
        return tree;
    }

    private void insertWordSetGroup(List<WordSet> allWordSets, Collection<List<Word2TokensAndAvailableInHours>> words, RepetitionClass clazz) {
        LinkedList<Word2TokensAndAvailableInHours> flattenList = new LinkedList<>();
        for (List<Word2TokensAndAvailableInHours> list : words) {
            flattenList.addAll(list);
        }
        WordSet set = new WordSet();
        set.setWords(new LinkedList<Word2Tokens>());
        set.setRepetitionClass(clazz);
        for (Word2TokensAndAvailableInHours word : flattenList) {
            if (set.getAvailableInHours() < word.getAvailableInHours()) {
                set.setAvailableInHours(word.getAvailableInHours());
            }
            set.getWords().add(word.getWord2Tokens());
            if (set.getWords().size() == wordSetSize) {
                allWordSets.add(set);
                set = new WordSet();
                set.setWords(new LinkedList<Word2Tokens>());
                set.setRepetitionClass(clazz);
            }
        }
        if (!set.getWords().isEmpty()) {
            allWordSets.add(set);
        }
    }

    @Override
    public int getMaxWordSetSize() {
        return wordSetSize;
    }

    private int countHours(int olderThenInHours, int counter) {
        return (int) (olderThenInHours + log(max(counter, 1)) * 48 * counter);
    }

    @Override
    public List<WordSet> findFinishedWordSetsSortByUpdatedDate(int olderThenInHours) {
        return findFinishedWordSetsSortByUpdatedDate(Integer.MAX_VALUE, olderThenInHours);
    }

    private Word2Tokens getWord2Tokens(WordRepetitionProgress mapping) {
        WordSet wordSet = wordSetRepository.findById(mapping.getWordSetId());
        return wordSet.getWords().get(mapping.getWordIndex());
    }

    @Override
    public void moveCurrentWordToNextState(Word2Tokens word) {
        WordSet wordSet = wordSetRepository.findById(word.getSourceWordSetId());
        List<WordRepetitionProgress> current = progressRepository.findByWordIndexAndWordSetId(
                wordSet.getWords().indexOf(word), word.getSourceWordSetId());
        WordRepetitionProgress mapping = current.get(0);
        mapping.setStatus(next(WordSetProgressStatus.valueOf(mapping.getStatus())).name());
        mapping.setUpdatedDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
        progressRepository.createNewOrUpdate(mapping);
    }

    @Override
    public int markAsRepeated(Word2Tokens word) {
        WordRepetitionProgress exercise = getWordRepetitionProgressMapping(word);
        int counter = exercise.getRepetitionCounter();
        counter++;
        exercise.setRepetitionCounter(counter);
        exercise.setUpdatedDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
        progressRepository.createNewOrUpdate(exercise);
        return counter;
    }

    @Override
    public int getRepetitionCounter(Word2Tokens word) {
        WordRepetitionProgress exercise = getWordRepetitionProgressMapping(word);
        return exercise.getRepetitionCounter();
    }

    @Override
    public int markAsForgottenAgain(Word2Tokens word) {
        WordRepetitionProgress exercise = getWordRepetitionProgressMapping(word);
        int counter = exercise.getForgettingCounter();
        counter++;
        exercise.setForgettingCounter(counter);
        progressRepository.createNewOrUpdate(exercise);
        return counter;
    }

    @Override
    public List<WordSet> findWordSetOfDifficultWords() {
        List<WordRepetitionProgress> words = progressRepository.findWordSetsSortByUpdatedDateAndByStatus(Integer.MAX_VALUE, new Date(), FINISHED.name());
        sortByForgettingAndRepetitionCounters(words);
        LinkedList<WordSet> wordSets = new LinkedList<>();
        WordSet current = new WordSet();
        current.setWords(new LinkedList<Word2Tokens>());
        for (WordRepetitionProgress word : words) {
            if (current.getWords().size() == wordSetSize) {
                wordSets.add(current);
                current = new WordSet();
                current.setWords(new LinkedList<Word2Tokens>());
            }
            current.getWords().add(getWord2Tokens(word));
        }
        if (current.getWords().size() == wordSetSize) {
            wordSets.add(current);
        }
        return wordSets;
    }

    @Override
    public void updateSentenceIds(Word2Tokens newWord2Token, Word2Tokens oldWord2Token) {
        WordSet wordSet = wordSetRepository.findById(newWord2Token.getSourceWordSetId());
        List<WordRepetitionProgress> exercises = progressRepository.findByWordIndexAndWordSetId(wordSet.getWords().indexOf(newWord2Token), newWord2Token.getSourceWordSetId());
        if (exercises.isEmpty()) {
            return;
        }
        WordRepetitionProgress exercise = exercises.get(0);
        int wordsNumber = 6;
        List<Sentence> sentences = sentenceRepository.findAllByWord(newWord2Token.getWord(), wordsNumber);
        LinkedList<String> sentenceIds = new LinkedList<>();
        for (Sentence sentence : sentences) {
            sentenceIds.add(sentence.getId());
        }
        exercise.setSentenceIds(sentenceIds);
        progressRepository.createNewOrUpdate(exercise);
    }

    private void sortByForgettingAndRepetitionCounters(List<WordRepetitionProgress> words) {
        sort(words, new Comparator<WordRepetitionProgress>() {
            @Override
            public int compare(WordRepetitionProgress o1, WordRepetitionProgress o2) {
                float o1Result = o1.getRepetitionCounter() == 0 ? 0 : o1.getForgettingCounter() / o1.getRepetitionCounter();
                float o2Result = o2.getRepetitionCounter() == 0 ? 0 : o2.getForgettingCounter() / o2.getRepetitionCounter();
                if (o1Result > o2Result) {
                    return 1;
                } else if (o1Result < o2Result) {
                    return -1;
                }
                return 0;
            }
        });
    }

    private WordRepetitionProgress getWordRepetitionProgressMapping(Word2Tokens word) {
        int wordSetId = word.getSourceWordSetId();
        WordSet wordSet = wordSetRepository.findById(wordSetId);
        int index = wordSet.getWords().indexOf(word);
        List<WordRepetitionProgress> exercises;
        exercises = progressRepository.findByWordIndexAndWordSetId(index, wordSetId);
        return exercises.get(0);
    }

    private int getDifferenceInHours(Date startDate, Date endDate) {
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        return (int) (different / hoursInMilli);
    }

    private static class Word2TokensAndAvailableInHours {
        private final Word2Tokens word2Tokens;
        private final int availableInHours;

        public Word2TokensAndAvailableInHours(Word2Tokens word2Tokens, int availableInHours) {
            this.word2Tokens = word2Tokens;
            this.availableInHours = availableInHours;
        }

        public Word2Tokens getWord2Tokens() {
            return word2Tokens;
        }

        public int getAvailableInHours() {
            return availableInHours;
        }
    }
}