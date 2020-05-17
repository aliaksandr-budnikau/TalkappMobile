package talkapp.org.talkappmobile.presenter.decorator;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.presenter.IPracticeWordSetPresenter;
import talkapp.org.talkappmobile.view.PracticeWordSetView;

@RequiredArgsConstructor
public class ButtonsDisablingDecorator implements IPracticeWordSetPresenter {
    @Delegate(excludes = ExcludedMethods.class)
    private final IPracticeWordSetPresenter presenter;
    private final PracticeWordSetView view;

    @Override
    public void nextButtonClick() {
        try {
            view.setEnableRightAnswerTextView(false);
            view.setEnablePronounceRightAnswerButton(false);
            view.setEnableNextButton(false);
            presenter.nextButtonClick();
        } finally {
            view.setEnableRightAnswerTextView(true);
            view.setEnablePronounceRightAnswerButton(true);
            view.setEnableNextButton(true);
        }
    }

    @Override
    public void disableButtonsDuringPronunciation() {
        view.setEnablePronounceRightAnswerButton(false);
        view.setEnableVoiceRecButton(false);
        view.setEnableCheckButton(false);
        view.setEnableNextButton(false);
        presenter.disableButtonsDuringPronunciation();
    }

    @Override
    public void refreshCurrentWord() {
        try {
            view.setEnableRightAnswerTextView(false);
            view.setEnablePronounceRightAnswerButton(false);
            view.setEnableNextButton(false);
            presenter.refreshCurrentWord();
        } finally {
            view.setEnableRightAnswerTextView(true);
            view.setEnablePronounceRightAnswerButton(true);
            view.setEnableNextButton(true);
        }
    }

    @Override
    public void enableButtonsAfterPronunciation() {
        view.setEnablePronounceRightAnswerButton(true);
        view.setEnableVoiceRecButton(true);
        view.setEnableCheckButton(true);
        view.setEnableNextButton(true);
        presenter.enableButtonsAfterPronunciation();
    }

    @Override
    public void checkRightAnswerCommandRecognized() {
        try {
            view.setEnableRightAnswerTextView(false);
            view.setEnablePronounceRightAnswerButton(false);
            view.setEnableCheckButton(false);
            presenter.checkRightAnswerCommandRecognized();
        } finally {
            view.setEnableRightAnswerTextView(true);
            view.setEnablePronounceRightAnswerButton(true);
            view.setEnableCheckButton(true);
        }
    }

    @Override
    public void checkAnswerButtonClick(String answer) {
        try {
            view.setEnableRightAnswerTextView(false);
            view.setEnablePronounceRightAnswerButton(false);
            view.setEnableCheckButton(false);
            presenter.checkAnswerButtonClick(answer);
        } finally {
            view.setEnableRightAnswerTextView(true);
            view.setEnablePronounceRightAnswerButton(true);
            view.setEnableCheckButton(true);
        }
    }

    @Override
    public void scoreSentence(SentenceContentScore score, Sentence sentence) {
        try {
            view.setEnableCheckButton(false);
            view.setEnableNextButton(false);
            presenter.scoreSentence(score, sentence);
        } finally {
            view.setEnableCheckButton(true);
            view.setEnableNextButton(true);
        }
    }

    @Override
    public void changeSentence(List<Sentence> sentences, Word2Tokens word) {
        try {
            view.setEnableCheckButton(false);
            view.setEnableNextButton(false);
            presenter.changeSentence(sentences, word);
        } finally {
            view.setEnableCheckButton(true);
            view.setEnableNextButton(true);
        }
    }

    @Override
    public void changeSentence() {
        try {
            view.setEnableCheckButton(false);
            view.setEnableNextButton(false);
            presenter.changeSentence();
        } finally {
            view.setEnableCheckButton(true);
            view.setEnableNextButton(true);
        }
    }

    private interface ExcludedMethods {
        void changeSentence();

        void changeSentence(List<Sentence> sentences, Word2Tokens word);

        void scoreSentence(SentenceContentScore score, Sentence sentence);

        void checkAnswerButtonClick(String answer);

        void checkRightAnswerCommandRecognized();

        void enableButtonsAfterPronunciation();

        void refreshCurrentWord();

        void disableButtonsDuringPronunciation();

        void nextButtonClick();
    }
}