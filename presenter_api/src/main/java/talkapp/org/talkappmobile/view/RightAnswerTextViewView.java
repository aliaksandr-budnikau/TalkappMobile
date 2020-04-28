package talkapp.org.talkappmobile.view;

public interface RightAnswerTextViewView {
    void onNewValue(String newValue);

    void answerHasBeenSeen();

    void turnAnswerToLink(String value);

    void openGoogleTranslate(String input, String langFrom, String langTo);

    void onActivityNotFoundException();
}