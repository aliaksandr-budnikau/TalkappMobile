package talkapp.org.talkappmobile.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.PracticeWordSetVocabularyListAdapter;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenter;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.backend.BackendServerFactory;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.impl.SpeakerBean;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

@EFragment(value = R.layout.word_translations_layout)
public class PracticeWordSetVocabularyFragment extends Fragment implements PracticeWordSetVocabularyView {
    public static final String WORD_SET_MAPPING = "wordSet";
    @Bean
    PracticeWordSetVocabularyListAdapter adapter;
    @Bean(BackendServerFactoryBean.class)
    BackendServerFactory backendServerFactory;
    @Bean(SpeakerBean.class)
    Speaker speaker;
    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;

    @ViewById(R.id.wordTranslationsListView)
    ListView wordSetsListView;
    @ViewById(R.id.please_wait_progress_bar)
    View progressBarView;

    @FragmentArg(WORD_SET_MAPPING)
    WordSet wordSet;

    private WaitingForProgressBarManager waitingForProgressBarManager;
    private PracticeWordSetVocabularyPresenter presenter;

    public static PracticeWordSetVocabularyFragment newInstance(WordSet wordSet) {
        PracticeWordSetVocabularyFragment fragment = new PracticeWordSetVocabularyFragment_();
        Bundle args = new Bundle();
        args.putSerializable(WORD_SET_MAPPING, wordSet);
        fragment.setArguments(args);
        return fragment;
    }

    @AfterViews
    public void init() {
        wordSetsListView.setAdapter(adapter);

        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(progressBarView, wordSetsListView);

        PracticeWordSetVocabularyInteractor interactor = new PracticeWordSetVocabularyInteractor(backendServerFactory.get(), speaker);
        initPresenter(interactor);
    }

    @Background
    public void initPresenter(PracticeWordSetVocabularyInteractor interactor) {
        presenter = new PracticeWordSetVocabularyPresenter(wordSet, this, interactor);
        presenter.initialise();
    }

    @Override
    @UiThread
    public void setWordSetVocabularyList(final List<WordTranslation> wordTranslations) {
        adapter.addAll(wordTranslations);
    }

    @ItemClick(R.id.wordTranslationsListView)
    public void onItemClick(int position) {
        WordTranslation translation = adapter.getItem(position);
        presenter.onPronounceWordButtonClick(translation);
    }

    @Override
    @UiThread
    public void onInitializeBeginning() {
        waitingForProgressBarManager.showProgressBar();
    }

    @Override
    @UiThread
    public void onInitializeEnd() {
        waitingForProgressBarManager.hideProgressBar();
    }
}