package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
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
import talkapp.org.talkappmobile.mappings.SentenceIdMapping;
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
    private final CollectionType LINKED_LIST_OF_SENTENCE_ID_JAVA_TYPE;
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
        LINKED_LIST_OF_SENTENCE_ID_JAVA_TYPE = mapper.getTypeFactory().constructCollectionType(LinkedList.class, SentenceIdMapping.class);
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
        if (StringUtils.isEmpty(exercise.getSentenceIds()) || getSentenceIdMappings(exercise.getSentenceIds()).isEmpty()) {
            return emptyList();
        }
        return getSentence(exercise);
    }

    @Override
    public void save(Word2Tokens word, List<Sentence> sentences) {
        WordSetMapping mapping = wordSetDao.findById(word.getSourceWordSetId());
        WordSet wordSet = wordSetMapper.toDto(mapping);
        WordRepetitionProgressMapping exercise = exerciseDao.findByWordIndexAndWordSetId(wordSet.getWords().indexOf(word), word.getSourceWordSetId()).get(0);
        List<SentenceIdMapping> ids = new LinkedList<>();
        for (Sentence sentence : sentences) {
            String[] split = sentence.getId().split("#");
            ids.add(new SentenceIdMapping(split[0], Integer.valueOf(split[2])));
        }
        setSentencesIds(exercise, ids);
        exercise.setUpdatedDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
        exerciseDao.createNewOrUpdate(exercise);
    }

    private void setSentencesIds(WordRepetitionProgressMapping exercise, List<SentenceIdMapping> ids) {
        try {
            exercise.setSentenceIds(mapper.writeValueAsString(ids));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void shiftSentences(Word2Tokens word) {
        WordSetMapping mapping = wordSetDao.findById(word.getSourceWordSetId());
        WordSet wordSet = wordSetMapper.toDto(mapping);
        WordRepetitionProgressMapping exercise = exerciseDao.findByWordIndexAndWordSetId(wordSet.getWords().indexOf(word), word.getSourceWordSetId()).get(0);
        List<SentenceIdMapping> ids = getSentenceIdMappings(exercise);
        for (int i = 0; i < ids.size(); i++) {
            if (isEmpty(ids.get(i), wordSet.getWords().get(exercise.getWordIndex()).getWord())) {
                ids.remove(i);
                i--;
            } else {
                SentenceIdMapping first = ids.remove(i);
                ids.add(first);
                break;
            }
        }
        setSentencesIds(exercise, ids);
        exerciseDao.createNewOrUpdate(exercise);
    }

    private boolean isEmpty(SentenceIdMapping id, String word) {
        return StringUtils.isEmpty(id.getSentenceId()) || StringUtils.isEmpty(word) || id.getLengthInWords() == 0;
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
        WordSetMapping wordSetMapping = wordSetDao.findById(exercise.getWordSetId());
        WordSet wordSet = wordSetMapper.toDto(wordSetMapping);
        List<SentenceIdMapping> ids = getSentenceIdMappings(exercise);
        String[] sentenceIds = new String[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            try {
                SentenceIdMapping idMapping = ids.get(i);
                idMapping.setWord(wordSet.getWords().get(exercise.getWordIndex()).getWord());
                sentenceIds[i] = mapper.writeValueAsString(idMapping);
            } catch (JsonProcessingException e) {
                throw new RuntimeException();
            }
        }
        List<SentenceMapping> sentences = sentenceDao.findAllByIds(sentenceIds);
        if (sentences.isEmpty()) {
            return emptyList();
        }
        Map<String, SentenceMapping> hashMap = new HashMap<>();
        for (SentenceMapping sentence : sentences) {
            hashMap.put(sentence.getId(), sentence);
        }
        LinkedList<Sentence> result = new LinkedList<>();
        for (String id : sentenceIds) {
            SentenceMapping mapping = hashMap.get(id);
            if (mapping != null) {
                result.add(sentenceMapper.toDto(mapping));
            }
        }
        return result;
    }

    private List<SentenceIdMapping> getSentenceIdMappings(WordRepetitionProgressMapping exercise) {
        return getSentenceIdMappings(exercise.getSentenceIds());
    }

    private List<SentenceIdMapping> getSentenceIdMappings(String sentenceIds) {
        List<SentenceIdMapping> ids;
        try {
            ids = mapper.readValue(sentenceIds, LINKED_LIST_OF_SENTENCE_ID_JAVA_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return ids;
    }


    @Override
    public int markAsRepeated(Word2Tokens word) {
        WordRepetitionProgressMapping exercise = getWordRepetitionProgressMapping(word);
        int counter = exercise.getRepetitionCounter();
        counter++;
        exercise.setRepetitionCounter(counter);
        exercise.setUpdatedDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
        exerciseDao.createNewOrUpdate(exercise);
        return counter;
    }

    @Override
    public int markAsForgottenAgain(Word2Tokens word) {
        WordRepetitionProgressMapping exercise = getWordRepetitionProgressMapping(word);
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

    @Override
    public void updateSentenceIds(Word2Tokens newWord2Token, Word2Tokens oldWord2Token) {
        WordSetMapping mapping = wordSetDao.findById(newWord2Token.getSourceWordSetId());
        WordSet wordSet = wordSetMapper.toDto(mapping);
        List<WordRepetitionProgressMapping> exercises = exerciseDao.findByWordIndexAndWordSetId(wordSet.getWords().indexOf(newWord2Token), newWord2Token.getSourceWordSetId());
        if (exercises.isEmpty()) {
            return;
        }
        WordRepetitionProgressMapping exercise = exercises.get(0);
        int wordsNumber = 6;
        List<SentenceMapping> sentences = sentenceDao.findAllByWord(newWord2Token.getWord(), wordsNumber);
        LinkedList<SentenceIdMapping> sentenceIds = new LinkedList<>();
        for (SentenceMapping sentence : sentences) {
            try {
                sentenceIds.add(mapper.readValue(sentence.getId(), SentenceIdMapping.class));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        try {
            exercise.setSentenceIds(mapper.writeValueAsString(sentenceIds));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        exerciseDao.createNewOrUpdate(exercise);
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

    private WordRepetitionProgressMapping getWordRepetitionProgressMapping(Word2Tokens word) {
        int wordSetId = word.getSourceWordSetId();
        WordSetMapping mapping = wordSetDao.findById(wordSetId);
        WordSet wordDto = wordSetMapper.toDto(mapping);
        int index = wordDto.getWords().indexOf(word);
        List<WordRepetitionProgressMapping> exercises;
        exercises = exerciseDao.findByWordIndexAndByWordSetIdAndByStatus(
                index,
                wordSetId,
                FINISHED.name()
        );
        return exercises.get(0);
    }

    private boolean isNotThereCurrentExercise(List<WordRepetitionProgressMapping> current) {
        return current.isEmpty();
    }
}