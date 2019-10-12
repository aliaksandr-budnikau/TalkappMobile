package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TimeZone;
import java.util.TreeMap;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.mapper.SentenceMapper;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.util.Collections.emptyList;
import static java.util.Collections.sort;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.next;

public class WordRepetitionProgressServiceImpl implements WordRepetitionProgressService {
    public static final String SPLITER = ",";
    private final SentenceMapper sentenceMapper;
    private final SentenceDao sentenceDao;
    private final WordSetMapper wordSetMapper;
    private WordRepetitionProgressDao exerciseDao;
    private WordSetDao wordSetDao;
    private ObjectMapper mapper;
    private int wordSetSize = 12;

    public WordRepetitionProgressServiceImpl(WordRepetitionProgressDao exerciseDao, WordSetDao wordSetDao, SentenceDao sentenceDao, ObjectMapper mapper) {
        this.exerciseDao = exerciseDao;
        this.wordSetDao = wordSetDao;
        this.sentenceDao = sentenceDao;
        this.mapper = mapper;
        this.sentenceMapper = new SentenceMapper(mapper);
        this.wordSetMapper = new WordSetMapper(mapper);
    }

    @Override
    public List<Sentence> findByWordAndWordSetId(Word2Tokens word) {
        WordSetMapping mapping = wordSetDao.findById(word.getSourceWordSetId());
        WordSet wordSet = wordSetMapper.toDto(mapping);
        List<WordRepetitionProgressMapping> exercises = exerciseDao.findByWordIndexAndWordSetId(wordSet.getWords().indexOf(word), word.getSourceWordSetId());
        if (exercises.isEmpty()) {
            return emptyList();
        }
        WordRepetitionProgressMapping exercise = exercises.get(0);
        if (StringUtils.isEmpty(exercise.getSentenceIds())) {
            return emptyList();
        }
        return getSentence(exercise);
    }

    @Override
    public void save(Word2Tokens word, List<Sentence> sentences) {
        WordSetMapping mapping = wordSetDao.findById(word.getSourceWordSetId());
        WordSet wordSet = wordSetMapper.toDto(mapping);
        WordRepetitionProgressMapping exercise = exerciseDao.findByWordIndexAndWordSetId(wordSet.getWords().indexOf(word), word.getSourceWordSetId()).get(0);
        joinSentenceIds(sentences, exercise);
        exercise.setUpdatedDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
        exerciseDao.createNewOrUpdate(exercise);
    }

    @Override
    public void shiftSentences(Word2Tokens word) {
        WordSetMapping mapping = wordSetDao.findById(word.getSourceWordSetId());
        WordSet wordSet = wordSetMapper.toDto(mapping);
        WordRepetitionProgressMapping exercise = exerciseDao.findByWordIndexAndWordSetId(wordSet.getWords().indexOf(word), word.getSourceWordSetId()).get(0);
        List<String> ids = Lists.newArrayList(exercise.getSentenceIds().split(","));
        for (int i = 0; i < ids.size(); i++) {
            if (StringUtils.isEmpty(ids.get(i))) {
                ids.remove(i);
                i--;
            } else {
                String first = ids.remove(i);
                ids.add(first);
                break;
            }
        }
        exercise.setSentenceIds(StringUtils.join(ids.toArray(), ","));
        exerciseDao.createNewOrUpdate(exercise);
    }

    private void joinSentenceIds(List<Sentence> sentences, WordRepetitionProgressMapping exercise) {
        List<String> ids = new LinkedList<>();
        for (Sentence sentence : sentences) {
            ids.add(sentence.getId());
        }
        exercise.setSentenceIds(StringUtils.join(ids.toArray(), ","));
    }

    @Override
    public void cleanByWordSetId(int wordSetId) {
        exerciseDao.cleanByWordSetId(wordSetId);
    }

    @Override
    public void createSomeIfNecessary(List<Word2Tokens> words) {
        List<WordRepetitionProgressMapping> wordsEx = new LinkedList<>();
        for (int wordIndex = 0; wordIndex < words.size(); wordIndex++) {
            Word2Tokens word = words.get(wordIndex);
            List<WordRepetitionProgressMapping> alreadyCreatedWord = exerciseDao.findByWordIndexAndWordSetId(wordIndex, word.getSourceWordSetId());
            if (alreadyCreatedWord != null && !alreadyCreatedWord.isEmpty()) {
                continue;
            }
            WordRepetitionProgressMapping exercise = new WordRepetitionProgressMapping();
            exercise.setStatus(FIRST_CYCLE.name());
            exercise.setWordIndex(wordIndex);
            exercise.setWordSetId(word.getSourceWordSetId());
            exercise.setUpdatedDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
            wordsEx.add(exercise);
        }
        exerciseDao.createAll(wordsEx);
    }

