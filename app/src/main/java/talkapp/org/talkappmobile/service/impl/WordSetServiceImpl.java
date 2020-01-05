package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.impl.WordSetComparator;
import talkapp.org.talkappmobile.dao.NewWordSetDraftDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.NewWordSetDraftMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

public class WordSetServiceImpl implements WordSetService {
    private static final int DEFAULT_TOP_SUM = 20000;
    private final int CUSTOM_WORD_SETS_STARTS_SINCE = 1000000;
    @NonNull
    private final WordSetDao wordSetDao;
    @NonNull
    private final NewWordSetDraftDao newWordSetDraftDao;
    @NonNull
    private final WordSetMapper wordSetMapper;
    private final DataServer server;
    private int wordSetSize = 12;

    public WordSetServiceImpl(@NonNull DataServer server, @NonNull WordSetDao wordSetDao, @NonNull NewWordSetDraftDao newWordSetDraftDao, @NonNull ObjectMapper mapper) {
        this.server = server;
        this.wordSetDao = wordSetDao;
        this.newWordSetDraftDao = newWordSetDraftDao;
        this.wordSetMapper = new WordSetMapper(mapper);
    }

    @Override
    public void resetProgress(WordSet wordSet) {
        WordSetMapping wordSetMapping = wordSetDao.findById(wordSet.getId());
        wordSetMapping.setTrainingExperience(0);
        wordSetMapping.setStatus(FIRST_CYCLE.name());
        wordSetDao.createNewOrUpdate(wordSetMapping);
    }

    @Override
    public void moveToAnotherState(int id, WordSetProgressStatus value) {
        WordSetMapping wordSetMapping = wordSetDao.findById(id);
        wordSetMapping.setStatus(value.name());
        wordSetDao.createNewOrUpdate(wordSetMapping);
    }

    @Override
    public void remove(WordSet wordSet) {
        wordSetDao.removeById(wordSet.getId());
    }

    @Override
    public int getCustomWordSetsStartsSince() {
        return CUSTOM_WORD_SETS_STARTS_SINCE;
    }

    @Override
    public WordSet createNewCustomWordSet(List<WordTranslation> translations) {
        Integer newId = getNewWordSetId();
        int totalTop = countTotalTop(translations);
        List<Word2Tokens> word2Tokens = getWord2Tokens(translations, newId);

        WordSet wordSet = new WordSet();
        wordSet.setId(newId);
        wordSet.setTop(totalTop);
        wordSet.setWords(word2Tokens);
        wordSet.setTopicId("43");

        WordSetMapping mapping = wordSetMapper.toMapping(wordSet);
        wordSetDao.createNewOrUpdate(mapping);
        return wordSetMapper.toDto(mapping);
    }

    @Override
    public void updateWord2Tokens(Word2Tokens newWord2Tokens, Word2Tokens oldWord2Tokens) {
        WordSetMapping wordSetMapping = wordSetDao.findById(newWord2Tokens.getSourceWordSetId());
        WordSet wordSetDto = wordSetMapper.toDto(wordSetMapping);

        int position = wordSetDto.getWords().indexOf(oldWord2Tokens);
        wordSetDto.getWords().set(position, newWord2Tokens);

        wordSetMapping = wordSetMapper.toMapping(wordSetDto);

        wordSetDao.createNewOrUpdate(wordSetMapping);
    }

    @NonNull
    private Integer getNewWordSetId() {
        Integer lastId = wordSetDao.getTheLastCustomWordSetsId();
        Integer newId;
        if (lastId == null || lastId < CUSTOM_WORD_SETS_STARTS_SINCE) {
            newId = CUSTOM_WORD_SETS_STARTS_SINCE;
        } else {
            newId = lastId + 1;
        }
        return newId;
    }

    @NonNull
    private List<Word2Tokens> getWord2Tokens(List<WordTranslation> translations, Integer newId) {
        LinkedList<Word2Tokens> word2Tokens = new LinkedList<>();
        for (WordTranslation translation : translations) {
            word2Tokens.add(new Word2Tokens(translation.getWord(), translation.getTokens(), newId));
        }
        return word2Tokens;
    }

