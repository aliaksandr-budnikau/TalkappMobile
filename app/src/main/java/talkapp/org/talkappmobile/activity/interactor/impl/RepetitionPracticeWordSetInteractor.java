package talkapp.org.talkappmobile.activity.interactor.impl;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.PracticeState;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.UserExpService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;

import static java.util.Collections.singletonList;
import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;

public class RepetitionPracticeWordSetInteractor extends AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private static final String TAG = RepetitionPracticeWordSetInteractor.class.getSimpleName();
    private final SentenceService sentenceService;
    private final Logger logger;
    private final WordRepetitionProgressService exerciseService;
    private final UserExpService userExpService;
    private final WordSetExperienceUtils experienceUtils;
    private final WordSetService wordSetService;
    private final WordTranslationService wordTranslationService;
    private WordSource currentWord;
    private Sentence currentSentence;
    private int maxTrainingProgress;
    private List<WordSource> finishedWords = new LinkedList<>();
    private List<WordSource> wordsSources = new LinkedList<>();

    public RepetitionPracticeWordSetInteractor(
            SentenceService sentenceService,
            RefereeService refereeService,
            Logger logger,
            WordRepetitionProgressService exerciseService,
            UserExpService userExpService,
            WordSetExperienceUtils experienceUtils,
            WordSetService wordSetService,
            WordTranslationService wordTranslationService,
            Context context,
            AudioStuffFactory audioStuffFactory) {
        super(logger, context, refereeService, exerciseService, sentenceService, wordSetService, audioStuffFactory);
        this.sentenceService = sentenceService;
        this.logger = logger;
        this.exerciseService = exerciseService;
        this.userExpService = userExpService;
        this.experienceUtils = experienceUtils;
        this.wordSetService = wordSetService;
        this.wordTranslationService = wordTranslationService;
    }

    @Override
    public Sentence getCurrentSentence() {
        return currentSentence;
    }

    @Override
    protected void setCurrentSentence(Sentence sentence) {
        this.currentSentence = sentence;
    }

    @Override
    public void initialiseExperience(OnPracticeWordSetListener listener) {
        PracticeState practiceState = wordSetService.getCurrent();
        for (Word2Tokens word : practiceState.getWordSet().getWords()) {
            WordSet set = wordSetService.findById(word.getSourceWordSetId());
            wordsSources.add(new WordSource(word.getSourceWordSetId(), set.getWords().indexOf(word)));
        }
        maxTrainingProgress = experienceUtils.getMaxTrainingProgress(practiceState.getWordSet()) / 2;
        logger.i(TAG, "enable repetition mode");
        listener.onEnableRepetitionMode();
        practiceState.getWordSet().setTrainingExperience(0);
        wordSetService.saveCurrent(practiceState);
        listener.onInitialiseExperience(practiceState.getWordSet());
    }

    @Override
    public Word2Tokens peekAnyNewWordByWordSetId() {
        LinkedList<Word2Tokens> words = new LinkedList<>();
        for (WordSource word : wordsSources) {
            WordSet set = wordSetService.findById(word.getWordSetId());
            words.add(set.getWords().get(word.wordIndex));
        }
        return peekRandomWordWithoutCurrentWord(words, getCurrentWord());
    }

    @Override
    public void initialiseSentence(Word2Tokens word, OnPracticeWordSetListener listener) {
        WordSet wordSet = wordSetService.findById(word.getSourceWordSetId());
        this.currentWord = new WordSource(word.getSourceWordSetId(), wordSet.getWords().indexOf(word));
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

        PracticeState practiceState = wordSetService.getCurrent();
        practiceState.getWordSet().setTrainingExperience(practiceState.getWordSet().getTrainingExperience() + 1);
        wordSetService.saveCurrent(practiceState);
        finishedWords.add(currentWord);
        listener.onUpdateProgress(practiceState.getWordSet().getTrainingExperience(), maxTrainingProgress);
        int repetitionCounter = exerciseService.markAsRepeated(getCurrentWord());
        exerciseService.shiftSentences(getCurrentWord());
        double expScore = userExpService.increaseForRepetition(repetitionCounter, WORD_SET_PRACTICE);
        listener.onUpdateUserExp(expScore);
        return true;
    }

    @Override
    public void refreshSentence(OnPracticeWordSetListener listener) {
        WordSet wordSet = wordSetService.findById(currentWord.wordSetId);
        Word2Tokens word = wordSet.getWords().get(currentWord.getWordIndex());
        initialiseSentence(word, listener);
    }

    @Override
    protected Word2Tokens peekRandomWordWithoutCurrentWord(List<Word2Tokens> words, Word2Tokens currentWord) {
        LinkedList<Word2Tokens> leftOver = new LinkedList<>(words);
        for (WordSource finishedWord : finishedWords) {
            leftOver.remove(getWord2TokensSource(finishedWord));
        }
        return super.peekRandomWordWithoutCurrentWord(leftOver, currentWord);
    }

    private Word2Tokens getWord2TokensSource(WordSource source) {
        int wordSetId = source.getWordSetId();
        int wordIndex = source.getWordIndex();
        return wordSetService.findById(wordSetId).getWords().get(wordIndex);
    }

    @Override
    public Word2Tokens getCurrentWord() {
        if (currentWord == null) {
            return null;
        }
        return getWord2TokensSource(currentWord);
    }

    private static class WordSource {
        private final int wordSetId;
        private final int wordIndex;

        public WordSource(int wordSetId, int wordIndex) {
            this.wordSetId = wordSetId;
            this.wordIndex = wordIndex;
        }

        public int getWordSetId() {
            return wordSetId;
        }

        public int getWordIndex() {
            return wordIndex;
        }
    }
}