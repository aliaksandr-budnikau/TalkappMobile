package talkapp.org.talkappmobile.view;

import android.text.Spanned;

public interface RightAnswerTextViewView {
    void onNewValue(String newValue);

    void answerHasBeenSeen();

    void turnAnswerToLink(Spanned value);

    void openGoogleTranslate(String input, String langFrom, String langTo);

    void onActivityNotFoundException();
}