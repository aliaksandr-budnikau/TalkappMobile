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
import java.util.concurrent.Semaphore;

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
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordsCombinator;

public class PracticeWordSetActivity extends Activity implements View.OnClickListener, Callback<AnswerCheckingResult> {
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
    private Semaphore semaphore = new Semaphore(0);

    private TextView originalText;
    private TextView answerText;
    private WordSet currentWordSet;

    private AsyncTask<String, Object, List<Sentence>> loadingSentences = new AsyncTask<String, Object, List<Sentence>>() {
        @Override
        protected List<Sentence> doInBackground(String... params) {
            Call<List<Sentence>> call = sentenceService.findByWords(params[0]);
            Response<List<Sentence>> execute = null;
            try {
                execute = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return execute.body();
        }

        @Override
        protected void onPostExecute(List<Sentence> sentences) {
            final Sentence sentence = sentenceSelector.getSentence(sentences);
            originalText.setText(sentence.getTranslations().get("russian"));
        }
    };

    private AsyncTask<WordSet, Object, Void> practiceExercise = new AsyncTask<WordSet, Object, Void>() {
        @Override
        protected Void doInBackground(WordSet... words) {
            try {
                Set<String> combinations = wordsCombinator.combineWords(words[0].getWords());
                for (final String combination : combinations) {
                    loadingSentences.execute(combination);
                    semaphore.acquire();
                }
            } catch (Exception e) {
                // TODO
            }
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_word_set);
        DIContext.get().inject(this);
        currentWordSet = (WordSet) getIntent().getSerializableExtra(WORD_SET_MAPPING);
        originalText = (TextView) findViewById(R.id.originalText);
        answerText = (TextView) findViewById(R.id.answerText);
        findViewById(R.id.checkButton).setOnClickListener(this);
        practiceExercise.execute(currentWordSet);
    }

    @Override
    public void onClick(View v) {
        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setWordSetId(currentWordSet.getId());
        uncheckedAnswer.setText(answerText.getText().toString());
        refereeService.checkAnswer(uncheckedAnswer).enqueue(PracticeWordSetActivity.this);
    }

    @Override
    public void onResponse(Call<AnswerCheckingResult> call, Response<AnswerCheckingResult> response) {
        AnswerCheckingResult result = response.body();
        if (result.getErrors().isEmpty()) {
            if (result.getCurrentTrainingExperience() == currentWordSet.getMaxTrainingExperience()) {
                Toast.makeText(getApplicationContext(), "Congratulations! You are won!", Toast.LENGTH_LONG).show();
                finish();
            }
            Toast.makeText(getApplicationContext(), "Cool! Next sentence.", Toast.LENGTH_LONG).show();
            semaphore.release();
        } else {
            Toast.makeText(getApplicationContext(), "Spelling or grammar errors", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<AnswerCheckingResult> call, Throwable t) {
        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
    }
}