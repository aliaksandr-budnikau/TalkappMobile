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
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import static java.util.Arrays.asList;

public class StudyingPracticeWordSetInteractor extends AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private final SentenceService sentenceService;
    private final WordRepetitionProgressService exerciseService;
    private final WordSetService wordSetService;
    private final WordTranslationService wordTranslationService;
    private final CurrentPracticeStateService currentPracticeStateService;

    public StudyingPracticeWordSetInteractor(WordSetService wordSetService,
                                             SentenceService sentenceService,
                                             RefereeService refereeService,
                                             Logger logger,
                                             WordTranslationService wordTranslationService,
                                             CurrentPracticeStateService currentPracticeStateService,
                                             WordRepetitionProgressService exerciseService,
                                             Context context,
                                             AudioStuffFactory audioStuffFactory) {
        super(logger, context, refereeService, exerciseService, sentenceService, audioStuffFactory, currentPracticeStateService);
        this.sentenceService = sentenceService;
        this.wordSetService = wordSetService;
        this.currentPracticeStateService = currentPracticeStateService;
        this.exerciseService = exerciseService;
        this.wordTranslationService = wordTranslationService;
    }

    @Override
    public void initialiseExperience(OnPracticeWordSetListener listener) {
        PracticeWordSetInteractorStrategy state = getStrategy();
        state.initialiseExperience(listener);
        CurrentPracticeState currentPracticeState = currentPracticeStateService.get();
        listener.onInitialiseExperience(currentPracticeState.getWordSet());
    }

    @Override
    public void initialiseSentence(Word2Tokens word, final OnPracticeWordSetListener listener) {
        WordSet wordSet = wordSetService.findById(word.getSourceWordSetId());
        CurrentPracticeState currentPracticeState = currentPracticeStateService.get();
        currentPracticeState.setCurrentWord(new CurrentPracticeState.WordSource(wordSet.getId(), wordSet.getWords().indexOf(word)));
        currentPracticeStateService.save(currentPracticeState);
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
    public boolean checkAnswer(String answer, final OnPracticeWordSetListener listener) {
        CurrentPracticeState currentPracticeState = currentPracticeStateService.get();
        Sentence sentence = currentPracticeStateService.getCurrentSentence();
        Word2Tokens currentWord = currentPracticeStateService.getCurrentWord();
        if (!super.checkAccuracyOfAnswer(answer, currentWord, sentence, listener)) {
            return false;
        }

        if (isAnswerHasBeenSeen()) {
            listener.onRightAnswer(sentence);
            return false;
        }
        WordSet wordSet = currentPracticeState.getWordSet();
        int maxTrainingProgress = getMaxTrainingProgress(wordSet);
        if (wordSet.getTrainingExperience() > maxTrainingProgress) {
            wordSet.setTrainingExperience(maxTrainingProgress);
        } else {
            wordSet.setTrainingExperience(wordSet.getTrainingExperience() + 1);
        }
        wordSetService.save(wordSet);
        listener.onUpdateProgress(wordSet.getTrainingExperience(), wordSet.getWords().size() * 2);
        exerciseService.moveCurrentWordToNextState(wordSet.getId());
        return true;
    }

    @Override
    public Word2Tokens peekAnyNewWordByWordSetId() {
        CurrentPracticeState currentPracticeState = currentPracticeStateService.get();
        Word2Tokens currentWord = currentPracticeStateService.getCurrentWord();
        exerciseService.putOffCurrentWord(currentPracticeState.getWordSet().getId());
        List<Word2Tokens> leftOver = exerciseService.getLeftOverOfWordSetByWordSetId(currentPracticeState.getWordSet().getId());
        Word2Tokens newCurrentWord = peekRandomWordWithoutCurrentWord(leftOver, currentWord);
        exerciseService.markNewCurrentWordByWordSetIdAndWord(currentPracticeState.getWordSet().getId(), newCurrentWord);
        return newCurrentWord;
    }

    private int getMaxTrainingProgress(WordSet wordSet) {
        return wordSet.getWords().size() * 2;
    }
}