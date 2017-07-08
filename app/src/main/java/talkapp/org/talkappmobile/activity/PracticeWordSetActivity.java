package talkapp.org.talkappmobile.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
import talkapp.org.talkappmobile.service.GameProcessesFactory;
import talkapp.org.talkappmobile.service.NothingGotException;
import talkapp.org.talkappmobile.service.RecordedTrack;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.VoiceService;
import talkapp.org.talkappmobile.service.impl.GameProcessCallback;
import talkapp.org.talkappmobile.service.impl.GameProcesses;
import talkapp.org.talkappmobile.service.impl.VoicePlayingProcess;
import talkapp.org.talkappmobile.service.impl.VoiceRecordingProcess;

public class PracticeWordSetActivity extends Activity {
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
    private VoiceRecordingProcess voiceRecordingProcess;
    private TextView originalText;
    private TextView answerText;
    private WordSet currentWordSet;
    private LinkedBlockingQueue<Sentence> sentenceBlockingQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_word_set);
        DIContext.get().inject(this);
        originalText = findViewById(R.id.originalText);
        answerText = findViewById(R.id.answerText);

        sentenceBlockingQueue = new LinkedBlockingQueue<>(1);
        currentWordSet = (WordSet) getIntent().getSerializableExtra(WORD_SET_MAPPING);
        GameFlow gameFlow = new GameFlow();
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
        uncheckedAnswer.setWordSetId(currentWordSet.getId());
        uncheckedAnswer.setActualAnswer(actualAnswer);
        uncheckedAnswer.setExpectedAnswer(sentenceBlockingQueue.peek().getText());
        refereeService.checkAnswer(uncheckedAnswer).enqueue(new Callback<AnswerCheckingResult>() {

            @Override
            public void onResponse(Call<AnswerCheckingResult> call, Response<AnswerCheckingResult> response) {
                AnswerCheckingResult result = response.body();
                if (result.getErrors().isEmpty()) {
                    if (result.getCurrentTrainingExperience() == currentWordSet.getMaxTrainingExperience()) {
                        Toast.makeText(getApplicationContext(), "Congratulations! You are won!", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Cool! Next sentence.", Toast.LENGTH_LONG).show();
                    try {
                        sentenceBlockingQueue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
            voiceRecordingProcess = audioProcessesFactory.createVoiceRecordingProcess(recordedTrackBuffer);
            RecordAudioAsyncTask recordTask = new RecordAudioAsyncTask();
            recordTask.executeOnExecutor(executor, voiceRecordingProcess);
        } else {
            voiceRecordingProcess.stop();
            voiceRecordingProcess = null;
        }
    }

    public void onHearVoiceButtonClick(View view) {
        if (recordedTrackBuffer.size() == 0) {
            return;
        }
        VoicePlayingProcess voicePlayingProcess = audioProcessesFactory.createVoicePlayingProcess(recordedTrackBuffer);
        PlayAudioAsyncTask playTask = new PlayAudioAsyncTask();
        playTask.executeOnExecutor(executor, voicePlayingProcess);
    }

    private class PlayAudioAsyncTask extends AsyncTask<VoicePlayingProcess, Integer, Void> {
        @Override
        protected Void doInBackground(VoicePlayingProcess... params) {
            params[0].play();
            return null;
        }
    }

    private class RecordAudioAsyncTask extends AsyncTask<VoiceRecordingProcess, Integer, VoiceRecognitionResult> {
        @Override
        protected VoiceRecognitionResult doInBackground(VoiceRecordingProcess... params) {
            params[0].rec();
            UnrecognizedVoice voice = new UnrecognizedVoice();
            voice.setVoice(recordedTrackBuffer.get());
            try {
                return voiceService.recognize(voice).execute().body();
            } catch (IOException e) {
                e.printStackTrace();
                return new VoiceRecognitionResult();
            }
        }

        @Override
        protected void onPostExecute(VoiceRecognitionResult result) {
            if (result.getVariant().isEmpty()) {
                return;
            }
            answerText.setText(result.getVariant().get(0));
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
        }

        @Override
        public void returnProgress(Sentence sentence) {
            try {
                sentenceBlockingQueue.put(sentence);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.publishProgress(sentence);
        }

        @Override
        public List<Sentence> findByWords(String words) {
            try {
                return sentenceService.findByWords(words).execute().body();
            } catch (IOException e) {
                throw new NothingGotException(e);
            }
        }
    }
}