package talkapp.org.talkappmobile.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

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

import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.activity.custom.WordSetVocabularyItemAlertDialog;
import talkapp.org.talkappmobile.activity.custom.WordSetVocabularyView;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.impl.SpeakerBean;
import talkapp.org.talkappmobile.events.UpdateCustomWordSetFinishedEM;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.presenter.AddingNewWordSetPresenter;
import talkapp.org.talkappmobile.presenter.PracticeWordSetVocabularyPresenter;
import talkapp.org.talkappmobile.presenter.PresenterFactory;
import talkapp.org.talkappmobile.view.AddingNewWordSetView;
import talkapp.org.talkappmobile.view.PracticeWordSetVocabularyView;

import static org.androidannotations.annotations.IgnoreWhen.State.VIEW_DESTROYED;

@EFragment(value = R.layout.word_translations_layout)
public class PracticeWordSetVocabularyFragment extends Fragment implements PracticeWordSetVocabularyView, WordSetVocabularyView.OnItemViewInteractionListener,
        WordSetVocabularyItemAlertDialog.OnDialogInteractionListener, AddingNewWordSetView {
    public static final String WORD_SET_MAPPING = "wordSet";

    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;
    @Bean
    WordSetVocabularyItemAlertDialog editVocabularyItemAlertDialog;
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
    @StringRes(R.string.adding_new_word_set_fragment_warning_duplicate_field)
    String warningDuplicateField;
    @StringRes(R.string.adding_new_word_set_fragment_warning_translation_not_found)
    String warningTranslationNotFound;
    @StringRes(R.string.adding_new_word_set_fragment_warning_sentences_not_found)
    String warningSentencesNotFound;
    @StringRes(R.string.practice_word_set_vocabulary_fragment_warning_impossible_change_custom_word_set)
    String impossibleToChangeNotCustomWordSet;
    @StringRes(R.string.practice_word_set_vocabulary_fragment_message_successful_update)
    String successfulUpdateMessage;

    private WaitingForProgressBarManager waitingForProgressBarManager;
    private PracticeWordSetVocabularyPresenter presenter;
    private AddingNewWordSetPresenter addingNewWordSetPresenter;

    public static PracticeWordSetVocabularyFragment newInstance(WordSet wordSet) {
        PracticeWordSetVocabularyFragment fragment = new PracticeWordSetVocabularyFragment_();
        Bundle args = new Bundle();
        args.putSerializable(WORD_SET_MAPPING, wordSet);
        fragment.setArguments(args);
        return fragment;
    }

    @AfterViews
    public void init() {
        editVocabularyItemAlertDialog.setOnDialogInteractionListener(this);
        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(progressBarView, wordSetVocabularyView);
        wordSetVocabularyView.setOnItemViewInteractionListener(this);
        initPresenter();
    }


    @Background
    public void initPresenter() {
        BaseActivity activity = (BaseActivity) getActivity();
        PresenterFactory presenterFactory = activity.getPresenterFactory();
        presenter = presenterFactory.create((PracticeWordSetVocabularyView) this);
        presenter.initialise(wordSet);
        addingNewWordSetPresenter = presenterFactory.create((AddingNewWordSetView) this);
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
    public void onLocalCacheIsEmptyException(RuntimeException e) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
        throw e;
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void onUpdateNotCustomWordSet() {
        Toast.makeText(this.getContext(), impossibleToChangeNotCustomWordSet, Toast.LENGTH_LONG).show();
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void onUpdateCustomWordSetFinished() {
        Toast.makeText(this.getContext(), successfulUpdateMessage, Toast.LENGTH_LONG).show();
        eventBus.post(new UpdateCustomWordSetFinishedEM());
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
        editVocabularyItemAlertDialog.setOnDialogInteractionListener(this);
        editVocabularyItemAlertDialog.open(item.getWord(), item.getTranslation(), getActivity());
    }

    @Override
    @UiThread
    public void onOkButtonClicked(String newPhrase, String newTranslation, String origPhrase, String origTranslation) {
        editVocabularyItemAlertDialog.setPhraseBoxError(null);
        editVocabularyItemAlertDialog.setTranslationBoxError(null);
        if (StringUtils.isEmpty(newTranslation)) {
            newPhrase = newPhrase.trim().toLowerCase();
        }
        newPhrase = newPhrase.trim();
        newTranslation = newTranslation.trim();

        if (StringUtils.isEmpty(newPhrase)) {
            editVocabularyItemAlertDialog.setPhraseBoxError(warningEmptyField);
            editVocabularyItemAlertDialog.setTranslationBoxError(null);
            return;
        }

        savePhraseTranslation(newPhrase, newTranslation);
    }

    @Background
    public void savePhraseTranslation(String newPhrase, String newTranslation) {
        addingNewWordSetPresenter.savePhraseTranslationInputOnPopup(newPhrase, newTranslation);
    }

    @Override
    public void onNewWordSetDraftLoaded(WordTranslation[] words) {

    }

    @Override
    public void onNewWordSuccessfullySubmitted(WordSet wordSet) {

    }

    @Override
    public void onSomeWordIsEmpty() {

    }

    @Override
    @UiThread
    public void onNewWordTranslationWasNotFound() {
        editVocabularyItemAlertDialog.setPhraseBoxError(null);
        editVocabularyItemAlertDialog.setTranslationBoxError(warningTranslationNotFound);
    }

    @Override
    @UiThread
    public void onPhraseTranslationInputWasValidatedSuccessfully(String newPhrase, String newTranslation) {
        editVocabularyItemAlertDialog.setPhraseBoxError(null);
        editVocabularyItemAlertDialog.setTranslationBoxError(null);
        int editedItemPosition = wordSetVocabularyView.getEditedItemPosition();
        WordTranslation editedItem = wordSetVocabularyView.getEditedItem();
        wordSetVocabularyView.submitItemChange(editVocabularyItemAlertDialog.getPhraseBoxText(), editVocabularyItemAlertDialog.getTranslationBoxText());
        editVocabularyItemAlertDialog.cancel();
        editVocabularyItemAlertDialog.dismiss();
        presenter.updateCustomWordSet(editedItemPosition, editedItem);
    }

    @Override
    @UiThread
    public void showPleaseWaitProgressBar() {
        waitingForProgressBarManager.showProgressBar();
    }

    @Override
    @UiThread
    public void hidePleaseWaitProgressBar() {
        waitingForProgressBarManager.hideProgressBar();
    }
}