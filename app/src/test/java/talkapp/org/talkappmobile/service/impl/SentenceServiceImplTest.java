package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.SentenceProvider;
import talkapp.org.talkappmobile.service.mapper.SentenceMapper;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.SentenceContentScore.CORRUPTED;
import static talkapp.org.talkappmobile.model.SentenceContentScore.INSULT;
import static talkapp.org.talkappmobile.model.SentenceContentScore.POOR;
import static talkapp.org.talkappmobile.service.impl.SentenceProviderImpl.WORDS_NUMBER;

@RunWith(MockitoJUnitRunner.class)
public class SentenceServiceImplTest {
    @Mock
    private DataServer dataServer;
    @Mock
    private WordSetDao wordSetDao;
    @Mock
    private SentenceDao sentenceDao;
    private SentenceProvider sentenceProvider;
    @Mock
    private WordRepetitionProgressDao progressDao;
    private SentenceServiceImpl sentenceService;
    private WordProgressSentenceProviderDecorator wordProgressSentenceProviderDecorator;
    private SentenceMapper sentenceMapper;

    @Before
    public void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        sentenceMapper = new SentenceMapper(mapper);
        sentenceService = new SentenceServiceImpl(dataServer, sentenceDao, mapper);
        WordSetRepositoryImpl wordSetRepository = new WordSetRepositoryImpl(wordSetDao, mapper);
        sentenceProvider = new SentenceProviderImpl(wordSetRepository, progressDao, sentenceDao, mapper);
        wordProgressSentenceProviderDecorator = new WordProgressSentenceProviderDecorator(sentenceProvider, wordSetRepository, progressDao, mapper);
    }

    @Test
    public void fetchSentencesFromServerByWordAndWordSetId() {
        // setup
        LinkedList<SentenceMapping> sentences = new LinkedList<>();
        sentences.add(new SentenceMapping());
        sentences.getLast().setTranslations("{\"russian\":\"Текст 1\"}");
        sentences.getLast().setTokens("[]");
        sentences.add(new SentenceMapping());
        sentences.getLast().setTranslations("{\"russian\":\"Текст 1\"}");
        sentences.getLast().setTokens("[]");
        sentences.add(new SentenceMapping());
        sentences.getLast().setTranslations("{\"russian\":\"Текст 2\"}");
        sentences.getLast().setTokens("[]");
        sentences.add(new SentenceMapping());
        sentences.getLast().setTranslations("{\"russian\":\"Текст 1\"}");
        sentences.getLast().setTokens("[]");

        // when

        when(sentenceDao.findAllByWord(any(String.class), anyInt())).thenReturn(sentences);
        List<Sentence> result = wordProgressSentenceProviderDecorator.getFromDB(new Word2Tokens("sdfsd", "sdfsd", 2));

        // then
        assertEquals(2, result.size());
        assertEquals("Текст 1", result.get(0).getTranslations().get("russian"));
        assertEquals("Текст 2", result.get(1).getTranslations().get("russian"));
    }


    @Test
    public void findByWord_sentenceFound() {
        // setup
        int wordSetId = 3;
        Word2Tokens word = new Word2Tokens("word", "word", wordSetId);

        SentenceMapping sentence1 = new SentenceMapping();
        sentence1.setId("fds32ddd");
        sentence1.setTokens("[]");
        sentence1.setTranslations("{\"russian\":\"fsdfsdfs\"}");
        SentenceMapping sentence2 = new SentenceMapping();
        sentence2.setId("fds32ddddsas");
        sentence2.setTokens("[]");
        sentence2.setTranslations("{\"russian\":\"fsdfsdfs2\"}");
        List<SentenceMapping> sentences = asList(sentence1, sentence2);

        // when
        when(sentenceDao.findAllByWord(word.getWord(), WORDS_NUMBER)).thenReturn(sentences);
        List<Sentence> sentencesActual = wordProgressSentenceProviderDecorator.getFromDB(word);

        // then
        assertEquals(sentenceMapper.toDto(sentence1), sentencesActual.get(0));
        assertEquals("fds32ddd", sentencesActual.get(0).getId());
        assertEquals(sentenceMapper.toDto(sentence2), sentencesActual.get(1));
        assertEquals("fds32ddddsas", sentencesActual.get(1).getId());
    }

    @Test
    public void findByWord_sentenceInImmutableList() {
        // setup
        int wordSetId = 3;
        Word2Tokens word = new Word2Tokens("word", "word", wordSetId);

        Sentence sentence1 = new Sentence();
        sentence1.setId("fds32ddd");
        Sentence sentence2 = new Sentence();
        sentence2.setId("fds32ddddsas");
        List<SentenceMapping> sentences = asList(sentenceMapper.toMapping(sentence1), sentenceMapper.toMapping(sentence2));

        // when
        when(sentenceDao.findAllByWord(word.getWord(), WORDS_NUMBER)).thenReturn(sentences);
        List<Sentence> sentencesActual = wordProgressSentenceProviderDecorator.getFromDB(word);

        // then
        sentencesActual.clear();
    }

    @Test(expected = RuntimeException.class)
    public void findByWord_exception() {
        // setup
        int wordSetId = 3;
        Word2Tokens word = new Word2Tokens("word", "word", wordSetId);

        // when
        doThrow(RuntimeException.class).when(sentenceDao.findAllByWord(word.getWord(), WORDS_NUMBER));
        List<Sentence> sentencesActual = wordProgressSentenceProviderDecorator.getFromDB(word);

        // then
        assertTrue(sentencesActual.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSentence_empty() throws Exception {
        wordProgressSentenceProviderDecorator.selectSentences(new ArrayList<Sentence>());
    }

    @Test
    public void getSentence_count1() throws Exception {
        // setup
        List<Sentence> sentences = new ArrayList<>();
        Sentence e = new Sentence();
        sentences.add(e);

        // when
        List<Sentence> actualSentences = wordProgressSentenceProviderDecorator.selectSentences(sentences);

        // then
        assertEquals(sentences, actualSentences);
    }

    @Test
    public void getSentence_count2() throws Exception {
        // setup
        List<Sentence> sentences = new ArrayList<>();
        Sentence e1 = new Sentence();
        sentences.add(e1);
        Sentence e2 = new Sentence();
        sentences.add(e2);

        // when
        List<Sentence> sentence = wordProgressSentenceProviderDecorator.selectSentences(sentences);

        // then
        assertTrue(sentence.get(0) == e1 || sentence.get(0) == e2);
    }

    @Test
    public void orderByScore() {
        // setup
        LinkedList<Sentence> sentences = new LinkedList<>();
        for (SentenceContentScore score : SentenceContentScore.values()) {
            Sentence sentence = new Sentence();
            sentence.setContentScore(score);
            sentences.addFirst(sentence);
        }
        sentences.addLast(new Sentence());
        sentences.addLast(new Sentence());
        sentences.addLast(new Sentence());
        sentences.addLast(new Sentence());
        sentences.addLast(new Sentence());
        sentences.addLast(new Sentence());
        sentences.addLast(new Sentence());

        Collections.shuffle(sentences);

        // when
        wordProgressSentenceProviderDecorator.orderByScore(sentences);

        // then
        Iterator<Sentence> iterator = sentences.iterator();
        Sentence sentence = iterator.next();
        assertEquals(null, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(null, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(null, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(null, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(null, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(null, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(null, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(POOR, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(CORRUPTED, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(INSULT, sentence.getContentScore());
    }
}