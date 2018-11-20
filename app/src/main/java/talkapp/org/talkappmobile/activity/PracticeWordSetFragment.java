package talkapp.org.talkappmobile.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import butterknife.Unbinder;
import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenter;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewHideAllStrategy;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewHideNewWordOnlyStrategy;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.ViewStrategyFactory;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.WordSet;

import static android.app.Activity.RESULT_OK;

public class PracticeWordSetFragment extends Fragment implements PracticeWordSetView {
    public static final String WORD_SET_MAPPING = "wordSet";
    private static final String CHEAT_SEND_WRITE_ANSWER = "LLCLPCLL";
    private final StringBuilder SIGNAL_SEQUENCE = new StringBuilder("12345678");

    @Inject
    Executor executor;
    @Inject
    Handler uiEventHandler;
    @Inject
    PracticeWordSetInteractor interactor;
    @Inject
    ViewStrategyFactory viewStrategyFactory;
    @Inject
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;

    @BindView(R.id.originalText)
    TextView originalText;
    @BindView(R.id.rightAnswer)
    TextView rightAnswer;
    @BindView(R.id.answerText)
    TextView answerText;
    @BindView(R.id.wordSetProgress)
    ProgressBar wordSetProgress;
    @BindView(R.id.nextButton)
    Button nextButton;
    @BindView(R.id.checkButton)
    Button checkButton;
    @BindView(R.id.speakButton)
    Button speakButton;
    @BindView(R.id.playButton)
    Button playButton;
    @BindView(R.id.pronounceRightAnswerButton)
    Button pronounceRightAnswerButton;
    @BindView(R.id.please_wait_progress_bar)
    View pleaseWaitProgressBar;
    @BindView(R.id.word_set_practise_form)
    View wordSetPractiseForm;
    @BindView(R.id.spellingGrammarErrorsListView)
    LinearLayout spellingGrammarErrorsListView;

