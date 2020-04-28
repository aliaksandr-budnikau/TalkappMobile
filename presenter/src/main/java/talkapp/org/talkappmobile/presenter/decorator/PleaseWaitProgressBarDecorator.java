package talkapp.org.talkappmobile.presenter.decorator;

import android.net.Uri;

import java.util.List;

import talkapp.org.talkappmobile.view.PracticeWordSetView;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public class PleaseWaitProgressBarDecorator extends PracticeWordSetPresenterDecorator {
    private final PracticeWordSetView view;

    public PleaseWaitProgressBarDecorator(IPracticeWordSetPresenter presenter, PracticeWordSetView view) {
        super(presenter);
        this.view = view;
    }

    @Override
    public void nextButtonClick() {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.nextButtonClick();
            }
        }.execute();
    }

    @Override
    public void initialise(final WordSet wordSet) {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.initialise(wordSet);
            }
        }.execute();
    }

    @Override
    public void prepareOriginalTextClickEM() {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.prepareOriginalTextClickEM();
            }
        }.execute();
    }

    @Override
    public void playVoiceButtonClick() {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.playVoiceButtonClick();
            }
        }.execute();
    }

    @Override
    public void disableButtonsDuringPronunciation() {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.disableButtonsDuringPronunciation();
            }
        }.execute();
    }

    @Override
    public void refreshCurrentWord() {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.refreshCurrentWord();
            }
        }.execute();
    }

    @Override
    public void enableButtonsAfterPronunciation() {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.enableButtonsAfterPronunciation();
            }
        }.execute();
    }

    @Override
    public void checkRightAnswerCommandRecognized() {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.checkRightAnswerCommandRecognized();
            }
        }.execute();
    }

    @Override
    public void checkAnswerButtonClick(final String answer) {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.checkAnswerButtonClick(answer);
            }
        }.execute();
    }

    @Override
    public void gotRecognitionResult(final List<String> suggestedWords) {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.gotRecognitionResult(suggestedWords);
            }
        }.execute();
    }

    @Override
    public void voiceRecorded(final Uri data) {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.voiceRecorded(data);
            }
        }.execute();
    }

    @Override
    public void scoreSentence(final SentenceContentScore score, final Sentence sentence) {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.scoreSentence(score, sentence);
            }
        }.execute();
    }

    @Override
    public void findSentencesForChange(final Word2Tokens word) {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.findSentencesForChange(word);
            }
        }.execute();
    }

    @Override
    public void refreshSentence() {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.refreshSentence();
            }
        }.execute();
    }

    @Override
    public void changeSentence(final List<Sentence> sentences, final Word2Tokens word) {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.changeSentence(sentences, word);
            }
        }.execute();
    }

    @Override
    public void markAnswerHasBeenSeen() {
        new SuperClassWrapper(view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarDecorator.super.markAnswerHasBeenSeen();
            }
        }.execute();
    }

    private abstract static class SuperClassWrapper {
        private final PracticeWordSetView view;

        SuperClassWrapper(PracticeWordSetView view) {
            this.view = view;
        }

        void execute() {
            try {
                view.showPleaseWaitProgressBar();
                doSuperMethod();
            } finally {
                view.hidePleaseWaitProgressBar();
            }
        }

        abstract void doSuperMethod();
    }
}