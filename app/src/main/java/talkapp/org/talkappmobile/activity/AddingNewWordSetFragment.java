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
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.activity.custom.WordSetVocabularyView;
import talkapp.org.talkappmobile.controller.AddingNewWordSetFragmentController;
import talkapp.org.talkappmobile.events.AddNewWordSetButtonSubmitClickedEM;
import talkapp.org.talkappmobile.events.AddingNewWordSetFragmentGotReadyEM;
import talkapp.org.talkappmobile.events.NewWordIsDuplicateEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftLoadedEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftWasChangedEM;
import talkapp.org.talkappmobile.events.NewWordSuccessfullySubmittedEM;
import talkapp.org.talkappmobile.events.SomeWordIsEmptyEM;
import talkapp.org.talkappmobile.events.WordSetVocabularyLoadedEM;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.BackendServerFactory;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

import static org.androidannotations.annotations.IgnoreWhen.State.VIEW_DESTROYED;

@EFragment(value = R.layout.adding_new_word_set_layout)
public class AddingNewWordSetFragment extends Fragment implements View.OnFocusChangeListener {
    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;
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

        for (int i = 0; i < 12; i++) {
            /*View child = wordSetVocabularyView.getChildAt(i);
            child.setOnFocusChangeListener(this);*/
        }
        controller = new AddingNewWordSetFragmentController(eventBus, backendServerFactory.get(), serviceFactory);
        eventBus.post(new AddingNewWordSetFragmentGotReadyEM());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(AddingNewWordSetFragmentGotReadyEM event) {
        controller.handle(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordSetDraftLoadedEM event) {
        List<String> words = event.getNewWordSetDraft().getWords();
        WordTranslation[] wordTranslations = new WordTranslation[words.size()];
        for (int i = 0; i < words.size(); i++) {
            String[] split = words.get(i).split("\\|");
            if (split.length != 2) {
                WordTranslation translation = new WordTranslation();
                translation.setWord(split[0]);
                wordTranslations[i] = translation;
            } else {
                WordTranslation translation = new WordTranslation();
                translation.setWord(split[0]);
                translation.setTranslation(split[1]);
                wordTranslations[i] = translation;
            }
        }
        eventBus.post(new WordSetVocabularyLoadedEM(wordTranslations));
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
        LinkedList<WordTranslation> emptyTranslations = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            WordTranslation translation = new WordTranslation();
            translation.setWord("Word # " + (i + 1));
            emptyTranslations.add(translation);
        }
        WordTranslation[] translations = emptyTranslations.toArray(new WordTranslation[0]);
        eventBus.post(new WordSetVocabularyLoadedEM(translations));
        eventBus.post(new NewWordSetDraftWasChangedEM(getWords()));

        startWordSetActivity(event.getWordSet());
    }

    @Click(R.id.buttonSubmit)
    public void onButtonSubmitClick() {
        try {
            waitingForProgressBarManager.showProgressBar();
            eventBus.post(new AddNewWordSetButtonSubmitClickedEM(getWords()));
        } finally {
            waitingForProgressBarManager.hideProgressBar();
        }
    }

    private List<String> getWords() {
        List<WordTranslation> vocabulary = wordSetVocabularyView.getVocabulary();
        LinkedList<String> words = new LinkedList<>();
        for (WordTranslation wordTranslation : vocabulary) {
            words.add(wordTranslation.getWord() + "|" + wordTranslation.getTranslation());
        }
        return words;
    }

    private void startWordSetActivity(WordSet wordSet) {
        Intent intent = new Intent(getActivity(), PracticeWordSetActivity_.class);
        intent.putExtra(PracticeWordSetActivity.WORD_SET_MAPPING, wordSet);
        intent.putExtra(PracticeWordSetActivity.REPETITION_MODE_MAPPING, false);
        startActivity(intent);
    }

    @Override
    @IgnoreWhen(VIEW_DESTROYED)
    public void onFocusChange(View view, boolean b) {
        eventBus.post(new NewWordSetDraftWasChangedEM(getWords()));
    }

    @Override
    public void onPause() {
        eventBus.post(new NewWordSetDraftWasChangedEM(getWords()));
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        eventBus.post(new NewWordSetDraftWasChangedEM(getWords()));
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
}