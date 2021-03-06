package talkapp.org.talkappmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.event.wordset.AnswerHasBeenRevealedEM;
import talkapp.org.talkappmobile.activity.event.wordset.AnswerPronunciationStartedEM;
import talkapp.org.talkappmobile.activity.event.wordset.AnswerPronunciationStoppedEM;
import talkapp.org.talkappmobile.activity.event.wordset.ChangeSentenceOptionPickedEM;
import talkapp.org.talkappmobile.activity.event.wordset.ExerciseGotAnsweredEM;
import talkapp.org.talkappmobile.activity.event.wordset.NewSentenceEM;
import talkapp.org.talkappmobile.activity.event.wordset.OriginalTextClickEM;
import talkapp.org.talkappmobile.activity.event.wordset.PracticeHalfFinishedEM;
import talkapp.org.talkappmobile.activity.event.wordset.RightAnswerTouchedEM;
import talkapp.org.talkappmobile.activity.event.wordset.RightAnswerUntouchedEM;
import talkapp.org.talkappmobile.activity.event.wordset.ScoreSentenceOptionPickedEM;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenter;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewStrategy;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.EqualityScorer;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.component.WordsCombinator;
import talkapp.org.talkappmobile.component.backend.BackendServerFactory;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.database.ServiceFactory;
import talkapp.org.talkappmobile.component.database.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.component.impl.AudioStuffFactoryBean;
import talkapp.org.talkappmobile.component.impl.BackendSentenceProviderStrategy;
import talkapp.org.talkappmobile.component.impl.EqualityScorerBean;
import talkapp.org.talkappmobile.component.impl.GrammarCheckServiceImpl;
import talkapp.org.talkappmobile.component.impl.LoggerBean;
import talkapp.org.talkappmobile.component.impl.RandomSentenceSelectorBean;
import talkapp.org.talkappmobile.component.impl.RandomWordsCombinatorBean;
import talkapp.org.talkappmobile.component.impl.RefereeServiceImpl;
import talkapp.org.talkappmobile.component.impl.SentenceProviderImpl;
import talkapp.org.talkappmobile.component.impl.SentenceProviderRepetitionStrategy;
import talkapp.org.talkappmobile.component.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.component.impl.WordSetExperienceUtilsImpl;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

import static android.app.Activity.RESULT_OK;
import static org.androidannotations.annotations.IgnoreWhen.State.VIEW_DESTROYED;

@EFragment(value = R.layout.word_set_practice_activity_fragment)
public class PracticeWordSetFragment extends Fragment implements PracticeWordSetView {
    public static final String WORD_SET_MAPPING = "wordSet";
    public static final String REPETITION_MODE_MAPPING = "repetitionMode";
    private static final String CHEAT_SEND_WRITE_ANSWER = "LLCLPCLL";
    @Bean(BackendServerFactoryBean.class)
    BackendServerFactory backendServerFactory;
    @Bean(ServiceFactoryBean.class)
    ServiceFactory serviceFactory;
    @Bean(TextUtilsImpl.class)
    TextUtils textUtils;
    @Bean(WordSetExperienceUtilsImpl.class)
    WordSetExperienceUtils experienceUtils;
    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;
    @Bean(LoggerBean.class)
    Logger logger;
    @Bean(EqualityScorerBean.class)
    EqualityScorer equalityScorer;
    @Bean(RandomSentenceSelectorBean.class)
    SentenceSelector sentenceSelector;
    @Bean(AudioStuffFactoryBean.class)
    AudioStuffFactory audioStuffFactory;
    @Bean(RandomWordsCombinatorBean.class)
    WordsCombinator wordsCombinator;

    @ViewById(R.id.originalText)
    TextView originalText;
    @ViewById(R.id.rightAnswer)
    TextView rightAnswer;
    @ViewById(R.id.answerText)
    TextView answerText;
    @ViewById(R.id.wordSetProgress)
    ProgressBar wordSetProgress;
    @ViewById(R.id.nextButton)
    Button nextButton;
    @ViewById(R.id.checkButton)
    Button checkButton;
    @ViewById(R.id.speakButton)
    Button speakButton;
    @ViewById(R.id.playButton)
    Button playButton;
    @ViewById(R.id.pronounceRightAnswerButton)
    Button pronounceRightAnswerButton;
    @ViewById(R.id.please_wait_progress_bar)
    View pleaseWaitProgressBar;
    @ViewById(R.id.word_set_practise_form)
    View wordSetPractiseForm;
    @ViewById(R.id.spellingGrammarErrorsListView)
    LinearLayout spellingGrammarErrorsListView;

