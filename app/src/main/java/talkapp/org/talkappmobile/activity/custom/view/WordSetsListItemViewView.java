package talkapp.org.talkappmobile.activity.custom.view;

public interface WordSetsListItemViewView {
    void hideProgressBar();

    void showProgressBar();

    void setWordSetRowValue(String wordSetRowValue);

    void setProgressBarValue(int progressValue);

    void setAvailableInHours(int availableInHours);

    void disableWordSet();

    void hideAvailableInHoursTextView();

    void showAvailableInHoursTextView();

    void enableWordSet();
}