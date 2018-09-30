package talkapp.org.talkappmobile.activity.presenter;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.Sentence;

public class PracticeWordSetViewHideNewWordOnlyStrategy extends PracticeWordSetViewStrategy {

    @Inject
    TextUtils textUtils;

    public PracticeWordSetViewHideNewWordOnlyStrategy(PracticeWordSetView view) {
        super(view);
        DIContext.get().inject(this);
    }

    @Override
    public void onTrainingFinished() {
    }

    @Override
    protected String hideRightAnswer(Sentence sentence) {
        return textUtils.hideText(sentence.getText());
    }
}