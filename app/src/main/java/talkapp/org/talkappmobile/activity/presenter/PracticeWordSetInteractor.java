package talkapp.org.talkappmobile.activity.presenter;

import android.media.AudioRecord;
import android.media.AudioTrack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.ByteUtils;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.RecordedTrack;
import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.component.WordsCombinator;
import talkapp.org.talkappmobile.component.backend.RefereeService;
import talkapp.org.talkappmobile.component.backend.SentenceService;
import talkapp.org.talkappmobile.component.backend.VoiceService;
import talkapp.org.talkappmobile.component.backend.WordSetExperienceService;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.model.UnrecognizedVoice;
import talkapp.org.talkappmobile.model.VoiceRecognitionResult;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class PracticeWordSetInteractor {
    public static final int WORDS_NUMBER = 6;
    private static final String TAG = PracticeWordSetInteractor.class.getSimpleName();
    @Inject
    WordsCombinator wordsCombinator;
    @Inject
    WordSetExperienceService wordSetExperienceService;
    @Inject
    AuthSign authSign;
    @Inject
    SentenceService sentenceService;
    @Inject
    SentenceSelector sentenceSelector;
    @Inject
    RefereeService refereeService;
    @Inject
    Logger logger;
    @Inject
    RecordedTrack recordedTrackBuffer;
    @Inject
    AudioStuffFactory audioStuffFactory;
    @Inject
    ByteUtils byteUtils;
    @Inject
    VoiceService voiceService;

    public PracticeWordSetInteractor() {
        DIContext.get().inject(this);
    }

    public void initialiseExperience(WordSet wordSet, OnPracticeWordSetListener listener) {
        if (wordSet.getExperience() == null) {
            wordSet.setExperience(createExperience(wordSet.getId()));
        }
        listener.onInitialiseExperience();
    }

    public void initialiseWordsSequence(WordSet wordSet, OnPracticeWordSetListener listener) {
        Set<String> set = wordsCombinator.combineWords(wordSet.getWords());
        wordSet.setWords(new ArrayList<>(set));
    }

    public void initialiseSentence(WordSet wordSet, final OnPracticeWordSetListener listener) {
        List<String> words = wordSet.getWords();
        String word = words.remove(0);
        List<Sentence> sentences = findSentencesByWords(word);
        if (sentences.isEmpty()) {
            logger.w(TAG, "Sentences haven't been found with words '{}'. Fill the storage.", word);
            return;
        }
        final Sentence sentence = sentenceSelector.getSentence(sentences);
        listener.onSentencesFound(sentence);
    }

    public void checkAnswer(String answer, final WordSet wordSet, final Sentence sentence, final OnPracticeWordSetListener listener) {
        if (isEmpty(answer)) {
            listener.onAnswerEmpty();
            return;
        }
        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setWordSetExperienceId(wordSet.getExperience().getId());
        uncheckedAnswer.setActualAnswer(answer);
        uncheckedAnswer.setExpectedAnswer(sentence.getText());

        AnswerCheckingResult result = checkAnswer(uncheckedAnswer);
        if (!result.getErrors().isEmpty()) {
            listener.onSpellingOrGrammarError(result.getErrors());
            return;
        }

        if (result.getCurrentTrainingExperience() == 0) {
            listener.onAccuracyTooLowError();
            return;
        }

        wordSet.getExperience().setTrainingExperience(result.getCurrentTrainingExperience());
        listener.onUpdateProgress(result.getCurrentTrainingExperience());
        if (result.getCurrentTrainingExperience() == wordSet.getExperience().getMaxTrainingExperience()) {
            listener.onTrainingFinished();
            return;
        }
        listener.onRightAnswer();
    }

    private AnswerCheckingResult checkAnswer(UncheckedAnswer uncheckedAnswer) {
        try {
            return refereeService.checkAnswer(uncheckedAnswer, authSign).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private WordSetExperience createExperience(String wordSetId) {
        try {
            return wordSetExperienceService.create(wordSetId, authSign).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private List<Sentence> findSentencesByWords(String words) {
        try {
            return sentenceService.findByWords(words, WORDS_NUMBER, authSign).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void playVoice(OnPracticeWordSetListener listener) {
        if (recordedTrackBuffer.isEmpty()) {
            return;
        }
        try {
            listener.onStartPlaying();
            AudioTrack audioTrack = null;
            try {
                audioTrack = audioStuffFactory.createAudioTrack();
                audioTrack.play();
                byte[] bytes = recordedTrackBuffer.getAsOneArray();
                audioTrack.write(bytes, 0, recordedTrackBuffer.getPosition());
            } finally {
                if (audioTrack != null) {
                    audioTrack.release();
                }
            }
        } finally {
            listener.onStopPlaying();
        }
    }

    public void recVoice(int speechTimeoutMillis, OnPracticeWordSetListener listener) {
        recordedTrackBuffer.init();
        try {
            listener.onStartRecording();
            AudioRecord audioRecord = null;
            try {
                audioRecord = audioStuffFactory.createAudioRecord();
                audioRecord.startRecording();
                byte[] buffer = audioStuffFactory.createBuffer();
                long voiceStartedMillis = 0;
                long lastVoiceHeardMillis = Long.MAX_VALUE;
                while (!recordedTrackBuffer.isClosed()) {
                    buffer = new byte[buffer.length];
                    final int size = audioRecord.read(buffer, 0, buffer.length);
                    final long now = System.currentTimeMillis();
                    if (byteUtils.isHearingVoice(buffer, size)) {
                        if (lastVoiceHeardMillis == Long.MAX_VALUE) {
                            voiceStartedMillis = now;
                        }
                        recordedTrackBuffer.append(buffer);
                        lastVoiceHeardMillis = now;
                        long speechLength = now - voiceStartedMillis;
                        listener.onSnippetRecorded(speechLength, recordedTrackBuffer.getMaxSpeechLengthMillis());
                        if (speechLength > recordedTrackBuffer.getMaxSpeechLengthMillis()) {
                            break;
                        }
                    } else {
                        recordedTrackBuffer.append(buffer);
                        if (now - lastVoiceHeardMillis > speechTimeoutMillis) {
                            break;
                        }
                    }
                }
            } finally {
                if (audioRecord != null) {
                    audioRecord.release();
                }
            }
        } finally {
            listener.onStopRecording();
        }
    }

    public void recognizeVoice(OnPracticeWordSetListener listener) {
        UnrecognizedVoice voice = new UnrecognizedVoice();
        voice.setVoice(recordedTrackBuffer.getAsOneArray());
        VoiceRecognitionResult result = getVoiceRecognitionResult(voice);
        listener.onStopRecognition();
        if (result.getVariant().isEmpty()) {
            return;
        }
        listener.onGotRecognitionResult(result);
    }

    private VoiceRecognitionResult getVoiceRecognitionResult(UnrecognizedVoice voice) {
        VoiceRecognitionResult result;
        try {
            result = voiceService.recognize(voice, authSign).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    public void stopRecording() {
        recordedTrackBuffer.close();
    }
}