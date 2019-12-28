package talkapp.org.talkappmobile.activity.interactor.impl;

import android.content.Context;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
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
import talkapp.org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import static java.util.Arrays.asList;
import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;

public class StudyingPracticeWordSetInteractor extends AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private static final String TAG = StudyingPracticeWordSetInteractor.class.getSimpleName();
    private final SentenceService sentenceService;
    private final Logger logger;
    private final WordSetService experienceService;
    private final WordSetExperienceUtils experienceUtils;
    private final WordRepetitionProgressService exerciseService;
    private final UserExpService userExpService;
    private final WordSetService wordSetService;
    private final WordTranslationService wordTranslationService;
    private int currentWordIndex;
    private Sentence currentSentence;

    public StudyingPracticeWordSetInteractor(WordSetService wordSetService,
                                             SentenceService sentenceService,
                                             RefereeService refereeService,
                                             Logger logger,
                                             WordSetService experienceService,
                                             WordTranslationService wordTranslationService,
                                             WordRepetitionProgressService exerciseService,
                                             UserExpService userExpService,
                                             WordSetExperienceUtils experienceUtils,
                                             Context context,
                                             AudioStuffFactory audioStuffFactory) {
        super(logger, context, refereeService, exerciseService, sentenceService, wordSetService, audioStuffFactory);
        this.sentenceService = sentenceService;
        this.wordSetService = wordSetService;
        this.wordTranslationService = wordTranslationService;
        this.logger = logger;
        this.experienceService = experienceService;
        this.exerciseService = exerciseService;
        this.userExpService = userExpService;
        this.experienceUtils = experienceUtils;
    }

    @Override
    public void initialiseExperience(OnPracticeWordSetListener listener) {
        PracticeWordSetInteractorStrategy state = getStrategy();
        state.initialiseExperience(listener);
        WordSet wordSet = wordSetService.getCurrent();
        listener.onInitialiseExperience(wordSet);
    }

    @Override
    public void initialiseSentence(Word2Tokens word, final OnPracticeWordSetListener listener) {
        WordSet wordSet = wordSetService.findById(word.getSourceWordSetId());
        this.currentWordIndex = wordSet.getWords().indexOf(word);
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
        int wordSetId = wordSetService.getCurrent().getId();
        int experience = experienceService.increaseExperience(wordSetId, 1);
        WordSet wordSet = wordSetService.getCurrent();
        wordSet.setTrainingExperience(experience);
        listener.onUpdateProgress(wordSet.getTrainingExperience(), wordSet.getWords().size() * 2);
        wordSetService.save(wordSet);

        exerciseService.moveCurrentWordToNextState(wordSetId);
        double expScore = userExpService.increaseForRepetition(1, WORD_SET_PRACTICE);
        listener.onUpdateUserExp(expScore);
        return true;
    }

    @Override
    public void refreshSentence(OnPracticeWordSetListener listener) {
        WordSet wordSet = wordSetService.getCurrent();
        Word2Tokens word = wordSet.getWords().get(currentWordIndex);
        initialiseSentence(word, listener);
    }

    @Override
    public void finishWord(OnPracticeWordSetListener listener) {
        getStrategy().finishWord(listener);
    }

    @Override
    public Word2Tokens peekAnyNewWordByWordSetId() {
        WordSet wordSet = wordSetService.getCurrent();
        Word2Tokens currentWord = exerciseService.getCurrentWord(wordSet.getId());
        exerciseService.putOffCurrentWord(wordSet.getId());
        List<Word2Tokens> leftOver = exerciseService.getLeftOverOfWordSetByWordSetId(wordSet.getId());
        Word2Tokens newCurrentWord = peekRandomWordWithoutCurrentWord(leftOver, currentWord);
        exerciseService.markNewCurrentWordByWordSetIdAndWord(wordSet.getId(), newCurrentWord);
        return newCurrentWord;
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
    public Word2Tokens getCurrentWord() {
        WordSet wordSet = wordSetService.getCurrent();
        return wordSet.getWords().get(currentWordIndex);
    }
}