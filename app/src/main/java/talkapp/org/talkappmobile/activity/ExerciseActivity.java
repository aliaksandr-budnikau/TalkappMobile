package talkapp.org.talkappmobile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

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
import talkapp.org.talkappmobile.service.DataSource;
import talkapp.org.talkappmobile.service.EventHandler;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceNotFoundException;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.TranslationExercise;
import talkapp.org.talkappmobile.service.WordSetNotFoundException;
import talkapp.org.talkappmobile.service.WordSetService;

public class ExerciseActivity extends Activity {
    @Inject
    WordSetService wordSetService;
    @Inject
    RefereeService refereeService;
    @Inject
    SentenceService sentenceService;
    @Inject
    TranslationExercise translationExercise;

    private TextView originalText;
    private TextView answerText;
    private ImageButton checkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        DIContext.get().inject(this);
        originalText = (TextView) findViewById(R.id.originalText);
        answerText = (TextView) findViewById(R.id.answerText);
        initCheckButton();
        initTranslationExerciseLifeCycle();
    }

    private void initTranslationExerciseLifeCycle() {
        translationExercise.setDataSource(new DataSource() {
            @Override
            public String getWordSetId() {
                return "qwe0";
            }

            @Override
            public WordSet findWordSetById(String id) throws IOException, WordSetNotFoundException {
                Response<WordSet> response = wordSetService.findById(id).execute();
                WordSet wordSet = response.body();
                if (wordSet == null || wordSet.getWords().isEmpty()) {
                    throw new WordSetNotFoundException("With id " + id);
                }
                return wordSet;
            }

            @Override
            public List<Sentence> findSentencesByWords(String words) throws SentenceNotFoundException, IOException {
                Response<List<Sentence>> response = sentenceService.findByWords(words).execute();
                List<Sentence> sentences = response.body();
                if (sentences.isEmpty()) {
                    throw new SentenceNotFoundException();
                }
                return sentences;
            }
        });
        translationExercise.setEventHandler(new EventHandler() {
            @Override
            public void onNextTask(AnswerCheckingResult result, WordSet wordSet) {
                Toast.makeText(getApplicationContext(), "Cool! Next sentence.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onWin(AnswerCheckingResult result, WordSet wordSet) {
                Toast.makeText(getApplicationContext(), "Congratulations! You are won!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onErrors(AnswerCheckingResult result, WordSet wordSet) {
                Toast.makeText(getApplicationContext(), "Spelling or grammar errors", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onWordSetNotFound(String wordSetId) {
                Toast.makeText(getApplicationContext(), "Word set is not found", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Exception e) {
                Log.e("ExerciseLifeCycle", "Error", e);
            }

            @Override
            public void onDestroy() {

            }

            @Override
            public void onSentenceNotFound(String words, WordSet wordSet) {
                Toast.makeText(getApplicationContext(), "Sentence with (" + words + ") is not found", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNewSentenceGot(final Sentence sentence, String combination, WordSet wordSet) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        originalText.setText(sentence.getTranslations().get("russian"));
                    }
                });
            }
        });
        new Thread(translationExercise).start();
    }

    private void initCheckButton() {
        checkButton = (ImageButton) findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
                uncheckedAnswer.setWordSetId("qwe0");
                uncheckedAnswer.setText(answerText.getText().toString());
                refereeService.checkAnswer(uncheckedAnswer).enqueue(new Callback<AnswerCheckingResult>() {
                    @Override
                    public void onResponse(Call<AnswerCheckingResult> call, Response<AnswerCheckingResult> response) {
                        translationExercise.analyzeCheckingResult(response.body());
                    }

                    @Override
                    public void onFailure(Call<AnswerCheckingResult> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
