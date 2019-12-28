package talkapp.org.talkappmobile.activity.interactor.impl;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import static java.util.Collections.shuffle;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public abstract class AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private static final String TAG = AbstractPracticeWordSetInteractor.class.getSimpleName();
    private final Logger logger;
    private final Context context;
    private final RefereeService refereeService;
    private final AudioStuffFactory audioStuffFactory;
    private final WordRepetitionProgressService exerciseService;
    private final SentenceService sentenceService;
    private final WordSetService wordSetService;

    public AbstractPracticeWordSetInteractor(Logger logger,
                                             Context context,
                                             RefereeService refereeService,
                                             WordRepetitionProgressService exerciseService,
                                             SentenceService sentenceService,
                                             WordSetService wordSetService,
                                             AudioStuffFactory audioStuffFactory) {
        this.logger = logger;
        this.context = context;
        this.refereeService = refereeService;
        this.exerciseService = exerciseService;
        this.sentenceService = sentenceService;
        this.wordSetService = wordSetService;
        this.audioStuffFactory = audioStuffFactory;
    }

    protected boolean checkAccuracyOfAnswer(String answer, Word2Tokens word, Sentence sentence, OnPracticeWordSetListener listener) {
        logger.i(TAG, "check answer {} ", answer);
        if (isEmpty(answer)) {
            logger.i(TAG, "answer is empty");
            listener.onAnswerEmpty();
            return false;
        }
        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setActualAnswer(answer);
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setCurrentWord(word);

        logger.i(TAG, "checking ... {}", uncheckedAnswer);
        if (!refereeService.checkAnswer(uncheckedAnswer)) {
            logger.i(TAG, "accuracy is too low");
            listener.onAccuracyTooLowError();
            return false;
        }
        logger.i(TAG, "accuracy is ok");
        return true;
    }

    @Override
    public void playVoice(Uri voiceRecordUri, OnPracticeWordSetListener listener) {
        if (voiceRecordUri == null) {
            logger.i(TAG, "voice record uri is empty");
            return;
        }
        MediaPlayer mp = null;
        try {
            listener.onStartPlaying();
            try {
                mp = audioStuffFactory.createMediaPlayer();
                mp.setDataSource(context, voiceRecordUri);
                mp.prepare();
                mp.start();
                logger.i(TAG, "start playing {}", voiceRecordUri);
                while (mp.isPlaying()) {
                    logger.i(TAG, "playing...");
                    Thread.sleep(500);
                }
                logger.i(TAG, "stop playing");
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } finally {
            listener.onStopPlaying();
        }
    }

    @Override
    public void initialiseWordsSequence(OnPracticeWordSetListener listener) {
        WordSet wordSet = wordSetService.getCurrent();
        exerciseService.createSomeIfNecessary(wordSet.getWords());
    }

    @Override
    public void scoreSentence(Sentence sentence, SentenceContentScore score, OnPracticeWordSetListener listener) {
        sentence.setContentScore(score);
        if (sentenceService.classifySentence(sentence)) {
            listener.onScoringSuccessful();
        } else {
            listener.onScoringUnsuccessful();
        }
    }

    @Override
    public void changeSentence(OnPracticeWordSetListener listener) {
        WordSet wordSet = wordSetService.getCurrent();
        Word2Tokens word = exerciseService.getCurrentWord(wordSet.getId());
        initialiseSentence(word, listener);
        listener.onSentenceChanged();
    }

    @Override
    public void changeSentence(Word2Tokens currentWord, List<Sentence> sentences, OnPracticeWordSetListener listener) {
        replaceSentence(sentences, currentWord, listener);
        listener.onSentenceChanged();
    }

    protected void replaceSentence(List<Sentence> sentences, Word2Tokens word, final OnPracticeWordSetListener listener) {
        List<Sentence> shuffledSentences = new ArrayList<>(sentences);
        shuffle(shuffledSentences);

        if (shuffledSentences.size() > 1 && shuffledSentences.remove(getCurrentSentence())) {
            Sentence removedOne = this.getCurrentSentence();
            setCurrentSentence(shuffledSentences.get(0));
            shuffledSentences.add(removedOne);
        } else {
            setCurrentSentence(shuffledSentences.get(0));
        }
        exerciseService.save(word, shuffledSentences);
        listener.onSentencesFound(getCurrentSentence(), word);
    }

    @Override
    public void findSentencesForChange(Word2Tokens currentWord, OnPracticeWordSetListener listener) {
        List<Sentence> alreadyPickedSentences = exerciseService.findByWordAndWordSetId(currentWord);
        List<Sentence> sentences;
        try {
            sentences = sentenceService.fetchSentencesFromServerByWordAndWordSetId(currentWord);
        } catch (LocalCacheIsEmptyException e) {
            listener.onNoSentencesToChange();
            return;
        }
        listener.onGotSentencesToChange(sentences, alreadyPickedSentences, currentWord);
    }

    protected Word2Tokens peekRandomWordWithoutCurrentWord(List<Word2Tokens> words, Word2Tokens currentWord) {
        if (words.isEmpty()) {
            return null;
        }
        if (words.size() == 1) {
            return words.get(0);
        }
        LinkedList<Word2Tokens> copyWords = new LinkedList<>(words);
        copyWords.remove(currentWord);
        shuffle(copyWords);
        return copyWords.getFirst();
    }

    public void prepareOriginalTextClickEM(OnPracticeWordSetListener listener) {
        listener.onOriginalTextClickEMPrepared(getCurrentWord());
    }

    @Override
    public void saveCurrentWordSet(WordSet wordSet) {
        wordSetService.saveCurrent(wordSet);
    }

    protected abstract Word2Tokens getCurrentWord();

    protected abstract void setCurrentSentence(Sentence sentence);
}