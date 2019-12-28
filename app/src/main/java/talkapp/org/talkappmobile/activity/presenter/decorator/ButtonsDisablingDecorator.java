package talkapp.org.talkappmobile.activity.presenter.decorator;

import java.util.List;

import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public class ButtonsDisablingDecorator extends PracticeWordSetPresenterDecorator {
    private final PracticeWordSetView view;

    public ButtonsDisablingDecorator(IPracticeWordSetPresenter presenter, PracticeWordSetView view) {
        super(presenter);
        this.view = view;
    }

    @Override
    public void nextButtonClick() {
        try {
            view.setEnableRightAnswerTextView(false);
            view.setEnablePronounceRightAnswerButton(false);
            view.setEnableNextButton(false);
            super.nextButtonClick();
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
        super.disableButtonsDuringPronunciation();
    }

    @Override
    public void refreshCurrentWord() {
        try {
            view.setEnableRightAnswerTextView(false);
            view.setEnablePronounceRightAnswerButton(false);
            view.setEnableNextButton(false);
            super.refreshCurrentWord();
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
        super.enableButtonsAfterPronunciation();
    }

    @Override
    public void checkRightAnswerCommandRecognized(WordSet wordSet) {
        try {
            view.setEnableRightAnswerTextView(false);
            view.setEnablePronounceRightAnswerButton(false);
            view.setEnableCheckButton(false);
            super.checkRightAnswerCommandRecognized(wordSet);
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
            super.checkAnswerButtonClick(answer);
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
            super.scoreSentence(score, sentence);
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
            super.changeSentence(sentences, word);
        } finally {
            view.setEnableCheckButton(true);
            view.setEnableNextButton(true);
        }
    }

    @Override
    public void changeSentence(int wordSetId) {
        try {
            view.setEnableCheckButton(false);
            view.setEnableNextButton(false);
            super.changeSentence(wordSetId);
        } finally {
            view.setEnableCheckButton(true);
            view.setEnableNextButton(true);
        }
    }
}