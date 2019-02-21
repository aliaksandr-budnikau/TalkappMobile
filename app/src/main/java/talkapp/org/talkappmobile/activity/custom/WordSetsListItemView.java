package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.interactor.WordSetsListItemViewInteractor;
import talkapp.org.talkappmobile.activity.custom.presenter.WordSetsListItemViewPresenter;
import talkapp.org.talkappmobile.activity.custom.view.WordSetsListItemViewView;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.component.impl.WordSetExperienceUtilsImpl;
import talkapp.org.talkappmobile.model.WordSet;

@EViewGroup(R.layout.row_word_sets_list)
public class WordSetsListItemView extends RelativeLayout implements WordSetsListItemViewView {

    @Bean(WordSetExperienceUtilsImpl.class)
    WordSetExperienceUtils experienceUtils;

    @ViewById(R.id.wordSetRow)
    TextView wordSetRow;

    @ViewById(R.id.wordSetProgress)
    ProgressBar wordSetProgress;

    private WordSetsListItemViewPresenter presenter;

    public WordSetsListItemView(Context context) {
        super(context);
    }

    public WordSetsListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WordSetsListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    public void init() {
        WordSetsListItemViewInteractor interactor = new WordSetsListItemViewInteractor(experienceUtils);
        presenter = new WordSetsListItemViewPresenter(interactor, this);
    }

    public void setModel(WordSet wordSet) {
        presenter.setModel(wordSet);
    }

    public void refreshModel() {
        presenter.refreshModel();
    }

    public void hideProgress() {
        presenter.hideProgress();
    }

    public void showProgress() {
        presenter.showProgress();
    }

    @Override
    public void hideProgressBar() {
        wordSetProgress.setVisibility(INVISIBLE);
    }

    @Override
    public void showProgressBar() {
        wordSetProgress.setVisibility(VISIBLE);
    }

    @Override
    public void setWordSetRowValue(String wordSetRowValue) {
        wordSetRow.setText(wordSetRowValue);
    }

    @Override
    public void setProgressBarValue(int progressValue) {
        wordSetProgress.setProgress(progressValue);
    }
}