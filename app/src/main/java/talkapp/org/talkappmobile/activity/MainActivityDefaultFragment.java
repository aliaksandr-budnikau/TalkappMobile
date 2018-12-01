package talkapp.org.talkappmobile.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.interactor.MainActivityDefaultFragmentInteractor;
import talkapp.org.talkappmobile.activity.presenter.MainActivityDefaultFragmentPresenter;
import talkapp.org.talkappmobile.activity.view.MainActivityDefaultFragmentView;
import talkapp.org.talkappmobile.config.DIContextUtils;

import static java.lang.String.format;
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.REPETITION_MODE_MAPPING;

@EFragment(value = R.layout.main_activity_default_fragment_layout)
public class MainActivityDefaultFragment extends Fragment implements MainActivityDefaultFragmentView {
    @Inject
    MainActivityDefaultFragmentInteractor interactor;

    @ViewById(R.id.wordsForRepetitionTextView)
    TextView wordsForRepetitionTextView;

    private MainActivityDefaultFragmentPresenter presenter;

    @AfterViews
    public void init() {
        DIContextUtils.get().inject(this);

        presenter = new MainActivityDefaultFragmentPresenter(this, interactor);

        initPresenter();
    }

    @Background
    public void initPresenter() {
        presenter.init();
    }


    @Override
    @UiThread
    public void onWordsForRepetitionCounted(int counter) {
        wordsForRepetitionTextView.setText(format("Words for repetition %s", counter));
    }

    @Click(R.id.wordsForRepetitionTextView)
    public void onWordsForRepetitionTextViewClick() {
        Bundle args = new Bundle();
        args.putBoolean(REPETITION_MODE_MAPPING, true);
        WordSetsListFragment fragment = new WordSetsListFragment_();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }
}