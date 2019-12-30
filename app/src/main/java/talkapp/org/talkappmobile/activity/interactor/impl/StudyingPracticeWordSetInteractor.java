package talkapp.org.talkappmobile.activity.interactor.impl;

import android.content.Context;

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
import talkapp.org.talkappmobile.service.UserExpService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import static java.util.Arrays.asList;
import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;

public class StudyingPracticeWordSetInteractor extends AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private final SentenceService sentenceService;
    private final WordRepetitionProgressService exerciseService;
    private final UserExpService userExpService;
    private final WordSetService wordSetService;
    private final WordTranslationService wordTranslationService;

    public StudyingPracticeWordSetInteractor(WordSetService wordSetService,
                                             SentenceService sentenceService,
                                             RefereeService refereeService,
                                             Logger logger,
                                             WordTranslationService wordTranslationService,
                                             WordRepetitionProgressService exerciseService,
                                             UserExpService userExpService,
                                             Context context,
                                             AudioStuffFactory audioStuffFactory) {
        super(logger, context, refereeService, exerciseService, sentenceService, wordSetService, audioStuffFactory, wordTranslationService);
        this.sentenceService = sentenceService;
        this.wordSetService = wordSetService;
        this.wordTranslationService = wordTranslationService;
        this.exerciseService = exerciseService;
        this.userExpService = userExpService;
    }

    @Override
    public void initialiseExperience(OnPracticeWordSetListener listener) {
        PracticeWordSetInteractorStrategy state = getStrategy();
        state.initialiseExperience(listener);
        CurrentPracticeState currentPracticeState = wordSetService.getCurrentPracticeState();
        listener.onInitialiseExperience(currentPracticeState.getWordSet());
    }

    @Override
    public void initialiseSentence(Word2Tokens word, final OnPracticeWordSetListener listener) {
        WordSet wordSet = wordSetService.findById(word.getSourceWordSetId());
        CurrentPracticeState currentPracticeState = wordSetService.getCurrentPracticeState();
        currentPracticeState.setCurrentWord(new CurrentPracticeState.WordSource(wordSet.getId(), wordSet.getWords().indexOf(word)));
        wordSetService.saveCurrentPracticeState(currentPracticeState);
        List<Sentence> sentences = exerciseService.findByWordAndWordSetId(word);
        if (sentences.isEmpty()) {
            try {
                sentences = sentenceService.fetchSentencesFromServerByWordAndWordSetId(word);
            } catch (LocalCacheIsEmptyException e) {
                WordTranslation wordTranslation = wordTranslationService.findByWordAndLanguage(word.getWord(), "russian");
                if (wordTranslation == null) {
                    return;
                }
                sentences = asList(sentenceService.convertToSentence(wordTranslation));
            }
            sentenceService.orderByScore(sentences);
            List<Sentence> selectSentences = sentenceService.selectSentences(sentences);
            replaceSentence(selectSentences, word, listener);
        } else {
            setCurrentSentence(sentences.get(0));
            listener.onSentencesFound(getCurrentSentence(), word);
        }
    }

    @Override
    public boolean checkAnswer(String answer, final Sentence sentence, final OnPracticeWordSetListener listener) {
        if (!super.checkAccuracyOfAnswer(answer, getCurrentWord(), sentence, listener)) {
            return false;
        }

        if (isAnswerHasBeenSeen()) {
            listener.onRightAnswer(sentence);
            return false;
        }
        WordSet wordSet = wordSetService.getCurrentPracticeState().getWordSet();
        int maxTrainingProgress = getMaxTrainingProgress(wordSet);
        if (wordSet.getTrainingExperience() > maxTrainingProgress) {
            wordSet.setTrainingExperience(maxTrainingProgress);
        } else {
            wordSet.setTrainingExperience(wordSet.getTrainingExperience() + 1);
        }
        wordSetService.save(wordSet);
        listener.onUpdateProgress(wordSet.getTrainingExperience(), wordSet.getWords().size() * 2);
        exerciseService.moveCurrentWordToNextState(wordSet.getId());
        double expScore = userExpService.increaseForRepetition(1, WORD_SET_PRACTICE);
        listener.onUpdateUserExp(expScore);
        return true;
    }

    @Override
    public void refreshSentence(OnPracticeWordSetListener listener) {
        CurrentPracticeState currentPracticeState = wordSetService.getCurrentPracticeState();
        int wordIndex = currentPracticeState.getCurrentWord().getWordIndex();
        Word2Tokens word = currentPracticeState.getWordSet().getWords().get(wordIndex);
        initialiseSentence(word, listener);
    }

    @Override
    public Word2Tokens peekAnyNewWordByWordSetId() {
        CurrentPracticeState currentPracticeState = wordSetService.getCurrentPracticeState();
        Word2Tokens currentWord = exerciseService.getCurrentWord(currentPracticeState.getWordSet().getId());
        exerciseService.putOffCurrentWord(currentPracticeState.getWordSet().getId());
        List<Word2Tokens> leftOver = exerciseService.getLeftOverOfWordSetByWordSetId(currentPracticeState.getWordSet().getId());
        Word2Tokens newCurrentWord = peekRandomWordWithoutCurrentWord(leftOver, currentWord);
        exerciseService.markNewCurrentWordByWordSetIdAndWord(currentPracticeState.getWordSet().getId(), newCurrentWord);
        return newCurrentWord;
    }

    @Override
    protected Word2Tokens getCurrentWord() {
        CurrentPracticeState currentPracticeState = wordSetService.getCurrentPracticeState();
        CurrentPracticeState.WordSource currentWord = currentPracticeState.getCurrentWord();
        int wordIndex = 0;
        if (currentWord != null) {
            wordIndex = currentWord.getWordIndex();
        }
        return currentPracticeState.getWordSet().getWords().get(wordIndex);
    }

    private int getMaxTrainingProgress(WordSet wordSet) {
        return wordSet.getWords().size() * 2;
    }
}