    private int countTotalTop(List<WordTranslation> translations) {
        int totalTop = 0;
        for (WordTranslation translation : translations) {
            if (translation.getTop() == null) {
                totalTop += DEFAULT_TOP_SUM;
            } else {
                totalTop += translation.getTop();
            }
        }
        totalTop /= translations.size();
        return totalTop;
    }

    @Override
    @NonNull
    public NewWordSetDraft getNewWordSetDraft() {
        NewWordSetDraftMapping mapping = newWordSetDraftDao.getNewWordSetDraftById(1);
        if (mapping == null) {
            mapping = new NewWordSetDraftMapping();
            mapping.setWords("");
            return wordSetMapper.toDto(mapping);
        }
        return wordSetMapper.toDto(mapping);
    }

    @Override
    public void save(@NonNull NewWordSetDraft draft) {
        if (draft.getWordTranslations().size() != wordSetSize) {
            throw new RuntimeException("draft.getWordTranslations().size() = " + draft.getWordTranslations().size());
        }
        NewWordSetDraftMapping mapping = wordSetMapper.toMapping(draft);
        newWordSetDraftDao.createNewOrUpdate(mapping);
    }

    @Override
    public WordSet findById(int wordSetId) {
        WordSetMapping mapping = wordSetDao.findById(wordSetId);
        return wordSetMapper.toDto(mapping);
    }

    @Override
    public void save(WordSet wordSet) {
        WordSetMapping wordSetMapping = wordSetMapper.toMapping(wordSet);
        wordSetDao.createNewOrUpdate(wordSetMapping);
    }

    @Override
    public List<WordSet> findAllWordSets() {
        List<WordSet> allWordSets;
        try {
            allWordSets = server.findAllWordSets();
        } catch (InternetConnectionLostException e) {
            return findAllWordSetsLocally();
        }
        if (allWordSets == null) {
            return new LinkedList<>();
        }
        initWordSetIdsOfWord2Tokens(allWordSets);
        return allWordSets;
    }


    protected void initWordSetIdsOfWord2Tokens(List<WordSet> wordSets) {
        for (WordSet wordSet : wordSets) {
            LinkedList<Word2Tokens> newWords = new LinkedList<>();
            for (Word2Tokens word : wordSet.getWords()) {
                newWords.add(new Word2Tokens(word.getWord(), word.getTokens(), wordSet.getId()));
            }
            wordSet.setWords(newWords);
        }
    }

    @Override
    public void saveWordSets(final List<WordSet> incomingSets) {
        LinkedList<WordSetMapping> mappingsForSaving = new LinkedList<>();
        for (WordSet wordSet : incomingSets) {
            HashSet<Word2Tokens> setOfWords = new HashSet<>(wordSet.getWords());
            wordSet.setWords(new LinkedList<>(setOfWords));
            WordSetMapping newSet = wordSetMapper.toMapping(wordSet);
            WordSetMapping old = wordSetDao.findById(wordSet.getId());
            if (old != null) {
                newSet.setStatus(old.getStatus());
                newSet.setTrainingExperience(old.getTrainingExperience());
            }
            mappingsForSaving.add(newSet);
        }
        wordSetDao.refreshAll(mappingsForSaving);
    }

    @Override
    public List<WordSet> findAllWordSetsLocally() {
        List<WordSetMapping> allMappings = wordSetDao.findAll();
        List<WordSet> result = new LinkedList<>();
        for (WordSetMapping mapping : allMappings) {
            result.add(wordSetMapper.toDto(mapping));
        }
        return result;
    }

    @Override
    public List<WordSet> getWordSets(Topic topic) {
        List<WordSet> wordSets;
        if (topic == null) {
            wordSets = server.findAllWordSets();
        } else {
            wordSets = server.findWordSetsByTopicId(topic.getId());
        }
        initWordSetIdsOfWord2Tokens(wordSets);
        Collections.sort(wordSets, new WordSetComparator());
        return wordSets;
    }
}