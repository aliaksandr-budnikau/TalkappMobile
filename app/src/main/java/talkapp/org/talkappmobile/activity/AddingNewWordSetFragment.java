package talkapp.org.talkappmobile.activity;

import android.app.Fragment;
import android.content.Intent;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.interactor.AddingNewWordSetInteractor;
import talkapp.org.talkappmobile.activity.presenter.AddingNewWordSetPresenter;
import talkapp.org.talkappmobile.activity.view.AddingNewWordSetFragmentView;
import talkapp.org.talkappmobile.component.backend.BackendServerFactory;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.model.WordSet;

import static java.util.Arrays.asList;

@EFragment(value = R.layout.adding_new_word_set_layout)
public class AddingNewWordSetFragment extends Fragment implements AddingNewWordSetFragmentView {
    @Bean(BackendServerFactoryBean.class)
    BackendServerFactory backendServerFactory;
    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;
    @ViewById(R.id.word1)
    TextView word1;
    @ViewById(R.id.word2)
    TextView word2;
    @ViewById(R.id.word3)
    TextView word3;
    @ViewById(R.id.word4)
    TextView word4;
    @ViewById(R.id.word5)
    TextView word5;
    @ViewById(R.id.word6)
    TextView word6;
    @ViewById(R.id.word7)
    TextView word7;
    @ViewById(R.id.word8)
    TextView word8;
    @ViewById(R.id.word9)
    TextView word9;
    @ViewById(R.id.word10)
    TextView word10;
    @ViewById(R.id.word11)
    TextView word11;
    @ViewById(R.id.word12)
    TextView word12;

    private List<TextView> allTextViews;

    private AddingNewWordSetPresenter presenter;

    @AfterViews
    public void init() {
        allTextViews = asList(word1, word2, word3, word4, word5, word6, word7, word8, word9, word10, word11, word12);
        AddingNewWordSetInteractor interactor = new AddingNewWordSetInteractor(backendServerFactory.get());

        presenter = new AddingNewWordSetPresenter(this, interactor);
    }

    @Click(R.id.buttonSubmit)
    @Background
    public void onButtonSubmitClick() {
        presenter.submit(asList(
                word1.getText().toString(),
                word2.getText().toString(),
                word3.getText().toString(),
                word4.getText().toString(),
                word5.getText().toString(),
                word6.getText().toString(),
                word7.getText().toString(),
                word8.getText().toString(),
                word9.getText().toString(),
                word10.getText().toString(),
                word11.getText().toString(),
                word12.getText().toString()
        ));
    }

    @Override
    @UiThread
    public void markSentencesWereNotFound(int wordIndex) {
        TextView textView = allTextViews.get(wordIndex);
        textView.setError("No sentences");
    }

    @Override
    @UiThread
    public void markSentencesWereFound(int wordIndex) {
        TextView textView = allTextViews.get(wordIndex);
        textView.setError(null);
    }

    @Override
    @UiThread
    public void submitSuccessfully(WordSet wordSet) {
        startWordSetActivity(wordSet);
    }

    @Override
    @UiThread
    public void markWordIsEmpty(int wordIndex) {
        TextView textView = allTextViews.get(wordIndex);
        textView.setError("Empty!");
    }

    private void startWordSetActivity(WordSet wordSet) {
        Intent intent = new Intent(getActivity(), PracticeWordSetActivity_.class);
        intent.putExtra(PracticeWordSetActivity.WORD_SET_MAPPING, wordSet);
        intent.putExtra(PracticeWordSetActivity.REPETITION_MODE_MAPPING, false);
        startActivity(intent);
    }
}