    @FragmentArg(WORD_SET_MAPPING)
    WordSet wordSet;
    @FragmentArg(REPETITION_MODE_MAPPING)
    boolean repetitionMode;

    @EventBusGreenRobot
    EventBus eventBus;

    private WaitingForProgressBarManager waitingForProgressBarManager;
    private PracticeWordSetPresenter presenter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PracticeWordSetFragment newInstance(WordSet wordSet, boolean repetitionMode) {
        PracticeWordSetFragment fragment = new PracticeWordSetFragment_();
        Bundle args = new Bundle();
        args.putSerializable(WORD_SET_MAPPING, wordSet);
        args.putBoolean(REPETITION_MODE_MAPPING, repetitionMode);
        fragment.setArguments(args);
        return fragment;
    }

    @AfterViews
    public void init() {

        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(pleaseWaitProgressBar, wordSetPractiseForm);

        initPresenter();
    }

    @Background
    public void initPresenter() {
        BackendSentenceProviderStrategy backendStrategy = new BackendSentenceProviderStrategy(backendServerFactory.get());
        SentenceProviderRepetitionStrategy repetitionStrategy = new SentenceProviderRepetitionStrategy(backendServerFactory.get(), serviceFactory.getPracticeWordSetExerciseRepository());
        SentenceProvider sentenceProvider = new SentenceProviderImpl(backendStrategy, repetitionStrategy);
        GrammarCheckServiceImpl grammarCheckService = new GrammarCheckServiceImpl(backendServerFactory.get());
        RefereeService refereeService = new RefereeServiceImpl(grammarCheckService, equalityScorer);
        PracticeWordSetViewStrategy viewStrategy = new PracticeWordSetViewStrategy(this, textUtils, experienceUtils);

        PracticeWordSetInteractor interactor = new StudyingPracticeWordSetInteractor(wordsCombinator, sentenceProvider, sentenceSelector, refereeService, logger, serviceFactory.getWordSetExperienceRepository(), serviceFactory.getPracticeWordSetExerciseRepository(), serviceFactory.getUserExpService(), getContext(), audioStuffFactory);
        if (repetitionMode) {
            interactor = new RepetitionPracticeWordSetInteractor(sentenceProvider, sentenceSelector, refereeService, logger, serviceFactory.getPracticeWordSetExerciseRepository(), serviceFactory.getUserExpService(), getContext(), audioStuffFactory);
        }
        presenter = new PracticeWordSetPresenter(wordSet, interactor, viewStrategy);
        presenter.initialise();
        presenter.nextButtonClick();
    }

    @Click(R.id.originalText)
    public void onOriginalTextClick() {
        eventBus.post(new OriginalTextClickEM());
    }

