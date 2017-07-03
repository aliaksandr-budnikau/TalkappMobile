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

import java.io.IOException;
import java.nio.ByteBuffer;
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
import talkapp.org.talkappmobile.model.UnrecognizedVoice;
import talkapp.org.talkappmobile.model.VoiceRecognitionResult;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceSelector;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.VoiceService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordsCombinator;

public class PracticeWordSetActivity extends Activity {
    public static final String WORD_SET_MAPPING = "wordSet";
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int AMPLITUDE_THRESHOLD = 1500;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int SPEECH_TIMEOUT_MILLIS = 2000;
    private static final int[] SAMPLE_RATE_CANDIDATES = new int[]{16000, 11025, 22050, 44100};
    private static final int MAX_SPEECH_LENGTH_MILLIS = 30 * 1000;
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
    RecordAudio recordTask;
    PlayAudio playTask;
    private TextView originalText;
    private TextView answerText;
    private WordSet currentWordSet;
    private LinkedBlockingQueue<Sentence> sentenceBlockingQueue;
    private boolean isRecording = false;
    private List<Byte> bytes = new LinkedList<>();
    private byte[] buffer;
    private long lastVoiceHeardMillis = Long.MAX_VALUE;
    private long voiceStartedMillis;
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
    private AudioRecord audioRecord;

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
        play();
    }

    public void play() {
        playTask = new PlayAudio();
        playTask.executeOnExecutor(executor);
    }

    public void record() {
        recordTask = new RecordAudio();
        recordTask.executeOnExecutor(executor);
    }

    public void stopRecording() {
        isRecording = false;
    }

    private AudioRecord createAudioRecord() {
        for (int sampleRate : SAMPLE_RATE_CANDIDATES) {
            final int sizeInBytes = AudioRecord.getMinBufferSize(sampleRate, CHANNEL, ENCODING);
            if (sizeInBytes == AudioRecord.ERROR_BAD_VALUE) {
                continue;
            }
            final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    sampleRate, CHANNEL, ENCODING, sizeInBytes);
            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                buffer = new byte[sizeInBytes];
                return audioRecord;
            } else {
                audioRecord.release();
            }
        }
        return null;
    }

    private AudioTrack createAudioTrack() {
        for (int sampleRate : SAMPLE_RATE_CANDIDATES) {
            final int sizeInBytes = AudioTrack.getMinBufferSize(sampleRate, CHANNEL, ENCODING);
            if (sizeInBytes == AudioTrack.ERROR_BAD_VALUE) {
                continue;
            }
            final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, ENCODING, sizeInBytes, AudioTrack.MODE_STREAM);
            if (audioTrack.getState() == AudioRecord.STATE_INITIALIZED) {
                return audioTrack;
            } else {
                audioTrack.release();
            }
        }
        return null;
    }

    private void addToBytesList(int size) {
        for (int i = 0; i < size; i++) {
            bytes.add(buffer[i]);
        }
    }

    private boolean isHearingVoice(byte[] buffer, int size) {
        for (int i = 0; i < size - 1; i += 2) {
            // The buffer has LINEAR16 in little endian.
            int s = buffer[i + 1];
            if (s < 0) s *= -1;
            s <<= 8;
            s += Math.abs(buffer[i]);
            if (s > AMPLITUDE_THRESHOLD) {
                return true;
            }
        }
        return false;
    }

    private void end() {
        lastVoiceHeardMillis = Long.MAX_VALUE;
    }

    private class PlayAudio extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            AudioTrack audioTrack = createAudioTrack();
            audioTrack.play();
            ByteBuffer byteBuf = ByteBuffer.allocate(bytes.size());
            for (Iterator<Byte> i = bytes.iterator(); i.hasNext(); ) {
                byteBuf.put(i.next());
            }
            byte[] array = byteBuf.array();
            audioTrack.write(array, 0, array.length);
            return null;
        }
    }

    private class RecordAudio extends AsyncTask<Void, Integer, VoiceRecognitionResult> {

        protected void onPreExecute() {
            bytes.clear();
            isRecording = true;
        }

        @Override
        protected VoiceRecognitionResult doInBackground(Void... params) {
            audioRecord = createAudioRecord();
            if (audioRecord == null) {
                throw new RuntimeException("Cannot instantiate AudioRecord");
            }
            audioRecord.startRecording();

            while (isRecording) {
                final int size = audioRecord.read(buffer, 0, buffer.length);
                final long now = System.currentTimeMillis();
                if (isHearingVoice(buffer, size)) {
                    if (lastVoiceHeardMillis == Long.MAX_VALUE) {
                        voiceStartedMillis = now;
                    }
                    addToBytesList(size);
                    lastVoiceHeardMillis = now;
                    if (now - voiceStartedMillis > MAX_SPEECH_LENGTH_MILLIS) {
                        end();
                    }
                } else if (lastVoiceHeardMillis != Long.MAX_VALUE) {
                    addToBytesList(size);
                    if (now - lastVoiceHeardMillis > SPEECH_TIMEOUT_MILLIS) {
                        end();
                    }
                }
            }
            ByteBuffer byteBuf = ByteBuffer.allocate(bytes.size());
            for (Iterator<Byte> i = bytes.iterator(); i.hasNext(); ) {
                byteBuf.put(i.next());
            }

            UnrecognizedVoice voice = new UnrecognizedVoice();
            voice.setVoice(byteBuf.array());
            try {
                return voiceService.recognize(voice).execute().body();
            } catch (IOException e) {
                e.printStackTrace();
                return new VoiceRecognitionResult();
            }
        }

        @Override
        protected void onPostExecute(VoiceRecognitionResult result) {
            isRecording = false;
            if (result.getVariant().isEmpty()) {
                return;
            }
            answerText.setText(result.getVariant().get(0));
        }
    }
}