package talkapp.org.talkappmobile.activity.custom.view;

public interface OriginalTextTextViewView {
    void setOriginalText(String originalText);

    void onChangeSentence();

    void openDialog(String[] options, boolean mutable);
}