package talkapp.org.talkappmobile.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.adapter.AdaptersFactory;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenter;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public class PracticeWordSetVocabularyFragment extends Fragment implements PracticeWordSetVocabularyView {
    public static final String WORD_SET_MAPPING = "wordSet";
    @Inject
    Executor executor;
    @Inject
    AdaptersFactory adaptersFactory;
    @Inject
    Handler uiEventHandler;
    private ArrayAdapter<WordTranslation> adapter;
    private PracticeWordSetVocabularyPresenter presenter;

    public static PracticeWordSetVocabularyFragment newInstance(WordSet wordSet) {
        PracticeWordSetVocabularyFragment fragment = new PracticeWordSetVocabularyFragment();
        Bundle args = new Bundle();
        args.putSerializable(WORD_SET_MAPPING, wordSet);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        DIContext.get().inject(this);
        View view = inflater.inflate(R.layout.word_translations_layout, container, false);

        adapter = adaptersFactory.createWordTranslationListAdapter(this.getActivity());

        ListView wordSetsListView = view.findViewById(R.id.wordTranslationsListView);
        wordSetsListView.setAdapter(adapter);

        WordSet wordSet = (WordSet) getArguments().get(WORD_SET_MAPPING);

        presenter = new PracticeWordSetVocabularyPresenter(wordSet, this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                presenter.onResume();
                return null;
            }
        }.executeOnExecutor(executor);
    }

    @Override
    public void setWordSetVocabularyList(final List<WordTranslation> wordTranslations) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                adapter.addAll(wordTranslations);
            }
        });
    }
}
