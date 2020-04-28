package talkapp.org.talkappmobile.view;

public interface PronounceRightAnswerButtonView {
    void onStartSpeaking();

    void onStopSpeaking();

    void onAnswerHasBeenRevealed();

    void onPronounceRightAnswer(String text);
}