    @Override
    public void markNewCurrentWordByWordSetIdAndWord(int wordSetId, Word2Tokens newCurrentWord) {
        WordSetMapping exp = wordSetDao.findById(wordSetId);
        WordSet wordSet = wordSetMapper.toDto(exp);
        int wordIndex = wordSet.getWords().indexOf(newCurrentWord);
        List<WordRepetitionProgressMapping> exercises = exerciseDao.findByStatusAndByWordSetId(exp.getStatus(), wordSetId);
        for (WordRepetitionProgressMapping exercise : exercises) {
            if (wordIndex == exercise.getWordIndex()) {
                exercise.setCurrent(true);
                exerciseDao.createNewOrUpdate(exercise);
                return;
            }
        }
    }

    @Override
    public List<Word2Tokens> getLeftOverOfWordSetByWordSetId(int wordSetId) {
        WordSetMapping exp = wordSetDao.findById(wordSetId);
        List<WordRepetitionProgressMapping> exercises = exerciseDao.findByStatusAndByWordSetId(exp.getStatus(), wordSetId);
        LinkedList<Word2Tokens> result = new LinkedList<>();
        for (WordRepetitionProgressMapping exercise : exercises) {
            result.add(getWord2Tokens(exercise));
        }
        return result;
    }

    @Override
    public List<WordSet> findFinishedWordSetsSortByUpdatedDate(long limit, int olderThenInHours) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        //cal.add(Calendar.SECOND, -olderThenInHours);
        cal.add(Calendar.HOUR, -olderThenInHours);
        List<WordRepetitionProgressMapping> exercises = exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(limit * wordSetSize, cal.getTime(), FINISHED.name());
        Iterator<WordRepetitionProgressMapping> iterator = exercises.iterator();
        while (iterator.hasNext()) {
            WordRepetitionProgressMapping exe = iterator.next();
            cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            // 5, 7, 13, 23, 37,
            //cal.add(Calendar.SECOND, -countHours(olderThenInHours, exe.getRepetitionCounter()));
            cal.add(Calendar.HOUR, -(countHours(olderThenInHours, exe.getRepetitionCounter())));
            if (exe.getUpdatedDate().after(cal.getTime())) {
                iterator.remove();
            }
        }
        NavigableMap<Integer, List<Word2Tokens>> tree = getSortedTreeByRepetitionCount(exercises);
        List<WordSet> wordSets = new LinkedList<>();
        for (RepetitionClass clazz : RepetitionClass.values()) {
            insertWordSetGroup(wordSets, tree.subMap(clazz.getFrom(), clazz.getTo()).values(), clazz);
        }
        return wordSets;
    }

    @NonNull
    private NavigableMap<Integer, List<Word2Tokens>> getSortedTreeByRepetitionCount(List<WordRepetitionProgressMapping> exercises) {
        NavigableMap<Integer, List<Word2Tokens>> tree = new TreeMap<>();
        for (WordRepetitionProgressMapping exercise : exercises) {
            Word2Tokens word2Tokens = getWord2Tokens(exercise);
            if (tree.get(exercise.getRepetitionCounter()) == null) {
                tree.put(exercise.getRepetitionCounter(), new LinkedList<Word2Tokens>());
            }
            tree.get(exercise.getRepetitionCounter()).add(word2Tokens);
        }
        return tree;
    }

    private void insertWordSetGroup(List<WordSet> allWordSets, Collection<List<Word2Tokens>> words, RepetitionClass clazz) {
        LinkedList<Word2Tokens> flattenList = new LinkedList<>();
        for (List<Word2Tokens> list : words) {
            flattenList.addAll(list);
        }
        WordSet set = new WordSet();
        set.setWords(new LinkedList<Word2Tokens>());
        set.setRepetitionClass(clazz);
        for (Word2Tokens word : flattenList) {
            set.getWords().add(word);
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

    @Override
    public void putOffCurrentWord(int wordSetId) {
        List<WordRepetitionProgressMapping> current = exerciseDao.findByCurrentAndByWordSetId(wordSetId);
        if (isNotThereCurrentExercise(current)) {
            return;
        }
        WordRepetitionProgressMapping mapping = current.get(0);
        mapping.setCurrent(false);
        exerciseDao.createNewOrUpdate(mapping);
    }

    @Override
    public Word2Tokens getCurrentWord(int wordSetId) {
        List<WordRepetitionProgressMapping> current = exerciseDao.findByCurrentAndByWordSetId(wordSetId);
        if (isNotThereCurrentExercise(current)) {
            return null;
        }
        return getWord2Tokens(current.get(0));
    }

    private Word2Tokens getWord2Tokens(WordRepetitionProgressMapping mapping) {
        WordSetMapping wordSetMapping = wordSetDao.findById(mapping.getWordSetId());
        WordSet wordSet = wordSetMapper.toDto(wordSetMapping);
        return wordSet.getWords().get(mapping.getWordIndex());
    }

    @Override
    public void moveCurrentWordToNextState(int wordSetId) {
        List<WordRepetitionProgressMapping> current = exerciseDao.findByCurrentAndByWordSetId(wordSetId);
        WordRepetitionProgressMapping mapping = current.get(0);
        mapping.setStatus(next(WordSetProgressStatus.valueOf(mapping.getStatus())).name());
        mapping.setUpdatedDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
        exerciseDao.createNewOrUpdate(mapping);
    }

    private List<Sentence> getSentence(WordRepetitionProgressMapping exercise) {
        String[] sentenceIds = exercise.getSentenceIds().split(SPLITER);
        List<SentenceMapping> sentences = sentenceDao.findAllByIds(sentenceIds);
        if (sentences.isEmpty()) {
            throw new RuntimeException("Sentence wasn't found");
        }
        Map<String, SentenceMapping> hashMap = new HashMap<>();
        for (SentenceMapping sentence : sentences) {
            hashMap.put(sentence.getId(), sentence);
        }
        LinkedList<Sentence> result = new LinkedList<>();
        for (String id : sentenceIds) {
            result.add(sentenceMapper.toDto(hashMap.get(id)));
        }
        return result;
    }

    @Override
    public int markAsRepeated(int wordIndex, int wordSetId, Sentence sentence) {
        WordRepetitionProgressMapping exercise = getWordRepetitionProgressMapping(wordIndex, wordSetId, sentence);
        int counter = exercise.getRepetitionCounter();
        counter++;
        exercise.setRepetitionCounter(counter);
        exercise.setUpdatedDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
        exerciseDao.createNewOrUpdate(exercise);
        return counter;
    }

    @Override
    public int markAsForgottenAgain(int wordIndex, int wordSetId, Sentence sentence) {
        WordRepetitionProgressMapping exercise = getWordRepetitionProgressMapping(wordIndex, wordSetId, sentence);
        int counter = exercise.getForgettingCounter();
        counter++;
        exercise.setForgettingCounter(counter);
        exerciseDao.createNewOrUpdate(exercise);
        return counter;
    }

    @Override
    public List<WordSet> findWordSetOfDifficultWords() {
        List<WordRepetitionProgressMapping> words = exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(Integer.MAX_VALUE, new Date(), FINISHED.name());
        sortByForgettingAndRepetitionCounters(words);
        LinkedList<WordSet> wordSets = new LinkedList<>();
        WordSet current = new WordSet();
        current.setWords(new LinkedList<Word2Tokens>());
        for (WordRepetitionProgressMapping word : words) {
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

    private void sortByForgettingAndRepetitionCounters(List<WordRepetitionProgressMapping> words) {
        sort(words, new Comparator<WordRepetitionProgressMapping>() {
            @Override
            public int compare(WordRepetitionProgressMapping o1, WordRepetitionProgressMapping o2) {
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

    private WordRepetitionProgressMapping getWordRepetitionProgressMapping(int wordIndex, int wordSetId, Sentence sentence) {
        List<WordRepetitionProgressMapping> exercises;
        exercises = exerciseDao.findByWordIndexAndByWordSetIdAndByStatus(
                wordIndex,
                wordSetId,
                FINISHED.name()
        );
        return exercises.get(0);
    }

    private boolean isNotThereCurrentExercise(List<WordRepetitionProgressMapping> current) {
        return current.isEmpty();
    }
}