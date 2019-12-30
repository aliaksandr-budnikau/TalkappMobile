package talkapp.org.talkappmobile.activity.interactor.impl;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.CurrentPracticeState;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;

import static java.util.Collections.singletonList;

public class RepetitionPracticeWordSetInteractor extends AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private static final String TAG = RepetitionPracticeWordSetInteractor.class.getSimpleName();
    private final SentenceService sentenceService;
    private final Logger logger;
    private final WordRepetitionProgressService exerciseService;
    private final WordSetExperienceUtils experienceUtils;
    private final WordSetService wordSetService;
    private final WordTranslationService wordTranslationService;
    private int maxTrainingProgress;
    private List<CurrentPracticeState.WordSource> finishedWords = new LinkedList<>();
    private List<CurrentPracticeState.WordSource> wordsSources = new LinkedList<>();

    public RepetitionPracticeWordSetInteractor(
            SentenceService sentenceService,
            RefereeService refereeService,
            Logger logger,
            WordRepetitionProgressService exerciseService,
            WordSetExperienceUtils experienceUtils,
            WordSetService wordSetService,
            WordTranslationService wordTranslationService,
            Context context,
            AudioStuffFactory audioStuffFactory) {
        super(logger, context, refereeService, exerciseService, sentenceService, wordSetService, audioStuffFactory, wordTranslationService);
        this.sentenceService = sentenceService;
        this.logger = logger;
        this.exerciseService = exerciseService;
        this.experienceUtils = experienceUtils;
        this.wordSetService = wordSetService;
        this.wordTranslationService = wordTranslationService;
    }

    @Override
    public void initialiseExperience(OnPracticeWordSetListener listener) {
        CurrentPracticeState currentPracticeState = wordSetService.getCurrentPracticeState();
        for (Word2Tokens word : currentPracticeState.getWordSet().getWords()) {
            WordSet set = wordSetService.findById(word.getSourceWordSetId());
            wordsSources.add(new CurrentPracticeState.WordSource(word.getSourceWordSetId(), set.getWords().indexOf(word)));
        }
        maxTrainingProgress = experienceUtils.getMaxTrainingProgress(currentPracticeState.getWordSet()) / 2;
        logger.i(TAG, "enable repetition mode");
        listener.onEnableRepetitionMode();
        currentPracticeState.getWordSet().setTrainingExperience(0);
        wordSetService.saveCurrentPracticeState(currentPracticeState);
        listener.onInitialiseExperience(currentPracticeState.getWordSet());
    }

    @Override
    public Word2Tokens peekAnyNewWordByWordSetId() {
        LinkedList<Word2Tokens> words = new LinkedList<>();
        for (CurrentPracticeState.WordSource word : wordsSources) {
            WordSet set = wordSetService.findById(word.getWordSetId());
            words.add(set.getWords().get(word.getWordIndex()));
        }
        return peekRandomWordWithoutCurrentWord(words, getCurrentWord());
    }

    @Override
    public void initialiseSentence(Word2Tokens word, OnPracticeWordSetListener listener) {
        CurrentPracticeState currentPracticeState = wordSetService.getCurrentPracticeState();
        WordSet wordSet = wordSetService.findById(word.getSourceWordSetId());
        currentPracticeState.setCurrentWord(new CurrentPracticeState.WordSource(word.getSourceWordSetId(), wordSet.getWords().indexOf(word)));
        wordSetService.saveCurrentPracticeState(currentPracticeState);
        List<Sentence> sentences = sentenceService.fetchSentencesNotFromServerByWordAndWordSetId(word);
        if (sentences.isEmpty()) {
            WordTranslation wordTranslation = wordTranslationService.findByWordAndLanguage(word.getWord(), "russian");
            if (wordTranslation == null) {
                return;
            }
            sentences = singletonList(sentenceService.convertToSentence(wordTranslation));
        }
        setCurrentSentence(sentences.get(0));
        listener.onSentencesFound(getCurrentSentence(), word);
    }

    @Override
    public boolean checkAnswer(String answer, Sentence sentence, OnPracticeWordSetListener listener) {
        if (!super.checkAccuracyOfAnswer(answer, getCurrentWord(), sentence, listener)) {
            return false;
        }

        if (isAnswerHasBeenSeen()) {
            int counter = exerciseService.markAsForgottenAgain(getCurrentWord());
            listener.onForgottenAgain(counter);
            listener.onRightAnswer(sentence);
            return false;
        }

        CurrentPracticeState currentPracticeState = wordSetService.getCurrentPracticeState();
        currentPracticeState.getWordSet().setTrainingExperience(currentPracticeState.getWordSet().getTrainingExperience() + 1);
        wordSetService.saveCurrentPracticeState(currentPracticeState);
        finishedWords.add(currentPracticeState.getCurrentWord());
        listener.onUpdateProgress(currentPracticeState.getWordSet().getTrainingExperience(), maxTrainingProgress);
        int repetitionCounter = exerciseService.markAsRepeated(getCurrentWord());
        exerciseService.shiftSentences(getCurrentWord());
        return true;
    }

    @Override
    public void refreshSentence(OnPracticeWordSetListener listener) {
        CurrentPracticeState currentPracticeState = wordSetService.getCurrentPracticeState();
        CurrentPracticeState.WordSource currentWord = currentPracticeState.getCurrentWord();
        WordSet wordSet = wordSetService.findById(currentWord.getWordSetId());
        Word2Tokens word = wordSet.getWords().get(currentWord.getWordIndex());
        initialiseSentence(word, listener);
    }

    @Override
    protected Word2Tokens peekRandomWordWithoutCurrentWord(List<Word2Tokens> words, Word2Tokens currentWord) {
        LinkedList<Word2Tokens> leftOver = new LinkedList<>(words);
        for (CurrentPracticeState.WordSource finishedWord : finishedWords) {
            leftOver.remove(getWord2TokensSource(finishedWord));
        }
        return super.peekRandomWordWithoutCurrentWord(leftOver, currentWord);
    }

    private Word2Tokens getWord2TokensSource(CurrentPracticeState.WordSource source) {
        int wordSetId = source.getWordSetId();
        int wordIndex = source.getWordIndex();
        return wordSetService.findById(wordSetId).getWords().get(wordIndex);
    }

    @Override
    protected Word2Tokens getCurrentWord() {
        CurrentPracticeState.WordSource currentWord = wordSetService.getCurrentPracticeState().getCurrentWord();
        if (currentWord == null) {
            return null;
        }
        return getWord2TokensSource(currentWord);
    }
}