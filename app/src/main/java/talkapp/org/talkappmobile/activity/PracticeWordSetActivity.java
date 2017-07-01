package talkapp.org.talkappmobile.activity;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.LinkedList;
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
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceSelector;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.VoiceService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordsCombinator;

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
    int frequency = 11025, channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    RecordAudio recordTask;
    PlayAudio playTask;
    private TextView originalText;
    private TextView answerText;
    private WordSet currentWordSet;
    private LinkedBlockingQueue<Sentence> sentenceBlockingQueue;
    private boolean isRecording = false, isPlaying = false;
    private List<Short> bytes = new LinkedList<>();

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
        if (isRecording) {
            stopRecording();
        } else if (!isRecording) {
            record();
        }
    }

    public void onHearVoiceButtonClick(View view) {
        if (isPlaying) {
            stopPlaying();
        } else if (!isPlaying) {
            play();
        }
    }

    public void play() {
        playTask = new PlayAudio();
        playTask.executeOnExecutor(executor);
    }

    public void stopPlaying() {
        isPlaying = false;
    }

    public void record() {
        recordTask = new RecordAudio();
        recordTask.executeOnExecutor(executor);
    }

    public void stopRecording() {
        isRecording = false;
    }

    private class PlayAudio extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            isPlaying = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            int bufferSize = AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
            short[] audiodata = new short[bufferSize / 4];

            AudioTrack audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC, frequency,
                    channelConfiguration, audioEncoding, bufferSize,
                    AudioTrack.MODE_STREAM);

            audioTrack.play();
            Iterator<Short> iterator = bytes.iterator();
            while (isPlaying && iterator.hasNext()) {
                for (int i = 0; iterator.hasNext() && i < audiodata.length; i++) {
                    audiodata[i] = iterator.next();
                }
                audioTrack.write(audiodata, 0, audiodata.length);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isPlaying = false;
        }
    }

    private class RecordAudio extends AsyncTask<Void, Integer, Void> {

        protected void onPreExecute() {
            bytes.clear();
            isRecording = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            int bufferSize = AudioRecord.getMinBufferSize(frequency,
                    channelConfiguration, audioEncoding);
            AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC, frequency,
                    channelConfiguration, audioEncoding, bufferSize);

            short[] buffer = new short[bufferSize];
            audioRecord.startRecording();
            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                for (int i = 0; i < bufferReadResult; i++) {
                    bytes.add(buffer[i]);
                }
            }
            audioRecord.stop();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isRecording = false;
        }
    }
}