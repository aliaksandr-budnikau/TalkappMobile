package talkapp.org.talkappmobile.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.model.UnrecognizedVoice;
import talkapp.org.talkappmobile.model.VoiceRecognitionResult;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.AudioProcessesFactory;
import talkapp.org.talkappmobile.service.AuthSign;
import talkapp.org.talkappmobile.service.GameProcessesFactory;
import talkapp.org.talkappmobile.service.NothingGotException;
import talkapp.org.talkappmobile.service.RecordedTrack;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.TextUtils;
import talkapp.org.talkappmobile.service.VoiceService;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;
import talkapp.org.talkappmobile.service.impl.GameProcessCallback;
import talkapp.org.talkappmobile.service.impl.GameProcesses;
import talkapp.org.talkappmobile.service.impl.VoicePlayingProcess;
import talkapp.org.talkappmobile.service.impl.VoiceRecordingProcess;

public class PracticeWordSetActivity extends AppCompatActivity {
    public static final String WORD_SET_MAPPING = "wordSet";
    @Inject
    RefereeService refereeService;
    @Inject
    SentenceService sentenceService;
    @Inject
    VoiceService voiceService;
    @Inject
    Executor executor;
    @Inject
    RecordedTrack recordedTrackBuffer;
    @Inject
    AudioProcessesFactory audioProcessesFactory;
    @Inject
    GameProcessesFactory gameProcessesFactory;
    @Inject
    TextUtils textUtils;
    @Inject
    AuthSign authSign;
    @Inject
    WordSetExperienceUtils experienceUtils;

    private VoiceRecordingProcess voiceRecordingProcess;
    private TextView originalText;
    private TextView rightAnswer;
    private TextView answerText;
    private WordSet currentWordSet;
    private LinkedBlockingQueue<Sentence> sentenceBlockingQueue;
    private GameFlow gameFlow;
    private ProgressBar recProgress;
    private ProgressBar wordSetProgress;
    private Button nextButton;
    private Button checkButton;

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

        sentenceBlockingQueue = new LinkedBlockingQueue<>(1);
        currentWordSet = (WordSet) getIntent().getSerializableExtra(WORD_SET_MAPPING);
        wordSetProgress.setProgress(experienceUtils.getProgress(currentWordSet.getExperience()));
        gameFlow = new GameFlow();
        GameProcesses gameProcesses = gameProcessesFactory.createGameProcesses(currentWordSet, gameFlow);
        gameFlow.executeOnExecutor(executor, gameProcesses);
    }

    public void onCheckAnswerButtonClick(View v) {
        String actualAnswer = answerText.getText().toString();
        if (StringUtils.isEmpty(actualAnswer)) {
            Toast.makeText(getApplicationContext(), "Answer can't be empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setWordSetExperienceId(currentWordSet.getId());
        uncheckedAnswer.setActualAnswer(actualAnswer);
        uncheckedAnswer.setExpectedAnswer(sentenceBlockingQueue.peek().getText());
        refereeService.checkAnswer(uncheckedAnswer, authSign).enqueue(new Callback<AnswerCheckingResult>() {

            @Override
            public void onResponse(Call<AnswerCheckingResult> call, Response<AnswerCheckingResult> response) {
                AnswerCheckingResult result = response.body();
                if (result.getErrors().isEmpty()) {
                    wordSetProgress.setProgress(experienceUtils.getProgress(currentWordSet.getExperience(), result.getCurrentTrainingExperience()));
                    if (result.getCurrentTrainingExperience() == currentWordSet.getExperience().getMaxTrainingExperience()) {
                        Toast.makeText(getApplicationContext(), "Congratulations! You are won!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PracticeWordSetActivity.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                        return;
                    }
                    Sentence sentence = sentenceBlockingQueue.peek();
                    rightAnswer.setText(sentence.getText());
                    nextButton.setVisibility(View.VISIBLE);
                    checkButton.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getApplicationContext(), "Spelling or grammar errors", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AnswerCheckingResult> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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

    @Override
    protected void onStop() {
        super.onStop();
        gameFlow.cancel(true);
    }

    public void onNextButtonClick(View view) {
        answerText.setText("");
        try {
            sentenceBlockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Cool! Next sentence.", Toast.LENGTH_LONG).show();
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
            String textWithLastSymbol = textUtils.appendLastSymbol(textWithUpper,
                    sentenceBlockingQueue.peek().getText());
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

    private class GameFlow extends AsyncTask<GameProcesses, Sentence, Void> implements GameProcessCallback {

        @Override
        protected Void doInBackground(GameProcesses... gameProcesses) {
            gameProcesses[0].start();
            return null;
        }

        @Override
        protected void onProgressUpdate(Sentence... values) {
            originalText.setText(values[0].getTranslations().get("russian"));
            rightAnswer.setText(textUtils.screenTextWith(values[0].getText()));
        }

        @Override
        public void returnProgress(Sentence sentence) throws InterruptedException {
            sentenceBlockingQueue.put(sentence);
            this.publishProgress(sentence);
        }

        @Override
        public List<Sentence> findByWords(String words) {
            try {
                return sentenceService.findByWords(words, authSign).execute().body();
            } catch (IOException e) {
                throw new NothingGotException(e);
            }
        }

        @Override
        public void onFinish() {
            PracticeWordSetActivity.this.finish();
        }

        @Override
        public void onInterruption() {
            Toast.makeText(getApplicationContext(), "Interrupted", Toast.LENGTH_SHORT).show();
        }
    }
}