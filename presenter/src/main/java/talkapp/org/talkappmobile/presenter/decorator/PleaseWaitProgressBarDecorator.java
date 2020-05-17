package talkapp.org.talkappmobile.presenter.decorator;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.presenter.IPracticeWordSetPresenter;
import talkapp.org.talkappmobile.view.PracticeWordSetView;

public class PleaseWaitProgressBarDecorator implements IPracticeWordSetPresenter {
    private final PracticeWordSetView view;
    private final IPracticeWordSetPresenter presenter;

    public PleaseWaitProgressBarDecorator(IPracticeWordSetPresenter presenter, PracticeWordSetView view) {
        this.presenter = presenter;
        this.view = view;
    }

    @Override
    public void nextButtonClick() {
        new PresenterWrapper(view) {
            @Override
            void doSuperMethod() {
                presenter.nextButtonClick();
            }
        }.execute();
    }

    @Override
    public void initialise(final WordSet wordSet) {
        new PresenterWrapper(view) {
            @Override
            void doSuperMethod() {
                presenter.initialise(wordSet);
            }
        }.execute();
    }

    @Override
    public void prepareOriginalTextClickEM() {
        new PresenterWrapper(view) {
            @Override
            void doSuperMethod() {
                presenter.prepareOriginalTextClickEM();
            }
        }.execute();
    }

    @Override
    public void disableButtonsDuringPronunciation() {
        new PresenterWrapper(view) {
            @Override
            void doSuperMethod() {
                presenter.disableButtonsDuringPronunciation();
            }
        }.execute();
    }

    @Override
    public void refreshCurrentWord() {
        new PresenterWrapper(view) {
            @Override
            void doSuperMethod() {
                presenter.refreshCurrentWord();
            }
        }.execute();
    }

    @Override
    public void enableButtonsAfterPronunciation() {
        new PresenterWrapper(view) {
            @Override
            void doSuperMethod() {
                presenter.enableButtonsAfterPronunciation();
            }
        }.execute();
    }

    @Override
    public void checkRightAnswerCommandRecognized() {
        new PresenterWrapper(view) {
            @Override
            void doSuperMethod() {
                presenter.checkRightAnswerCommandRecognized();
            }
        }.execute();
    }

    @Override
    public void checkAnswerButtonClick(final String answer) {
        new PresenterWrapper(view) {
            @Override
            void doSuperMethod() {
                presenter.checkAnswerButtonClick(answer);
            }
        }.execute();
    }

    @Override
    public void gotRecognitionResult(final List<String> suggestedWords) {
        new PresenterWrapper(view) {
            @Override
            void doSuperMethod() {
                presenter.gotRecognitionResult(suggestedWords);
            }
        }.execute();
    }


    @Override
    public void scoreSentence(final SentenceContentScore score, final Sentence sentence) {
        new PresenterWrapper(view) {
            @Override
            void doSuperMethod() {
                presenter.scoreSentence(score, sentence);
            }
        }.execute();
    }

    @Override
    public void findSentencesForChange(final Word2Tokens word) {
        new PresenterWrapper(view) {
            @Override
            void doSuperMethod() {
                presenter.findSentencesForChange(word);
            }
        }.execute();
    }

    @Override
    public void refreshSentence() {
        new PresenterWrapper(view) {
            @Override
            void doSuperMethod() {
                presenter.refreshSentence();
            }
        }.execute();
    }

    @Override
    public void changeSentence(final List<Sentence> sentences, final Word2Tokens word) {
        new PresenterWrapper(view) {
            @Override
            void doSuperMethod() {
                presenter.changeSentence(sentences, word);
            }
        }.execute();
    }

    @Override
    public void markAnswerHasBeenSeen() {
        new PresenterWrapper(view) {
            @Override
            void doSuperMethod() {
                presenter.markAnswerHasBeenSeen();
            }
        }.execute();
    }

    @Override
    public Sentence getCurrentSentence() {
        return presenter.getCurrentSentence();
    }

    @Override
    public void changeSentence() {
        presenter.changeSentence();
    }

    private abstract static class PresenterWrapper {
        private final PracticeWordSetView view;

        PresenterWrapper(PracticeWordSetView view) {
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