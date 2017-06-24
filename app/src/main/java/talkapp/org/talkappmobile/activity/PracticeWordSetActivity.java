package talkapp.org.talkappmobile.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Set;
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
        originalText = (TextView) findViewById(R.id.originalText);
        answerText = (TextView) findViewById(R.id.answerText);
        gameFlow.execute(currentWordSet);
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
}