    private WaitingForProgressBarManager waitingForProgressBarManager;
    private Unbinder unbinder;
    private PracticeWordSetPresenter presenter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PracticeWordSetFragment newInstance(WordSet wordSet) {
        PracticeWordSetFragment fragment = new PracticeWordSetFragment();
        Bundle args = new Bundle();
        args.putSerializable(WORD_SET_MAPPING, wordSet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DIContextUtils.get().inject(this);
        View inflate = inflater.inflate(R.layout.word_set_practice_activity_fragment, container, false);
        unbinder = ButterKnife.bind(this, inflate);

        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(pleaseWaitProgressBar, wordSetPractiseForm);

        WordSet wordSet = (WordSet) getArguments().get(WORD_SET_MAPPING);

        PracticeWordSetViewHideNewWordOnlyStrategy newWordOnlyStrategy = viewStrategyFactory.createPracticeWordSetViewHideNewWordOnlyStrategy(this);
        PracticeWordSetViewHideAllStrategy hideAllStrategy = viewStrategyFactory.createPracticeWordSetViewHideAllStrategy(this);
        presenter = new PracticeWordSetPresenter(wordSet, interactor, newWordOnlyStrategy, hideAllStrategy);

        new MyAsyncTask(() -> {
            presenter.initialise();
            presenter.nextButtonClick();
        }).executeOnExecutor(executor);
        return inflate;
    }

    @OnTouch(R.id.rightAnswer)
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

    @OnClick(R.id.nextButton)
    public void onNextButtonClick() {
        new MyAsyncTask(() -> {
            presenter.nextButtonClick();
        }).executeOnExecutor(executor);
    }

    @OnClick(R.id.playButton)
    public void onPlayVoiceButtonClick() {
        new MyAsyncTask(() -> {
            presenter.playVoiceButtonClick();
        }).executeOnExecutor(executor);
        sendCheatSignal("L");
    }

    @OnClick(R.id.pronounceRightAnswerButton)
    public void onPronounceRightAnswerButtonClick() {
        new MyAsyncTask(() -> {
            presenter.pronounceRightAnswerButtonClick();
        }).executeOnExecutor(executor);
        sendCheatSignal("P");
    }

    @OnClick(R.id.checkButton)
    public void onCheckAnswerButtonClick() {
        new MyAsyncTask(() -> {
            presenter.checkAnswerButtonClick(answerText.getText().toString());
        }).executeOnExecutor(executor);
        sendCheatSignal("C");
    }

    @OnClick(R.id.speakButton)
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void showNextButton() {
        uiEventHandler.post(() -> nextButton.setVisibility(View.VISIBLE));
    }

    @Override
    public void hideNextButton() {
        uiEventHandler.post(() -> nextButton.setVisibility(View.GONE));
    }

    @Override
    public void showPleaseWaitProgressBar() {
        uiEventHandler.post(() -> waitingForProgressBarManager.showProgressBar());
    }

    @Override
    public void hidePleaseWaitProgressBar() {
        uiEventHandler.post(() -> waitingForProgressBarManager.hideProgressBar());
    }

    @Override
    public void showCheckButton() {
        uiEventHandler.post(() -> checkButton.setVisibility(View.VISIBLE));
    }

    @Override
    public void hideCheckButton() {
        uiEventHandler.post(() -> checkButton.setVisibility(View.GONE));
    }

    @Override
    public void setRightAnswer(final String text) {
        uiEventHandler.post(() -> rightAnswer.setText(text));
    }

    @Override
    public void setProgress(int progress) {
        wordSetProgress.setProgress(progress);
    }

    @Override
    public void setOriginalText(final String text) {
        uiEventHandler.post(() -> originalText.setText(text));
    }

    @Override
    public void showMessageAnswerEmpty() {
        uiEventHandler.post(() -> Toast.makeText(getContext(), "Answer can't be empty.", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void showMessageSpellingOrGrammarError() {
        uiEventHandler.post(() -> Toast.makeText(getContext(), "Spelling or grammar errors", Toast.LENGTH_LONG).show());
    }

    @Override
    public void showMessageAccuracyTooLow() {
        uiEventHandler.post(() -> Toast.makeText(getContext(), "Accuracy too low", Toast.LENGTH_LONG).show());
    }

    @Override
    public void showCongratulationMessage() {
        uiEventHandler.post(() -> Toast.makeText(getContext(), "Congratulations! You won!", Toast.LENGTH_LONG).show());
    }

    @Override
    public void closeActivity() {
        uiEventHandler.post(() -> getActivity().finish());
    }

    @Override
    public void openAnotherActivity() {
        uiEventHandler.post(() -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void setEnableVoiceRecButton(final boolean value) {
        uiEventHandler.post(() -> speakButton.setEnabled(value));
    }

    @Override
    public void setEnablePronounceRightAnswerButton(final boolean value) {
        uiEventHandler.post(() -> pronounceRightAnswerButton.setEnabled(value));
    }

    @Override
    public void setEnableCheckButton(final boolean value) {
        uiEventHandler.post(() -> checkButton.setEnabled(value));
    }

    @Override
    public void setEnableNextButton(final boolean value) {
        uiEventHandler.post(() -> nextButton.setEnabled(value));
    }

    @Override
    public void setAnswerText(final String text) {
        uiEventHandler.post(() -> answerText.setText(text));
    }

    @Override
    public void showSpellingOrGrammarErrorPanel(final String errorMessage) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        uiEventHandler.post(() -> {
            View vi = inflater.inflate(R.layout.row_spelling_grammar_errors_list_item, null);
            vi.setOnClickListener(v -> Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show());
            TextView textView = vi.findViewById(R.id.errorRow);
            textView.setText(errorMessage);
            spellingGrammarErrorsListView.addView(vi);
        });
    }

    @Override
    public void hideSpellingOrGrammarErrorPanel() {
        uiEventHandler.post(() -> spellingGrammarErrorsListView.removeAllViews());
    }

    @Override
    public void setEnableRightAnswerTextView(final boolean value) {
        uiEventHandler.post(() -> rightAnswer.setEnabled(value));
    }

    private void sendCheatSignal(final String signal) {
        SIGNAL_SEQUENCE.deleteCharAt(0);
        SIGNAL_SEQUENCE.append(signal);
        if (CHEAT_SEND_WRITE_ANSWER.equals(SIGNAL_SEQUENCE.toString())) {
            new MyAsyncTask(() -> {
                presenter.checkRightAnswerCommandRecognized();
            }).executeOnExecutor(executor);
        }
    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private final Runnable runnable;

        MyAsyncTask(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            runnable.run();
            return null;
        }
    }
}