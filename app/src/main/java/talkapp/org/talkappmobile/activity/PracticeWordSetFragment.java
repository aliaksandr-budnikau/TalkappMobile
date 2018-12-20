package talkapp.org.talkappmobile.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.RightAnswerTextView;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenter;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetFirstCycleViewStrategy;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetSecondCycleViewStrategy;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.ViewStrategyFactory;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

import static android.app.Activity.RESULT_OK;
import static org.androidannotations.annotations.IgnoreWhen.State.VIEW_DESTROYED;

@EFragment(value = R.layout.word_set_practice_activity_fragment)
public class PracticeWordSetFragment extends Fragment implements PracticeWordSetView {
    public static final String WORD_SET_MAPPING = "wordSet";
    public static final String REPETITION_MODE_MAPPING = "repetitionMode";
    private static final String CHEAT_SEND_WRITE_ANSWER = "LLCLPCLL";
    private final StringBuilder SIGNAL_SEQUENCE = new StringBuilder("12345678");
    @Inject
    StudyingPracticeWordSetInteractor studyingPracticeWordSetInteractor;
    @Inject
    RepetitionPracticeWordSetInteractor repetitionPracticeWordSetInteractor;
    @Inject
    ViewStrategyFactory viewStrategyFactory;
    @Inject
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;

    @ViewById(R.id.originalText)
    TextView originalText;
    @ViewById(R.id.rightAnswer)
    RightAnswerTextView rightAnswer;
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

    @StringRes(R.string.score_sentence_dialog_title)
    String scoreSentenceDialogTitle;
    @StringRes(R.string.another_sentence_option)
    String anotherSentenceOption;
    @StringRes(R.string.poor_sentence_option)
    String poorSentenceOption;
    @StringRes(R.string.corrupted_sentence_option)
    String corruptedSentenceOption;
    @StringRes(R.string.insult_sentence_option)
    String insultSentenceOption;

    @FragmentArg(WORD_SET_MAPPING)
    WordSet wordSet;
    @FragmentArg(REPETITION_MODE_MAPPING)
    boolean repetitionMode;

    private WaitingForProgressBarManager waitingForProgressBarManager;
    private PracticeWordSetPresenter presenter;
    private HashMap<SentenceContentScore, String> enumToTexts = new HashMap<>();

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
        DIContextUtils.get().inject(this);

        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(pleaseWaitProgressBar, wordSetPractiseForm);

        enumToTexts.put(SentenceContentScore.POOR, poorSentenceOption);
        enumToTexts.put(SentenceContentScore.CORRUPTED, corruptedSentenceOption);
        enumToTexts.put(SentenceContentScore.INSULT, insultSentenceOption);

