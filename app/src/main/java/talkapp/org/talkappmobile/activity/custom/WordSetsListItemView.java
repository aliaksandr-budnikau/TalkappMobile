package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.BaseActivity;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.presenter.WordSetsListItemViewPresenter;
import talkapp.org.talkappmobile.view.WordSetsListItemViewView;


@EViewGroup(R.layout.row_word_sets_list)
public class WordSetsListItemView extends RelativeLayout implements WordSetsListItemViewView {

    @ViewById(R.id.wordSetRow)
    TextView wordSetRow;

    @ViewById(R.id.availableInHours)
    TextView availableInHours;

    @ViewById(R.id.wordSetProgress)
    ProgressBar wordSetProgress;

    @StringRes(R.string.word_sets_list_fragment_opening_warning_too_early_message)
    String openingWarningTooEarlyMessage;

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
        BaseActivity activity = (BaseActivity) getContext();
        presenter = activity.getPresenterFactory().create(this);
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

    @Override
    @UiThread
    public void setAvailableInHours(int availableInHours) {
        this.availableInHours.setText(String.format(openingWarningTooEarlyMessage, availableInHours));
    }

    @Override
    @UiThread
    public void disableWordSet() {
        wordSetRow.setTextColor(getResources().getColor(R.color.wordSetDisabledInListText));
    }

    @Override
    @UiThread
    public void hideAvailableInHoursTextView() {
        this.availableInHours.setVisibility(GONE);
    }

    @Override
    @UiThread
    public void showAvailableInHoursTextView() {
        this.availableInHours.setVisibility(VISIBLE);
    }

    @Override
    @UiThread
    public void enableWordSet() {
        wordSetRow.setTextColor(getResources().getColor(R.color.wordSetInListText));
    }
}