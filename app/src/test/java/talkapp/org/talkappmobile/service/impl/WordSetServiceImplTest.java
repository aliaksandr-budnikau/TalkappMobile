package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;

import talkapp.org.talkappmobile.dao.CurrentWordSetDao;
import talkapp.org.talkappmobile.dao.NewWordSetDraftDao;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

@RunWith(MockitoJUnitRunner.class)
public class WordSetServiceImplTest {
    @Mock
    private WordSetDao wordSetDao;
    @Mock
    private NewWordSetDraftDao newWordSetDraftDao;
    @Mock
    private SentenceDao sentenceDao;
    @Mock
    private WordSetExperienceUtils experienceUtils;
    @Mock
    private WordTranslationDao wordTranslationDao;
    @Mock
    private CurrentWordSetDao currentWordSetDao;
    private WordSetServiceImpl wordSetService;

    @Before
    public void setUp() throws Exception {
        wordSetService = new WordSetServiceImpl(wordSetDao, currentWordSetDao, newWordSetDraftDao, experienceUtils, new ObjectMapper());
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