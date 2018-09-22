package talkapp.org.talkappmobile.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.RecordedTrack;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static android.os.AsyncTask.Status.RUNNING;

public class PracticeWordSetActivity extends AppCompatActivity implements PracticeWordSetView {
    public static final String WORD_SET_MAPPING = "wordSet";

    @Inject
    Executor executor;
    @Inject
    RecordedTrack recordedTrackBuffer;
    @Inject
    TextUtils textUtils;
    @Inject
    AuthSign authSign;
    @Inject
    WordSetExperienceUtils experienceUtils;
    @Inject
    Handler uiEventHandler;

    private TextView originalText;
    private TextView rightAnswer;
    private TextView answerText;
    private ProgressBar recProgress;
    private ProgressBar wordSetProgress;
    private Button nextButton;
    private Button checkButton;
    private Button speakButton;
    private Button playButton;
    private LinearLayout spellingGrammarErrorsListView;

    private PracticeWordSetPresenter presenter;
    private AsyncTask<Void, Void, Void> asyncTask;
    private View.OnTouchListener rightAnswerOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_word_set);
        DIContext.get().inject(this);

        originalText = (TextView) findViewById(R.id.originalText);
        rightAnswer = (TextView) findViewById(R.id.rightAnswer);
        answerText = (TextView) findViewById(R.id.answerText);
        recProgress = (ProgressBar) findViewById(R.id.recProgress);
        wordSetProgress = (ProgressBar) findViewById(R.id.wordSetProgress);
        nextButton = (Button) findViewById(R.id.nextButton);
        checkButton = (Button) findViewById(R.id.checkButton);
        speakButton = (Button) findViewById(R.id.speakButton);
        playButton = (Button) findViewById(R.id.playButton);

        spellingGrammarErrorsListView = (LinearLayout) findViewById(R.id.spellingGrammarErrorsListView);
        rightAnswer.setOnTouchListener(rightAnswerOnTouchListener);

        presenter = new PracticeWordSetPresenter((WordSet) getIntent().getSerializableExtra(WORD_SET_MAPPING), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                presenter.onResume();
                presenter.onNextButtonClick();
                return null;
            }
        }.executeOnExecutor(executor);
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    public void onCheckAnswerButtonClick(View v) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                presenter.onCheckAnswerButtonClick(answerText.getText().toString());
                return null;
            }
        }.executeOnExecutor(executor);
    }

    public void onRecogniseVoiceButtonClick(View view) {
        if (asyncTask == null || asyncTask.getStatus() != RUNNING) {
            asyncTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    presenter.onRecogniseVoiceButtonClick();
                    return null;
                }
            }.executeOnExecutor(executor);
            return;
        }
        presenter.onStopRecognitionVoiceButtonClick();
    }

    public void onPlayVoiceButtonClick(View view) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                presenter.onPlayVoiceButtonClick();
                return null;
            }
        }.executeOnExecutor(executor);
    }

    public void onNextButtonClick(View view) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                presenter.onNextButtonClick();
                return null;
            }
        }.executeOnExecutor(executor);
    }

    @Override
    public void showNextButton() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                nextButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hideNextButton() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                nextButton.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showCheckButton() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                checkButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hideCheckButton() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                checkButton.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void setRightAnswer(final Sentence sentence) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                rightAnswer.setText(sentence.getText());
            }
        });
    }

    @Override
    public void setProgress(WordSetExperience exp) {
        wordSetProgress.setProgress(experienceUtils.getProgress(exp.getTrainingExperience(), exp.getMaxTrainingExperience()));
    }

    @Override
    public void setOriginalText(final Sentence sentence) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                originalText.setText(sentence.getTranslations().get("russian"));
            }
        });
    }

    @Override
    public void setHiddenRightAnswer(final Sentence sentence) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                rightAnswer.setText(textUtils.screenTextWith(sentence.getText()));
            }
        });
    }

    @Override
    public void setAnswerText(Sentence sentence) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                answerText.setText("");
            }
        });
    }

    @Override
    public void showMessageAnswerEmpty() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Answer can't be empty.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void showMessageSpellingOrGrammarError() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Spelling or grammar errors", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void showMessageAccuracyTooLow() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Accuracy too low", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void updateProgress(WordSetExperience experience, int currentTrainingExperience) {
        wordSetProgress.setProgress(experienceUtils.getProgress(currentTrainingExperience, experience.getMaxTrainingExperience()));
    }

    @Override
    public void showCongratulationMessage() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Congratulations! You won!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void closeActivity() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    @Override
    public void openAnotherActivity() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PracticeWordSetActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void setEnableVoiceRecButton(final boolean value) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                speakButton.setEnabled(value);
            }
        });
    }

    @Override
    public void setEnablePlayButton(final boolean value) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                playButton.setEnabled(value);
            }
        });
    }

    @Override
    public void setEnableCheckButton(final boolean value) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                checkButton.setEnabled(value);
            }
        });
    }

    @Override
    public void setEnableNextButton(final boolean value) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                nextButton.setEnabled(value);
            }
        });
    }

    @Override
    public void setRecProgress(final int value) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                recProgress.setProgress(value);
            }
        });
    }

    @Override
    public void hideRecProgress() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                recProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void setAnswerText(final String text) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                answerText.setText(text);
            }
        });
    }

    @Override
    public void setEnableRightAnswer(final boolean value) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                rightAnswer.setEnabled(value);
            }
        });
    }

    @Override
    public void showSpellingOrGrammarErrorPanel(final String errorMessage) {
        final LayoutInflater inflater = getLayoutInflater();
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                View vi = inflater.inflate(R.layout.row_spelling_grammar_errors_list_item, null);
                vi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
                TextView textView = vi.findViewById(R.id.errorRow);
                textView.setText(errorMessage);
                spellingGrammarErrorsListView.addView(vi);
            }
        });
    }

    @Override
    public void hideSpellingOrGrammarErrorPanel() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                spellingGrammarErrorsListView.removeAllViews();
            }
        });
    }

    @Override
    public void showRecProgress() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                recProgress.setVisibility(View.VISIBLE);
            }
        });
    }
}