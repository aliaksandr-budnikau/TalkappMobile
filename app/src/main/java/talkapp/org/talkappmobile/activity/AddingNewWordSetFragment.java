package talkapp.org.talkappmobile.activity;

import android.app.Fragment;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.activity.custom.WordSetVocabularyView;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.impl.SpeakerBean;
import talkapp.org.talkappmobile.controller.AddingNewWordSetFragmentController;
import talkapp.org.talkappmobile.events.AddNewWordSetButtonSubmitClickedEM;
import talkapp.org.talkappmobile.events.AddingNewWordSetFragmentGotReadyEM;
import talkapp.org.talkappmobile.events.NewWordIsDuplicateEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftLoadedEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftWasChangedEM;
import talkapp.org.talkappmobile.events.NewWordSuccessfullySubmittedEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputWasUpdatedEM;
import talkapp.org.talkappmobile.events.SomeWordIsEmptyEM;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.BackendServerFactory;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

@EFragment(value = R.layout.adding_new_word_set_layout)
public class AddingNewWordSetFragment extends Fragment implements WordSetVocabularyView.OnItemViewInteractionListener {
    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;
    @Bean(SpeakerBean.class)
    Speaker speaker;
    @Bean(ServiceFactoryBean.class)
    ServiceFactory serviceFactory;
    @Bean(BackendServerFactoryBean.class)
    BackendServerFactory backendServerFactory;
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
    @StringRes(R.string.adding_new_word_set_fragment_warning_duplicate_field)
    String warningDuplicateField;
    private WaitingForProgressBarManager waitingForProgressBarManager;
    private AddingNewWordSetFragmentController controller;

    @AfterViews
    public void init() {
        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(pleaseWaitProgressBar, mainForm);
        controller = new AddingNewWordSetFragmentController(eventBus, backendServerFactory.get(), serviceFactory);
        eventBus.post(new AddingNewWordSetFragmentGotReadyEM());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(AddingNewWordSetFragmentGotReadyEM event) {
        controller.handle(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordSetDraftLoadedEM event) {
        WordTranslation[] words = event.getNewWordSetDraft().getWordTranslations().toArray(new WordTranslation[0]);
        wordSetVocabularyView.setAdapter(new WordSetVocabularyView.VocabularyAdapter(words));
        wordSetVocabularyView.setOnItemViewInteractionListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SomeWordIsEmptyEM event) {
        Toast.makeText(getActivity(), warningEmptyFields, Toast.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordIsDuplicateEM event) {
        Toast.makeText(getActivity(), warningDuplicateField, Toast.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordSuccessfullySubmittedEM event) {
        wordSetVocabularyView.resetVocabulary();
        List<WordTranslation> vocabulary = wordSetVocabularyView.getVocabulary();
        wordSetVocabularyView.setAdapter(new WordSetVocabularyView.VocabularyAdapter(vocabulary.toArray(new WordTranslation[0])));
        eventBus.post(new NewWordSetDraftWasChangedEM(vocabulary));

        startWordSetActivity(event.getWordSet());
    }

    @Click(R.id.buttonSubmit)
    public void onButtonSubmitClick() {
        try {
            waitingForProgressBarManager.showProgressBar();
            eventBus.post(new AddNewWordSetButtonSubmitClickedEM(wordSetVocabularyView.getVocabulary()));
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
            eventBus.post(new NewWordSetDraftWasChangedEM(vocabulary));
        }
        super.onDestroyView();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(NewWordSetDraftWasChangedEM event) {
        controller.handle(event);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(AddNewWordSetButtonSubmitClickedEM event) {
        controller.handle(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PhraseTranslationInputWasUpdatedEM event) {
        eventBus.post(new NewWordSetDraftWasChangedEM(wordSetVocabularyView.getVocabulary()));
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
        wordSetVocabularyView.openAlertDialog(item, position);
    }

    @Override
    public void onSubmitChangeItemButtonClicked(String phrase, String translation, int position) {

    }
}