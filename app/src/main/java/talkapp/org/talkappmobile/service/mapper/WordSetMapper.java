package talkapp.org.talkappmobile.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.mappings.NewWordSetDraftMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.model.WordTranslation;

import static java.util.Arrays.asList;

public class WordSetMapper {
    public static final int MAX_LENGTH_OF_ONE_ROW = 250;
    public final CollectionType LINKED_LIST_OF_WORD_2_TOKENS_JAVA_TYPE;
    public final CollectionType LINKED_LIST_OF_STRINGS_JAVA_TYPE;
    private final ObjectMapper mapper;

    public WordSetMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        LINKED_LIST_OF_WORD_2_TOKENS_JAVA_TYPE = mapper.getTypeFactory().constructCollectionType(LinkedList.class, Word2Tokens.class);
        LINKED_LIST_OF_STRINGS_JAVA_TYPE = mapper.getTypeFactory().constructCollectionType(LinkedList.class, WordAndTransaction.class);
    }

    public WordSetMapping toMapping(WordSet wordSet) {
        WordSetMapping mapping = new WordSetMapping();
        mapping.setId(String.valueOf(wordSet.getId()));

        try {
            mapping.setWords(mapper.writeValueAsString(wordSet.getWords()));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        mapping.setTopicId(wordSet.getTopicId());
        mapping.setTop(wordSet.getTop());
        mapping.setTrainingExperience(wordSet.getTrainingExperience());
        mapping.setStatus(wordSet.getStatus().name());
        return mapping;
    }

    public WordSet toDto(WordSetMapping mapping) {
        WordSet wordSet = new WordSet();
        wordSet.setId(Integer.valueOf(mapping.getId()));
        wordSet.setTopicId(mapping.getTopicId());
        wordSet.setTop(mapping.getTop());
        wordSet.setTrainingExperience(mapping.getTrainingExperience());
        wordSet.setStatus(WordSetProgressStatus.valueOf(mapping.getStatus()));

        List<Word2Tokens> words;
        try {
            words = mapper.readValue(mapping.getWords(), LINKED_LIST_OF_WORD_2_TOKENS_JAVA_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        wordSet.setWords(words);
        return wordSet;
    }

    public NewWordSetDraft toDto(NewWordSetDraftMapping mapping) {
        List<WordAndTransaction> words;
        try {
            words = mapper.readValue(mapping.getWords(), LINKED_LIST_OF_STRINGS_JAVA_TYPE);
        } catch (IOException e) {
            words = asList(new WordAndTransaction(), new WordAndTransaction(), new WordAndTransaction(), new WordAndTransaction(), new WordAndTransaction(),
                    new WordAndTransaction(), new WordAndTransaction(), new WordAndTransaction(), new WordAndTransaction(), new WordAndTransaction(),
                    new WordAndTransaction(), new WordAndTransaction());
        }
        LinkedList<WordTranslation> result = new LinkedList<>();
        for (WordAndTransaction word : words) {
            WordTranslation translation = new WordTranslation();
            translation.setWord(word.getWord());
            translation.setTranslation(word.getTransaction());
            result.add(translation);
        }
        return new NewWordSetDraft(result);
    }

    public NewWordSetDraftMapping toMapping(NewWordSetDraft draft) {
        List<WordTranslation> wordTranslations = draft.getWordTranslations();
        List<WordAndTransaction> result = new ArrayList<>();
        for (int i = 0; i < wordTranslations.size(); i++) {
            WordTranslation word = wordTranslations.get(i);
            result.add(new WordAndTransaction(
                    word.getWord() == null ? null : word.getWord().length() > MAX_LENGTH_OF_ONE_ROW ? word.getWord().substring(0, MAX_LENGTH_OF_ONE_ROW) : word.getWord(),
                    word.getTranslation() == null ? null : word.getTranslation().length() > MAX_LENGTH_OF_ONE_ROW ? word.getTranslation().substring(0, MAX_LENGTH_OF_ONE_ROW) : word.getTranslation()
            ));
        }
        NewWordSetDraftMapping mapping = new NewWordSetDraftMapping();
        mapping.setId(1);
        try {
            mapping.setWords(mapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return mapping;
    }

    private static class WordAndTransaction {
        private String word;
        private String transaction;

        public WordAndTransaction() {
        }

        public WordAndTransaction(String word, String transaction) {
            this.word = word;
            this.transaction = transaction;
        }

        public String getWord() {
            return word;
        }

        public String getTransaction() {
            return transaction;
        }
    }
}