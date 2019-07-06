package org.talkappmobile.activity.custom.listener;

public interface OnRightAnswerTextViewListener {
    void onNewValue(String newValue);

    void onAnswerHasBeenSeen();

    void onOpenGoogleTranslate(String input, String langFrom, String langTo);

    void onActivityNotFoundException();
}