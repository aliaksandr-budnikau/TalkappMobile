package talkapp.org.talkappmobile.activity.custom.view;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;

public interface OriginalTextTextViewView {
    void setOriginalText(String originalText);

    void onChangeSentence();

    void openDialog(String[] options, boolean mutable);

    void openDialogForPickingNewSentence(String[] options, List<Sentence> sentences, boolean[] selectedOnes);
}