package talkapp.org.talkappmobile.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.activity.custom.WordSetVocabularyItemAlertDialog;
import talkapp.org.talkappmobile.activity.custom.WordSetVocabularyView;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenter;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.impl.SpeakerBean;
import talkapp.org.talkappmobile.events.NewWordSentencesWereFoundEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereNotFoundEM;
import talkapp.org.talkappmobile.events.NewWordTranslationWasNotFoundEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputPopupOkClickedEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputWasUpdatedEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputWasValidatedSuccessfullyEM;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import static org.androidannotations.annotations.IgnoreWhen.State.VIEW_DESTROYED;

@EFragment(value = R.layout.word_translations_layout)
public class PracticeWordSetVocabularyFragment extends Fragment implements PracticeWordSetVocabularyView, WordSetVocabularyView.OnItemViewInteractionListener,
        WordSetVocabularyItemAlertDialog.OnDialogInteractionListener {
    public static final String WORD_SET_MAPPING = "wordSet";
    @Bean
    PresenterFactory presenterFactory;
    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;
    @EventBusGreenRobot
    EventBus eventBus;
    @Bean(SpeakerBean.class)
    Speaker speaker;
    @ViewById(R.id.wordSetVocabularyView)
    WordSetVocabularyView wordSetVocabularyView;
    @ViewById(R.id.please_wait_progress_bar)
    View progressBarView;

    @FragmentArg(WORD_SET_MAPPING)
    WordSet wordSet;
    @StringRes(R.string.adding_new_word_set_fragment_warning_empty_field)
    String warningEmptyField;
    @StringRes(R.string.adding_new_word_set_fragment_warning_translation_not_found)
    String warningTranslationNotFound;
    @StringRes(R.string.adding_new_word_set_fragment_warning_sentences_not_found)
    String warningSentencesNotFound;
    private WaitingForProgressBarManager waitingForProgressBarManager;
    private WordSetVocabularyItemAlertDialog itemAlertDialog;

    public static PracticeWordSetVocabularyFragment newInstance(WordSet wordSet) {
        PracticeWordSetVocabularyFragment fragment = new PracticeWordSetVocabularyFragment_();
        Bundle args = new Bundle();
        args.putSerializable(WORD_SET_MAPPING, wordSet);
        fragment.setArguments(args);
        return fragment;
    }

    @AfterViews
    public void init() {
        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(progressBarView, wordSetVocabularyView);
        wordSetVocabularyView.setOnItemViewInteractionListener(this);
        initPresenter();
    }

    @Background
    public void initPresenter() {
        PracticeWordSetVocabularyPresenter presenter = presenterFactory.create(this);
        presenter.initialise(wordSet);
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void setWordSetVocabularyList(final List<WordTranslation> wordTranslations) {
        wordSetVocabularyView.setAdapter(new WordSetVocabularyView.VocabularyAdapter(wordTranslations.toArray(new WordTranslation[0])));
    }

    @Override
    @UiThread
    public void onInitializeBeginning() {
        waitingForProgressBarManager.showProgressBar();
    }

    @Override
    @UiThread
    public void onInitializeEnd() {
        waitingForProgressBarManager.hideProgressBar();
    }

    @Override
    public void onLocalCacheIsEmptyException(LocalCacheIsEmptyException e) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
        throw e;
    }

    @Override
    public void onSayItemButtonClicked(WordTranslation item, int position) {
        try {
            speaker.speak(item.getWord());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void onEditItemButtonClicked(WordTranslation item, int position) {
        itemAlertDialog = new WordSetVocabularyItemAlertDialog(getContext());
        itemAlertDialog.setOnDialogInteractionListener(this);
        itemAlertDialog.open(item, position);
    }

    @Override
    public void onSubmitChangeItemButtonClicked(String phrase, String translation, int position) {
        if (StringUtils.isEmpty(translation)) {
            phrase = phrase.trim().toLowerCase();
        }
        phrase = phrase.trim();
        translation = translation.trim();

        if (StringUtils.isEmpty(phrase)) {
            itemAlertDialog.setPhraseBoxError(warningEmptyField);
            itemAlertDialog.setTranslationBoxError(null);
        }

        eventBus.post(new PhraseTranslationInputPopupOkClickedEM(position, phrase, translation));
    }

    @UiThread
    public void onMessageEvent(NewWordTranslationWasNotFoundEM event) {
        itemAlertDialog.setPhraseBoxError(null);
        itemAlertDialog.setTranslationBoxError(warningTranslationNotFound);
    }

    @UiThread
    public void onMessageEvent(PhraseTranslationInputWasValidatedSuccessfullyEM event) {
        itemAlertDialog.setPhraseBoxError(null);
        itemAlertDialog.setTranslationBoxError(null);
        WordTranslation translation = wordSetVocabularyView.getVocabulary().get(event.getAdapterPosition());
        translation.setWord(itemAlertDialog.getPhraseBoxText());
        translation.setTranslation(itemAlertDialog.getTranslationBoxText());
        itemAlertDialog.cancel();
        itemAlertDialog.dismiss();
        itemAlertDialog = null;
        eventBus.post(new PhraseTranslationInputWasUpdatedEM());
    }

    @UiThread
    public void onMessageEvent(NewWordSentencesWereNotFoundEM event) {
        itemAlertDialog.setPhraseBoxError(null);
        itemAlertDialog.setTranslationBoxError(warningSentencesNotFound);
    }

    @UiThread
    public void onMessageEvent(NewWordSentencesWereFoundEM event) {
        itemAlertDialog.setPhraseBoxError(null);
        itemAlertDialog.setTranslationBoxError(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PhraseTranslationInputWasUpdatedEM event) {
        wordSetVocabularyView.getAdapter().notifyDataSetChanged();
    }
}