package talkapp.org.talkappmobile.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.adapter.AdaptersFactory;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenter;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

@EFragment(value = R.layout.word_translations_layout)
public class PracticeWordSetVocabularyFragment extends Fragment implements PracticeWordSetVocabularyView {
    public static final String WORD_SET_MAPPING = "wordSet";
    @Inject
    AdaptersFactory adaptersFactory;
    @Inject
    PracticeWordSetVocabularyInteractor interactor;
    @Inject
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;

    @ViewById(R.id.wordTranslationsListView)
    ListView wordSetsListView;
    @ViewById(R.id.please_wait_progress_bar)
    View progressBarView;

    private WaitingForProgressBarManager waitingForProgressBarManager;

    private ArrayAdapter<WordTranslation> adapter;
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
        DIContextUtils.get().inject(this);

        adapter = adaptersFactory.createWordTranslationListAdapter(this.getActivity());
        wordSetsListView.setAdapter(adapter);

        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(progressBarView, wordSetsListView);

        WordSet wordSet = (WordSet) getArguments().get(WORD_SET_MAPPING);
        presenter = new PracticeWordSetVocabularyPresenter(wordSet, this, interactor);

        initPresenter();
    }

    @Background
    public void initPresenter() {
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