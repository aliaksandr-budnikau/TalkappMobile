package talkapp.org.talkappmobile.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
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
import talkapp.org.talkappmobile.activity.presenter.AddingNewWordSetPresenter;
import talkapp.org.talkappmobile.activity.view.AddingNewWordSetView;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.impl.SpeakerBean;
import talkapp.org.talkappmobile.events.NewWordSetDraftWasChangedEM;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

@EFragment(value = R.layout.adding_new_word_set_layout)
public class AddingNewWordSetFragment extends Fragment implements WordSetVocabularyView.OnItemViewInteractionListener, WordSetVocabularyItemAlertDialog.OnDialogInteractionListener, AddingNewWordSetView {
    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;
    @Bean(SpeakerBean.class)
    Speaker speaker;
    @Bean
    WordSetVocabularyItemAlertDialog editVocabularyItemAlertDialog;
    @Bean
    PresenterFactory presenterFactory;
    @ViewById(R.id.wordSetVocabularyView)
    WordSetVocabularyView wordSetVocabularyView;
    @ViewById(R.id.please_wait_progress_bar)
    View pleaseWaitProgressBar;
    @ViewById(R.id.mainForm)
    View mainForm;
    @EventBusGreenRobot
    EventBus eventBus;
    @StringRes(R.string.adding_new_word_set_fragment_warning_empty_fields)
    String warningEmptyFields;
    @StringRes(R.string.adding_new_word_set_fragment_warning_empty_field)
    String warningEmptyField;
    @StringRes(R.string.adding_new_word_set_fragment_warning_duplicate_field)
    String warningDuplicateField;
    @StringRes(R.string.adding_new_word_set_fragment_warning_translation_not_found)
    String warningTranslationNotFound;
    @StringRes(R.string.adding_new_word_set_fragment_warning_sentences_not_found)
    String warningSentencesNotFound;
    AddingNewWordSetPresenter presenter;
    private WaitingForProgressBarManager waitingForProgressBarManager;

    @AfterViews
    public void init() {
        editVocabularyItemAlertDialog.setOnDialogInteractionListener(this);
        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(pleaseWaitProgressBar, mainForm);
        initPresenter();
    }

    @Background
    public void initPresenter() {
        presenter = presenterFactory.create(this);
        presenter.initialize();
    }

    @Click(R.id.buttonSubmit)
    public void onButtonSubmitClick() {
        try {
            waitingForProgressBarManager.showProgressBar();
            presenter.submitNewWordSet(wordSetVocabularyView.getVocabulary());
        } finally {
            waitingForProgressBarManager.hideProgressBar();
        }
    }

    private void startWordSetActivity(WordSet wordSet) {
        Intent intent = new Intent(getActivity(), PracticeWordSetActivity_.class);
        intent.putExtra(PracticeWordSetActivity.WORD_SET_MAPPING, wordSet);
        intent.putExtra(PracticeWordSetActivity.REPETITION_MODE_MAPPING, false);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        eventBus.post(new NewWordSetDraftWasChangedEM(wordSetVocabularyView.getVocabulary()));
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        List<WordTranslation> vocabulary = wordSetVocabularyView.getVocabulary();
        if (vocabulary != null) {
            presenter.saveChangedDraft(vocabulary);
        }
        super.onDestroyView();
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

        List<WordTranslation> vocabulary = wordSetVocabularyView.getVocabulary();
        if (hasDuplicates(vocabulary, newPhrase) && !newPhrase.equals(origPhrase)) {
            editVocabularyItemAlertDialog.setPhraseBoxError(warningDuplicateField);
            editVocabularyItemAlertDialog.setTranslationBoxError(null);
            return;
        }
        savePhraseTranslation(newPhrase, newTranslation);
    }

    @Background
    public void savePhraseTranslation(String newPhrase, String newTranslation) {
        presenter.savePhraseTranslationInputOnPopup(newPhrase, newTranslation);
    }

    private boolean hasDuplicates(List<WordTranslation> words, String phrase) {
        for (WordTranslation word : words) {
            String wordAsString = word.getWord() == null ? "" : word.getWord();
            if (wordAsString.toLowerCase().equals(phrase.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @UiThread
    public void onNewWordSetDraftLoaded(WordTranslation[] words) {
        wordSetVocabularyView.setAdapter(new WordSetVocabularyView.VocabularyAdapter(words));
        wordSetVocabularyView.setOnItemViewInteractionListener(this);
    }

    @Override
    @UiThread
    public void onNewWordSuccessfullySubmitted(WordSet wordSet) {
        wordSetVocabularyView.resetVocabulary();
        List<WordTranslation> vocabulary = wordSetVocabularyView.getVocabulary();
        wordSetVocabularyView.setAdapter(new WordSetVocabularyView.VocabularyAdapter(vocabulary.toArray(new WordTranslation[0])));
        eventBus.post(new NewWordSetDraftWasChangedEM(vocabulary));

        Activity activity = this.getActivity();
        if (activity != null) {
            activity.recreate();
        }
        startWordSetActivity(wordSet);
    }

    @Override
    @UiThread
    public void onSomeWordIsEmpty() {
        Toast.makeText(getActivity(), warningEmptyFields, Toast.LENGTH_LONG).show();
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
        wordSetVocabularyView.submitItemChange(editVocabularyItemAlertDialog.getPhraseBoxText(), editVocabularyItemAlertDialog.getTranslationBoxText());
        editVocabularyItemAlertDialog.cancel();
        editVocabularyItemAlertDialog.dismiss();
        eventBus.post(new NewWordSetDraftWasChangedEM(wordSetVocabularyView.getVocabulary()));
    }
}