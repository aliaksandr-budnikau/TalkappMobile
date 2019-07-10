package talkapp.org.talkappmobile.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.SentenceContentScore.CORRUPTED;
import static talkapp.org.talkappmobile.model.SentenceContentScore.INSULT;
import static talkapp.org.talkappmobile.model.SentenceContentScore.POOR;
import static talkapp.org.talkappmobile.service.impl.SentenceServiceImpl.WORDS_NUMBER;

@RunWith(MockitoJUnitRunner.class)
public class SentenceServiceImplTest {
    @Mock
    WordRepetitionProgressService exerciseService;
    @Mock
    private DataServer dataServer;
    @InjectMocks
    private SentenceServiceImpl sentenceService;

    @Test
    public void fetchSentencesFromServerByWordAndWordSetId() {
        // setup
        LinkedList<Sentence> sentences = new LinkedList<>();
        sentences.add(new Sentence());
        sentences.getLast().getTranslations().put("russian", "Текст 1");
        sentences.add(new Sentence());
        sentences.getLast().getTranslations().put("russian", "Текст 1");
        sentences.add(new Sentence());
        sentences.getLast().getTranslations().put("russian", "Текст 2");
        sentences.add(new Sentence());
        sentences.getLast().getTranslations().put("russian", "Текст 1");

        // when
        when(dataServer.findSentencesByWords(any(Word2Tokens.class), anyInt(), anyInt())).thenReturn(sentences);
        List<Sentence> result = sentenceService.fetchSentencesFromServerByWordAndWordSetId(new Word2Tokens("sdfsd", "sdfsd", 2));

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

        Sentence sentence1 = new Sentence();
        sentence1.setId("fds32ddd");
        sentence1.setTranslations(new HashMap<String, String>());
        sentence1.getTranslations().put("russian", "fsdfsdfs");
        Sentence sentence2 = new Sentence();
        sentence2.setId("fds32ddddsas");
        sentence2.setTranslations(new HashMap<String, String>());
        sentence2.getTranslations().put("russian", "fsdfsdfs2");
        List<Sentence> sentences = asList(sentence1, sentence2);

        // when
        when(dataServer.findSentencesByWords(word, WORDS_NUMBER, wordSetId)).thenReturn(sentences);
        List<Sentence> sentencesActual = sentenceService.fetchSentencesFromServerByWordAndWordSetId(word);

        // then
        assertEquals(sentences.get(0), sentencesActual.get(0));
        assertEquals("fds32ddd", sentencesActual.get(0).getId());
        assertEquals(sentences.get(1), sentencesActual.get(1));
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
        List<Sentence> sentences = asList(sentence1, sentence2);

        // when
        when(dataServer.findSentencesByWords(word, WORDS_NUMBER, wordSetId)).thenReturn(sentences);
        List<Sentence> sentencesActual = sentenceService.fetchSentencesFromServerByWordAndWordSetId(word);

        // then
        sentencesActual.clear();
    }

    @Test
    public void findByWord_sentenceNotFound() {
        // setup
        int wordSetId = 3;
        Word2Tokens word = new Word2Tokens("word", "word", wordSetId);

        List<Sentence> sentences = emptyList();

        // when
        when(dataServer.findSentencesByWords(word, WORDS_NUMBER, wordSetId)).thenReturn(sentences);
        List<Sentence> sentencesActual = sentenceService.fetchSentencesFromServerByWordAndWordSetId(word);

        // then
        assertTrue(sentencesActual.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void findByWord_exception() {
        // setup
        int wordSetId = 3;
        Word2Tokens word = new Word2Tokens("word", "word", wordSetId);

        // when
        doThrow(RuntimeException.class).when(dataServer.findSentencesByWords(word, WORDS_NUMBER, wordSetId));
        List<Sentence> sentencesActual = sentenceService.fetchSentencesFromServerByWordAndWordSetId(word);

        // then
        assertTrue(sentencesActual.isEmpty());
    }

    @Test
    public void findByWordAndWordSetId() {
        // setup
        int wordSetId = 3;
        Word2Tokens word = new Word2Tokens("", "", wordSetId);

        // when
        when(exerciseService.findByWordAndWordSetId(word)).thenReturn(singletonList(new Sentence()));
        List<Sentence> list = sentenceService.fetchSentencesNotFromServerByWordAndWordSetId(word);

        // then
        list.clear();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSentence_empty() throws Exception {
        sentenceService.selectSentences(new ArrayList<Sentence>());
    }

    @Test
    public void getSentence_count1() throws Exception {
        // setup
        List<Sentence> sentences = new ArrayList<>();
        Sentence e = new Sentence();
        sentences.add(e);

        // when
        List<Sentence> actualSentences = sentenceService.selectSentences(sentences);

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
        List<Sentence> sentence = sentenceService.selectSentences(sentences);

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
        sentenceService.orderByScore(sentences);

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