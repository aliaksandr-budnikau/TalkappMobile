package talkapp.org.talkappmobile.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import talkapp.org.talkappmobile.events.WordSetVocabularyLoadedEM;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenter;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;

import static org.androidannotations.annotations.IgnoreWhen.State.VIEW_DESTROYED;

@EFragment(value = R.layout.word_translations_layout)
public class PracticeWordSetVocabularyFragment extends Fragment implements PracticeWordSetVocabularyView {
    public static final String WORD_SET_MAPPING = "wordSet";
    @Bean
    PresenterFactory presenterFactory;
    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;
    @EventBusGreenRobot
    EventBus eventBus;

    @ViewById(R.id.wordSetVocabularyView)
    RecyclerView wordSetVocabularyView;
    @ViewById(R.id.please_wait_progress_bar)
    View progressBarView;

    @FragmentArg(WORD_SET_MAPPING)
    WordSet wordSet;

    private WaitingForProgressBarManager waitingForProgressBarManager;

    public static PracticeWordSetVocabularyFragment newInstance(WordSet wordSet) {
        PracticeWordSetVocabularyFragment fragment = new PracticeWordSetVocabularyFragment_();
        Bundle args = new Bundle();
        args.putSerializable(WORD_SET_MAPPING, wordSet);
        fragment.setArguments(args);
        return fragment;
    }

    @AfterViews
    public void init() {
        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(progressBarView, wordSetVocabularyView);

        initPresenter();
    }

    @Background
    public void initPresenter() {
        PracticeWordSetVocabularyPresenter presenter = presenterFactory.create(wordSet, this);
        presenter.initialise();
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void setWordSetVocabularyList(final List<WordTranslation> wordTranslations) {
        WordTranslation[] translations = wordTranslations.toArray(new WordTranslation[0]);
        WordSetVocabularyLoadedEM event = new WordSetVocabularyLoadedEM(translations);
        eventBus.post(event);
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

    @Override
    public void onLocalCacheIsEmptyException(LocalCacheIsEmptyException e) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
        throw e;
    }
}