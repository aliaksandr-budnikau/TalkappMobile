package talkapp.org.talkappmobile.presenter;

import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.view.AddingNewWordSetView;
import talkapp.org.talkappmobile.view.ExceptionHandlerView;
import talkapp.org.talkappmobile.view.MainActivityDefaultFragmentView;
import talkapp.org.talkappmobile.view.MainActivityView;
import talkapp.org.talkappmobile.view.OriginalTextTextViewView;
import talkapp.org.talkappmobile.view.PracticeWordSetView;
import talkapp.org.talkappmobile.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.view.PronounceRightAnswerButtonView;
import talkapp.org.talkappmobile.view.RightAnswerTextViewView;
import talkapp.org.talkappmobile.view.StatisticActivityView;
import talkapp.org.talkappmobile.view.TopicsFragmentView;
import talkapp.org.talkappmobile.view.WordSetQRImporterView;
import talkapp.org.talkappmobile.view.WordSetsListItemViewView;
import talkapp.org.talkappmobile.view.WordSetsListView;

public interface PresenterFactory {
    IPracticeWordSetPresenter create(PracticeWordSetView view, boolean repetitionMode);

    PracticeWordSetVocabularyPresenter create(PracticeWordSetVocabularyView view);

    MainActivityPresenter create(MainActivityView view, String versionName);

    StatisticActivityPresenter create(StatisticActivityView view);

    AddingNewWordSetPresenter create(AddingNewWordSetView view);

    WordSetsListPresenter create(WordSetsListView view, boolean repetitionMode, RepetitionClass repetitionClass, Topic topic);

    TopicsFragmentPresenter create(TopicsFragmentView view);

    RightAnswerTextViewPresenter create(RightAnswerTextViewView view);

    MainActivityDefaultFragmentPresenter create(MainActivityDefaultFragmentView view, String wordSetsRepetitionTitle, String wordSetsRepetitionDescription, String wordSetsLearningTitle, String wordSetsLearningDescription, String wordSetsAddNewTitle, String wordSetsAddNewDescription, String wordSetsExtraRepetitionTitle, String wordSetsExtraRepetitionDescription);

    ExceptionHandlerPresenter create(ExceptionHandlerView exceptionHandlerView);

    WordSetQRImporterBeanPresenter create(WordSetQRImporterView view);

    WordSetsListItemViewPresenter create(WordSetsListItemViewView view);

    PronounceRightAnswerButtonPresenter create(PronounceRightAnswerButtonView view);

    OriginalTextTextViewPresenter create(OriginalTextTextViewView view);
}
