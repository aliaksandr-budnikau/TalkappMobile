package talkapp.org.talkappmobile.activity.custom.presenter;

import android.text.Html;
import android.text.Spanned;

import talkapp.org.talkappmobile.activity.custom.interactor.RightAnswerTextViewInteractor;
import talkapp.org.talkappmobile.activity.custom.listener.OnRightAnswerTextViewListener;
import talkapp.org.talkappmobile.activity.custom.view.RightAnswerTextViewView;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public class RightAnswerTextViewPresenter implements OnRightAnswerTextViewListener {
    private final RightAnswerTextViewView view;
    private final RightAnswerTextViewInteractor interactor;
    private Sentence sentence;
    private Word2Tokens word;
    private boolean locked;
    private boolean hideAllMode;

    public RightAnswerTextViewPresenter(RightAnswerTextViewInteractor interactor, RightAnswerTextViewView view) {
        this.interactor = interactor;
        this.view = view;
    }

    public void setModel(Sentence sentence, Word2Tokens word) {
        this.sentence = sentence;
        this.word = word;
    }

    public void mask() {
        if (hideAllMode) {
            interactor.maskEntirely(sentence, locked, this);
        } else {
            interactor.maskOnlyWord(sentence, word, locked, this);
        }
    }

    public void rightAnswerTouched() {
        interactor.unmask(sentence, locked, this);
        interactor.openGoogleTranslate(sentence, locked, this);
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }

    public void enableHideAllMode() {
        hideAllMode = true;
    }

    @Override
    public void onNewValue(String newValue) {
        view.onNewValue(newValue);
    }

    @Override
    public void onAnswerHasBeenSeen() {
        view.answerHasBeenSeen();
    }

    @Override
    public void onOpenGoogleTranslate(String input, String langFrom, String langTo) {
        view.openGoogleTranslate(input, langFrom, langTo);
    }

    @Override
    public void onActivityNotFoundException() {
        view.onActivityNotFoundException();
    }

    public void turnAnswerToLink() {
        String value = sentence.getText();
        Spanned link = Html.fromHtml("<a href=\"https://translate.google.com/#view=home&op=translate&sl=en&tl=ru&text=" + value + "\">" + value + "</a>");
        view.turnAnswerToLink(link);
    }
}