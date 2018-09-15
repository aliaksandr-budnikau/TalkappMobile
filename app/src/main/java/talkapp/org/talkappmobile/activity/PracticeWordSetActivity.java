package talkapp.org.talkappmobile.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.component.AudioProcessesFactory;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.RecordedTrack;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.component.backend.VoiceService;
import talkapp.org.talkappmobile.component.impl.VoicePlayingProcess;
import talkapp.org.talkappmobile.component.impl.VoiceRecordingProcess;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.UnrecognizedVoice;
import talkapp.org.talkappmobile.model.VoiceRecognitionResult;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public class PracticeWordSetActivity extends AppCompatActivity implements PracticeWordSetView {
    public static final String WORD_SET_MAPPING = "wordSet";
    @Inject
    VoiceService voiceService;
    @Inject
    Executor executor;
    @Inject
    RecordedTrack recordedTrackBuffer;
    @Inject
    AudioProcessesFactory audioProcessesFactory;
    @Inject
    TextUtils textUtils;
    @Inject
    AuthSign authSign;
    @Inject
    WordSetExperienceUtils experienceUtils;
    @Inject
    Handler uiEventHandler;

    private VoiceRecordingProcess voiceRecordingProcess;
    private TextView originalText;
    private TextView rightAnswer;
    private TextView answerText;
    private ProgressBar recProgress;
    private ProgressBar wordSetProgress;
    private Button nextButton;
    private Button checkButton;
    private PracticeWordSetPresenter presenter;

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

        presenter = new PracticeWordSetPresenter((WordSet) getIntent().getSerializableExtra(WORD_SET_MAPPING), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
        nextSentenceButtonClick();
    }

    private void nextSentenceButtonClick() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
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
        if (voiceRecordingProcess == null) {
            RecordAudioAsyncTask recordTask = new RecordAudioAsyncTask();
            voiceRecordingProcess = audioProcessesFactory.createVoiceRecordingProcess(recordedTrackBuffer, recordTask);
            recordTask.executeOnExecutor(executor, voiceRecordingProcess);
        } else {
            voiceRecordingProcess.stop();
        }
    }

    public void onHearVoiceButtonClick(View view) {
        if (recordedTrackBuffer.isEmpty()) {
            return;
        }
        VoicePlayingProcess voicePlayingProcess = audioProcessesFactory.createVoicePlayingProcess(recordedTrackBuffer);
        PlayAudioAsyncTask playTask = new PlayAudioAsyncTask();
        playTask.executeOnExecutor(executor, voicePlayingProcess);
    }

    public void onNextButtonClick(View view) {
        nextSentenceButtonClick();
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

    private class PlayAudioAsyncTask extends AsyncTask<VoicePlayingProcess, Integer, Void> {
        @Override
        protected Void doInBackground(VoicePlayingProcess... params) {
            params[0].play();
            return null;
        }
    }

    private class RecordAudioAsyncTask extends AsyncTask<VoiceRecordingProcess, Long, VoiceRecognitionResult> implements ProgressCallback {
        @Override
        protected void onPreExecute() {
            recProgress.setProgress(0);
            recProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected VoiceRecognitionResult doInBackground(VoiceRecordingProcess... params) {
            params[0].rec();
            UnrecognizedVoice voice = new UnrecognizedVoice();
            voice.setVoice(recordedTrackBuffer.getAsOneArray());
            try {
                return voiceService.recognize(voice, authSign).execute().body();
            } catch (IOException e) {
                e.printStackTrace();
                return new VoiceRecognitionResult();
            }
        }

        @Override
        protected void onPostExecute(VoiceRecognitionResult result) {
            voiceRecordingProcess = null;
            recProgress.setVisibility(View.INVISIBLE);
            recProgress.setProgress(0);
            if (result.getVariant().isEmpty()) {
                return;
            }
            String textWithUpper = textUtils.toUpperCaseFirstLetter(result.getVariant().get(0));
            String textWithLastSymbol = textUtils.appendLastSymbol(textWithUpper, presenter.getSentence().getText());
            answerText.setText(textWithLastSymbol);
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            long speechLength = values[0];
            long maxSpeechLengthMillis = values[1];
            recProgress.setProgress((int) (speechLength / maxSpeechLengthMillis));
        }

        @Override
        public void markProgress(long speechLength, long maxSpeechLengthMillis) {
            publishProgress(speechLength, maxSpeechLengthMillis);
        }
    }
}