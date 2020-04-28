package talkapp.org.talkappmobile.view;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public interface OriginalTextTextViewView {
    void setOriginalText(String originalText);

    void onChangeSentence(Word2Tokens word);

    void openDialog(Word2Tokens word, String[] options, boolean mutable);

    void openDialogForPickingNewSentence(Word2Tokens word, String[] options, List<Sentence> sentences, boolean[] selectedOnes);
}