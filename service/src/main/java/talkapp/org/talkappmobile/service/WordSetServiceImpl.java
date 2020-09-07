package talkapp.org.talkappmobile.service;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.exceptions.ObjectNotFoundException;
import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.repository.WordSetRepository;

import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

public class WordSetServiceImpl implements WordSetService {
    private static final int DEFAULT_TOP_SUM = 20000;
    private final int CUSTOM_WORD_SETS_STARTS_SINCE = 1000000;
    private final WordSetRepository wordSetRepository;
    private final DataServer server;

    public WordSetServiceImpl(@NonNull DataServer server, @NonNull WordSetRepository wordSetRepository) {
        this.server = server;
        this.wordSetRepository = wordSetRepository;
    }

    @Override
    public void resetProgress(WordSet wordSet) {
        WordSet set = wordSetRepository.findById(wordSet.getId());
        set.setTrainingExperience(0);
        set.setStatus(FIRST_CYCLE);
        wordSetRepository.createNewOrUpdate(set);
    }

    @Override
    public void moveToAnotherState(int id, WordSetProgressStatus value) {
        WordSet wordSet = wordSetRepository.findById(id);
        wordSet.setStatus(value);
        wordSetRepository.createNewOrUpdate(wordSet);
    }

    @Override
    public void remove(WordSet wordSet) {
        wordSetRepository.removeById(wordSet.getId());
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

        wordSetRepository.createNewOrUpdate(wordSet);
        return wordSet;
    }

    @Override
    public void updateWord2Tokens(Word2Tokens newWord2Tokens, Word2Tokens oldWord2Tokens) {
        WordSet wordSetDto = wordSetRepository.findById(newWord2Tokens.getSourceWordSetId());

        int position = wordSetDto.getWords().indexOf(oldWord2Tokens);
        wordSetDto.getWords().set(position, newWord2Tokens);

        wordSetRepository.createNewOrUpdate(wordSetDto);
    }

    @NonNull
    private Integer getNewWordSetId() {
        Integer lastId = wordSetRepository.getTheLastCustomWordSetsId();
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
        return wordSetRepository.getNewWordSetDraft();
    }

    @Override
    public void save(@NonNull NewWordSetDraft draft) {
        wordSetRepository.createNewOrUpdate(draft);
    }

    @Override
    public WordSet findById(int wordSetId) {
        return wordSetRepository.findById(wordSetId);
    }

    @Override
    public void save(WordSet wordSet) {
        wordSetRepository.createNewOrUpdate(wordSet);
    }

    private void initWordSetIdsOfWord2Tokens(List<WordSet> wordSets) {
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
        LinkedList<WordSet> wordSets = new LinkedList<>();
        for (WordSet newSet : incomingSets) {
            HashSet<Word2Tokens> setOfWords = new HashSet<>(newSet.getWords());
            newSet.setWords(new LinkedList<>(setOfWords));
            WordSet old;
            try {
                old = wordSetRepository.findById(newSet.getId());
                newSet.setStatus(old.getStatus());
                newSet.setTrainingExperience(old.getTrainingExperience());
            } catch (ObjectNotFoundException ignored) {

            }
            wordSets.add(newSet);
        }
        wordSetRepository.createNewOrUpdate(wordSets);
    }

    @Override
    public List<WordSet> getWordSets(Topic topic) {
        List<WordSet> wordSets = server.findAllWordSets();
        if (topic != null) {
            List<WordSet> byTopic = new ArrayList<>();
            for (WordSet wordSet : wordSets) {
                if (topic.getId() == Integer.valueOf(wordSet.getTopicId())) {
                    byTopic.add(wordSet);
                }
            }
            wordSets = byTopic;
        }
        initWordSetIdsOfWord2Tokens(wordSets);
        Collections.sort(wordSets, new WordSetComparator());
        return wordSets;
    }
}