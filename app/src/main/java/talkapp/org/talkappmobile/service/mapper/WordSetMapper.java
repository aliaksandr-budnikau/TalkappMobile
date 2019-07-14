package talkapp.org.talkappmobile.service.mapper;

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

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.join;

public class WordSetMapper {
    public static final String SEPARATOR = ",";
    public final CollectionType LINKED_LIST_OF_WORD_2_TOKENS_JAVA_TYPE;
    private final ObjectMapper mapper;

    public WordSetMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        LINKED_LIST_OF_WORD_2_TOKENS_JAVA_TYPE = mapper.getTypeFactory().constructCollectionType(LinkedList.class, Word2Tokens.class);
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
        String[] words = mapping.getWords().split(",");
        return new NewWordSetDraft(asList(words));
    }

    public NewWordSetDraftMapping toMapping(NewWordSetDraft draft) {
        List<String> words = new ArrayList<>(draft.getWords());
        for (int i = 0; i < words.size(); i++) {
            String fixedWord = words.get(i).replaceAll(SEPARATOR, " ");
            String result = fixedWord.length() > 31 ? fixedWord.substring(0, 31) : fixedWord;
            words.set(i, result);
        }
        NewWordSetDraftMapping mapping = new NewWordSetDraftMapping();
        mapping.setId(1);
        mapping.setWords(join(draft.getWords(), SEPARATOR));
        return mapping;
    }
}