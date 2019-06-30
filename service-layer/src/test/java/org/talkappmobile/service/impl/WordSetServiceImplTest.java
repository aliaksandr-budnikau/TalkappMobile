package org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.talkappmobile.dao.WordSetDao;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.model.WordTranslation;
import org.talkappmobile.service.WordSetExperienceUtils;
import org.talkappmobile.service.mapper.WordSetMapper;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

@RunWith(MockitoJUnitRunner.class)
public class WordSetServiceImplTest {
    @Mock
    private WordSetDao wordSetDao;
    @Mock
    private WordSetExperienceUtils experienceUtils;
    private WordSetServiceImpl wordSetService;

    @Before
    public void setUp() throws Exception {
        wordSetService = new WordSetServiceImpl(wordSetDao, experienceUtils, new WordSetMapper(new ObjectMapper()));
    }

    @Test
    public void createNewCustomWordSet_noCustomWordSets() {
        String word = "word";
        int lastId = 5;
        int top = 1000;
        String topicId = "43";

        LinkedList<WordTranslation> translations = new LinkedList<>();
        translations.add(new WordTranslation());
        translations.getLast().setId("1");
        translations.getLast().setLanguage("russian");
        translations.getLast().setTokens(word);
        translations.getLast().setTop(top);
        translations.getLast().setTranslation("слово");
        translations.getLast().setWord(word);

        when(wordSetDao.getTheLastCustomWordSetsId()).thenReturn(lastId);
        WordSet actualWordSet = wordSetService.createNewCustomWordSet(translations);

        assertEquals(actualWordSet.getWords().get(0).getWord(), word);
        assertEquals(actualWordSet.getWords().get(0).getTokens(), word);
        assertEquals(actualWordSet.getWords().get(0).getSourceWordSetId().intValue(), wordSetService.getCustomWordSetsStartsSince());
        assertEquals(actualWordSet.getId(), wordSetService.getCustomWordSetsStartsSince());
        assertEquals(actualWordSet.getTop().intValue(), top);
        assertEquals(actualWordSet.getStatus(), FIRST_CYCLE);
        assertEquals(actualWordSet.getRepetitionClass(), null);
        assertEquals(actualWordSet.getTopicId(), topicId);
        assertEquals(actualWordSet.getTrainingExperience(), 0);
    }

    @Test
    public void createNewCustomWordSet_thereIsCustomWordSets() {
        String word = "word";
        int lastId = 1000100;
        int top = 1000;
        String topicId = "43";
        int newId = lastId + 1;

        LinkedList<WordTranslation> translations = new LinkedList<>();
        translations.add(new WordTranslation());
        translations.getLast().setId("1");
        translations.getLast().setLanguage("russian");
        translations.getLast().setTokens(word);
        translations.getLast().setTop(top);
        translations.getLast().setTranslation("слово");
        translations.getLast().setWord(word);

        when(wordSetDao.getTheLastCustomWordSetsId()).thenReturn(lastId);
        WordSet actualWordSet = wordSetService.createNewCustomWordSet(translations);

        assertEquals(actualWordSet.getWords().get(0).getWord(), word);
        assertEquals(actualWordSet.getWords().get(0).getTokens(), word);
        assertEquals(actualWordSet.getWords().get(0).getSourceWordSetId().intValue(), newId);
        assertEquals(actualWordSet.getId(), newId);
        assertEquals(actualWordSet.getTop().intValue(), top);
        assertEquals(actualWordSet.getStatus(), FIRST_CYCLE);
        assertEquals(actualWordSet.getRepetitionClass(), null);
        assertEquals(actualWordSet.getTopicId(), topicId);
        assertEquals(actualWordSet.getTrainingExperience(), 0);
    }
}