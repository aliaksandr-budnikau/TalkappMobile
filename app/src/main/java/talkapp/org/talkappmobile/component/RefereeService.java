package talkapp.org.talkappmobile.component;

import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.UncheckedAnswer;

public interface RefereeService {
    AnswerCheckingResult checkAnswer(UncheckedAnswer answer);
}