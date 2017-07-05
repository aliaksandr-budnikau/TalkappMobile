package talkapp.org.talkappmobile.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Set;
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
import talkapp.org.talkappmobile.service.RecordedTrack;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceSelector;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.VoiceService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordsCombinator;
import talkapp.org.talkappmobile.service.impl.VoicePlayingProcess;
import talkapp.org.talkappmobile.service.impl.VoiceRecordingProcess;

public class PracticeWordSetActivity extends Activity {
    public static final String WORD_SET_MAPPING = "wordSet";
    @Inject
    WordSetService wordSetService;
    @Inject
    RefereeService refereeService;
    @Inject
    SentenceService sentenceService;
    @Inject
    SentenceSelector sentenceSelector;
    @Inject
    WordsCombinator wordsCombinator;
    @Inject
    VoiceService voiceService;
    @Inject
    Executor executor;
    @Inject
    RecordedTrack recordedTrackBuffer;
    @Inject
    AudioProcessesFactory audioProcessesFactory;
    private VoiceRecordingProcess voiceRecordingProcess;
    private TextView originalText;
    private TextView answerText;
    private WordSet currentWordSet;
    private LinkedBlockingQueue<Sentence> sentenceBlockingQueue;
    private AsyncTask<WordSet, Sentence, Void> gameFlow = new AsyncTask<WordSet, Sentence, Void>() {
        @Override
        protected Void doInBackground(WordSet... words) {
            try {
                Set<String> combinations = wordsCombinator.combineWords(words[0].getWords());
                for (final String combination : combinations) {
                    List<Sentence> sentences = sentenceService.findByWords(combination).execute().body();
                    Sentence sentence = sentenceSelector.getSentence(sentences);
                    this.publishProgress(sentence);
                    sentenceBlockingQueue.put(sentence);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Sentence... values) {
            originalText.setText(values[0].getTranslations().get("russian"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_word_set);
        DIContext.get().inject(this);
        sentenceBlockingQueue = new LinkedBlockingQueue<>(1);
        currentWordSet = (WordSet) getIntent().getSerializableExtra(WORD_SET_MAPPING);
        originalText = findViewById(R.id.originalText);
        answerText = findViewById(R.id.answerText);
        gameFlow.executeOnExecutor(executor, currentWordSet);
    }

    public void onCheckAnswerButtonClick(View v) {
        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setWordSetId(currentWordSet.getId());
        uncheckedAnswer.setText(answerText.getText().toString());
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
}