    @Touch(R.id.rightAnswer)
    public boolean onRightAnswerOnTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                eventBus.post(new RightAnswerTouchedEM());
                return true; // if you want to handle the touch event
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                eventBus.post(new RightAnswerUntouchedEM());
                return true; // if you want to handle the touch event
        }
        return false;
    }

    @Click(R.id.nextButton)
    @Background
    public void onNextButtonClick() {
        presenter.nextButtonClick();
    }

    @Click(R.id.playButton)
    @Background
    public void onPlayVoiceButtonClick() {
        presenter.playVoiceButtonClick();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AnswerPronunciationStartedEM event) {
        presenter.disableButtonsDuringPronunciation();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AnswerPronunciationStoppedEM event) {
        presenter.enableButtonsAfterPronunciation();
    }

    @Click(R.id.checkButton)
    @Background
    public void onCheckAnswerButtonClick() {
        String answer = answerText.getText().toString();
        if (CHEAT_SEND_WRITE_ANSWER.equals(answer)) {
            presenter.checkRightAnswerCommandRecognized();
        } else {
            presenter.checkAnswerButtonClick(answer);
        }
    }

    @Click(R.id.speakButton)
    public void onRecogniseVoiceButtonClick() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, PracticeWordSetFragment.class.getPackage().getName());
        intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        intent.putExtra("android.speech.extra.GET_AUDIO", true);
        startActivityForResult(intent, 3000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        Bundle bundle = data.getExtras();

        if (resultCode != RESULT_OK || bundle == null) {
            return;
        }

        List<String> suggestedWords = bundle.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
        if (suggestedWords == null || suggestedWords.isEmpty()) {
            return;
        }

        presenter.gotRecognitionResult(suggestedWords);
        presenter.voiceRecorded(data.getData());
    }

    @Override
    @UiThread
    public void showNextButton() {
        nextButton.setVisibility(View.VISIBLE);
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void hideNextButton() {
        nextButton.setVisibility(View.GONE);
    }

    @Override
    @UiThread
    public void showPleaseWaitProgressBar() {
        waitingForProgressBarManager.showProgressBar();
    }

    @Override
    @UiThread
    public void hidePleaseWaitProgressBar() {
        waitingForProgressBarManager.hideProgressBar();
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void showCheckButton() {
        checkButton.setVisibility(View.VISIBLE);
    }

    @Override
    @UiThread
    public void hideCheckButton() {
        checkButton.setVisibility(View.GONE);
    }

    @Override
    @UiThread
    public void setRightAnswer(final String text) {
        rightAnswer.setText(text);
    }

    @Override
    public void setProgress(int progress) {
        wordSetProgress.setProgress(progress);
    }

    @Override
    @UiThread
    public void showMessageAnswerEmpty() {
        Toast.makeText(getContext(), "Answer can't be empty.", Toast.LENGTH_SHORT).show();
    }

    @Override
    @UiThread
    public void showMessageAccuracyTooLow() {
        Toast.makeText(getContext(), "Accuracy too low", Toast.LENGTH_LONG).show();
    }

    @Override
    @UiThread
    public void showCongratulationMessage() {
        Toast.makeText(getContext(), "Congratulations! You finished!", Toast.LENGTH_LONG).show();
    }

    @Override
    @UiThread
    public void closeActivity() {
        getActivity().finish();
    }

    @Override
    @UiThread
    public void openAnotherActivity() {
        Intent intent = new Intent(getContext(), MainActivity_.class);
        startActivity(intent);
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void setEnableVoiceRecButton(final boolean value) {
        speakButton.setEnabled(value);
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void setEnablePronounceRightAnswerButton(final boolean value) {
        pronounceRightAnswerButton.setEnabled(value);
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void setEnableCheckButton(final boolean value) {
        checkButton.setEnabled(value);
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void setEnableNextButton(final boolean value) {
        nextButton.setEnabled(value);
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void setAnswerText(final String text) {
        answerText.setText(text);
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void setEnableRightAnswerTextView(final boolean value) {
        rightAnswer.setEnabled(value);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(ScoreSentenceOptionPickedEM event) {
        presenter.scoreSentence(event.getScore(), event.getSentence());
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void showScoringSuccessfulMessage() {
        Toast.makeText(getContext(), "Thank you for your help", Toast.LENGTH_LONG).show();
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void showScoringUnsuccessfulMessage() {
        Toast.makeText(getContext(), "Try again later", Toast.LENGTH_LONG).show();
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void showSentenceChangedSuccessfullyMessage() {
        Toast.makeText(getContext(), "The sentence was changed", Toast.LENGTH_LONG).show();
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void onSentencesFound(Sentence sentence, Word2Tokens word) {
        eventBus.post(new NewSentenceEM(sentence, word));
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void onEnableRepetitionMode() {
        eventBus.post(new PracticeHalfFinishedEM());
    }

    @Override
    public void onExerciseGotAnswered() {
        eventBus.post(new ExerciseGotAnsweredEM());
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void onUpdateUserExp(double expScore) {
        Toast.makeText(getContext(), "+" + expScore + " EXP", Toast.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewSentenceEM event) {
        presenter.refreshSentence();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(ChangeSentenceOptionPickedEM event) {
        presenter.changeSentence();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AnswerHasBeenRevealedEM event) {
        presenter.markAnswerHasBeenSeen();
    }
}