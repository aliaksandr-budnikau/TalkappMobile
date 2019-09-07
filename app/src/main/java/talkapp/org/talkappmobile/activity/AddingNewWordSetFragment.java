package talkapp.org.talkappmobile.activity;

import android.app.Fragment;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.activity.presenter.AddingNewWordSetPresenter;
import talkapp.org.talkappmobile.activity.view.AddingNewWordSetFragmentView;
import talkapp.org.talkappmobile.controller.AddingNewWordSetFragmentController;
import talkapp.org.talkappmobile.events.AddingNewWordSetFragmentGotReadyEM;
import talkapp.org.talkappmobile.events.NewWordIsDuplicateEM;
import talkapp.org.talkappmobile.events.NewWordIsEmptyEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereFoundEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereNotFoundEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftLoadedEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftWasChangedEM;
import talkapp.org.talkappmobile.events.NewWordSuccessfullySubmittedEM;
import talkapp.org.talkappmobile.events.NewWordTranslationWasNotFoundEM;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

import static java.util.Arrays.asList;
import static org.androidannotations.annotations.IgnoreWhen.State.VIEW_DESTROYED;

@EFragment(value = R.layout.adding_new_word_set_layout)
public class AddingNewWordSetFragment extends Fragment implements AddingNewWordSetFragmentView, View.OnFocusChangeListener {
    @Bean
    PresenterFactory presenterFactory;
    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;
    @Bean(ServiceFactoryBean.class)
    ServiceFactory serviceFactory;
    @ViewById(R.id.word1)
    TextView word1;
    @ViewById(R.id.word2)
    TextView word2;
    @ViewById(R.id.word3)
    TextView word3;
    @ViewById(R.id.word4)
    TextView word4;
    @ViewById(R.id.word5)
    TextView word5;
    @ViewById(R.id.word6)
    TextView word6;
    @ViewById(R.id.word7)
    TextView word7;
    @ViewById(R.id.word8)
    TextView word8;
    @ViewById(R.id.word9)
    TextView word9;
    @ViewById(R.id.word10)
    TextView word10;
    @ViewById(R.id.word11)
    TextView word11;
    @ViewById(R.id.word12)
    TextView word12;
    @ViewById(R.id.please_wait_progress_bar)
    View pleaseWaitProgressBar;
    @ViewById(R.id.mainForm)
    View mainForm;

    @EventBusGreenRobot
    EventBus eventBus;

    @StringRes(R.string.adding_new_word_set_fragment_warning_translation_not_found)
    String warningTranslationNotFound;
    @StringRes(R.string.adding_new_word_set_fragment_warning_empty_field)
    String warningEmptyField;
    @StringRes(R.string.adding_new_word_set_fragment_warning_duplicate_field)
    String warningDuplicateField;
    @StringRes(R.string.adding_new_word_set_fragment_warning_sentences_not_found)
    String warningSentencesNotFound;

    private List<TextView> allTextViews;

    private AddingNewWordSetPresenter presenter;

    private WaitingForProgressBarManager waitingForProgressBarManager;
    private AddingNewWordSetFragmentController controller;

    @AfterViews
    public void init() {
        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(pleaseWaitProgressBar, mainForm);
        allTextViews = asList(word1, word2, word3, word4, word5, word6, word7, word8, word9, word10, word11, word12);

        for (TextView textView : allTextViews) {
            textView.setOnFocusChangeListener(this);
        }
        presenter = presenterFactory.create(this, eventBus);
        controller = new AddingNewWordSetFragmentController(eventBus, serviceFactory);
        eventBus.post(new AddingNewWordSetFragmentGotReadyEM());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(AddingNewWordSetFragmentGotReadyEM event) {
        controller.handle(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordSetDraftLoadedEM event) {
        List<String> words = event.getNewWordSetDraft().getWords();
        for (int i = 0; i < words.size(); i++) {
            allTextViews.get(i).setText(words.get(i));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordTranslationWasNotFoundEM event) {
        TextView textView = allTextViews.get(event.getWordIndex());
        textView.setError(warningTranslationNotFound);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordIsEmptyEM event) {
        TextView textView = allTextViews.get(event.getWordIndex());
        textView.setError(warningEmptyField);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordIsDuplicateEM event) {
        TextView textView = allTextViews.get(event.getWordIndex());
        textView.setError(warningDuplicateField);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordSentencesWereNotFoundEM event) {
        TextView textView = allTextViews.get(event.getWordIndex());
        textView.setError(warningSentencesNotFound);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordSentencesWereFoundEM event) {
        TextView textView = allTextViews.get(event.getWordIndex());
        textView.setError(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordSuccessfullySubmittedEM event) {
        word1.setText("");
        word2.setText("");
        word3.setText("");
        word4.setText("");
        word5.setText("");
        word6.setText("");
        word7.setText("");
        word8.setText("");
        word9.setText("");
        word10.setText("");
        word11.setText("");
        word12.setText("");

        eventBus.post(new NewWordSetDraftWasChangedEM(getWords()));

        startWordSetActivity(event.getWordSet());
    }

    @Click(R.id.buttonSubmit)
    @Background
    public void onButtonSubmitClick() {
        presenter.submit(getWords());
    }

    private List<String> getWords() {
        return asList(
                word1.getText().toString(),
                word2.getText().toString(),
                word3.getText().toString(),
                word4.getText().toString(),
                word5.getText().toString(),
                word6.getText().toString(),
                word7.getText().toString(),
                word8.getText().toString(),
                word9.getText().toString(),
                word10.getText().toString(),
                word11.getText().toString(),
                word12.getText().toString()
        );
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
}