        initPresenter();
    }

    @Background
    public void initPresenter() {
        PracticeWordSetFirstCycleViewStrategy firstStrategy = viewStrategyFactory.createPracticeWordSetFirstCycleViewStrategy(this);
        PracticeWordSetSecondCycleViewStrategy secondStrategy = viewStrategyFactory.createPracticeWordSetSecondCycleViewStrategy(this);
        PracticeWordSetInteractor interactor = studyingPracticeWordSetInteractor;
        if (repetitionMode) {
            interactor = repetitionPracticeWordSetInteractor;
        }
        presenter = new PracticeWordSetPresenter(wordSet, interactor, firstStrategy, secondStrategy);
        presenter.initialise();
        presenter.nextButtonClick();
    }

    @Click(R.id.originalText)
    public void onOriginalTextClick() {
        presenter.onOriginalTextClick();
    }

    @Touch(R.id.rightAnswer)
    public boolean onRightAnswerOnTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                presenter.rightAnswerTouched();
                return true; // if you want to handle the touch event
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                presenter.rightAnswerUntouched();
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
        sendCheatSignal("L");
    }

    @Click(R.id.pronounceRightAnswerButton)
    @Background
    public void onPronounceRightAnswerButtonClick() {
        presenter.pronounceRightAnswerButtonClick();
        sendCheatSignal("P");
    }

    @Click(R.id.checkButton)
    @Background
    public void onCheckAnswerButtonClick() {
        presenter.checkAnswerButtonClick(answerText.getText().toString());
        sendCheatSignal("C");
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
    @IgnoreWhen(VIEW_DESTROYED)
    public void setOriginalText(final String text) {
        originalText.setText(text);
    }

    @Override
    @UiThread
    public void showMessageAnswerEmpty() {
        Toast.makeText(getContext(), "Answer can't be empty.", Toast.LENGTH_SHORT).show();
    }

    @Override
    @UiThread
    public void showMessageSpellingOrGrammarError() {
        Toast.makeText(getContext(), "Spelling or grammar errors", Toast.LENGTH_LONG).show();
    }

    @Override
    @UiThread
    public void showMessageAccuracyTooLow() {
        Toast.makeText(getContext(), "Accuracy too low", Toast.LENGTH_LONG).show();
    }

    @Override
    @UiThread
    public void showCongratulationMessage() {
        Toast.makeText(getContext(), "Congratulations! You won!", Toast.LENGTH_LONG).show();
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
    public void setEnableVoiceRecButton(final boolean value) {
        speakButton.setEnabled(value);
    }

    @Override
    @UiThread
    public void setEnablePronounceRightAnswerButton(final boolean value) {
        pronounceRightAnswerButton.setEnabled(value);
    }

    @Override
    @UiThread
    public void setEnableCheckButton(final boolean value) {
        checkButton.setEnabled(value);
    }

    @Override
    @UiThread
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
    public void showSpellingOrGrammarErrorPanel(final String errorMessage) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View vi = inflater.inflate(R.layout.row_spelling_grammar_errors_list_item, null);
        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
        TextView textView = vi.findViewById(R.id.errorRow);
        textView.setText(errorMessage);
        spellingGrammarErrorsListView.addView(vi);
    }

    @Override
    @UiThread
    public void hideSpellingOrGrammarErrorPanel() {
        spellingGrammarErrorsListView.removeAllViews();
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void setEnableRightAnswerTextView(final boolean value) {
        rightAnswer.setEnabled(value);
    }

    @Override
    @IgnoreWhen(VIEW_DESTROYED)
    public void setRightAnswerModel(Sentence sentence, Word2Tokens word) {
        rightAnswer.setModel(sentence, word);
    }

    @Override
    @IgnoreWhen(VIEW_DESTROYED)
    public void maskRightAnswerEntirely() {
        rightAnswer.maskEntirely();
    }

    @Override
    @IgnoreWhen(VIEW_DESTROYED)
    public void maskRightAnswerOnlyWord() {
        rightAnswer.maskOnlyWord();
    }

    @Override
    public void unmaskRightAnswer() {
        rightAnswer.unmask();
    }

    @Override
    public void lockRightAnswer() {
        rightAnswer.lock();
    }

    @Override
    @IgnoreWhen(VIEW_DESTROYED)
    public void unlockRightAnswer() {
        rightAnswer.unlock();
    }

    @Override
    @IgnoreWhen(VIEW_DESTROYED)
    public void openDialogForSentenceScoring(final Sentence sentence) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(scoreSentenceDialogTitle)
                .setItems(getOptions(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        scoreSentence(which, sentence);
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    @NonNull
    private String[] getOptions() {
        List<String> options = new LinkedList<>();
        options.add(anotherSentenceOption);
        for (SentenceContentScore value : SentenceContentScore.values()) {
            options.add(enumToTexts.get(value));
        }
        return options.toArray(new String[SentenceContentScore.values().length]);
    }

    @Background
    public void scoreSentence(int which, Sentence sentence) {
        presenter.scoreSentence(sentence, which);
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
    public void showSentenceChangeUnsupportedMessage() {
        Toast.makeText(getContext(), "Unsupported in repetition mode", Toast.LENGTH_LONG).show();
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void showSentenceChangedSuccessfullyMessage() {
        Toast.makeText(getContext(), "The sentence was changed", Toast.LENGTH_LONG).show();
    }

    private void sendCheatSignal(final String signal) {
        SIGNAL_SEQUENCE.deleteCharAt(0);
        SIGNAL_SEQUENCE.append(signal);
        if (CHEAT_SEND_WRITE_ANSWER.equals(SIGNAL_SEQUENCE.toString())) {
            presenter.checkRightAnswerCommandRecognized();
        }
    }
}