package talkapp.org.talkappmobile.activity.custom;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.UiThread;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import talkapp.org.talkappmobile.component.PresenterFactoryProvider;
import talkapp.org.talkappmobile.presenter.PresenterFactory;
import talkapp.org.talkappmobile.events.AnswerHasBeenRevealedEM;
import talkapp.org.talkappmobile.events.ExerciseGotAnsweredEM;
import talkapp.org.talkappmobile.events.NewSentenceEM;
import talkapp.org.talkappmobile.events.PracticeHalfFinishedEM;
import talkapp.org.talkappmobile.events.RightAnswerTouchedEM;
import talkapp.org.talkappmobile.events.RightAnswerUntouchedEM;
import talkapp.org.talkappmobile.presenter.RightAnswerTextViewPresenter;
import talkapp.org.talkappmobile.view.RightAnswerTextViewView;

@EView
public class RightAnswerTextView extends AppCompatTextView implements RightAnswerTextViewView {

    @Bean
    PresenterFactoryProvider presenterFactoryProvider;
    @EventBusGreenRobot
    EventBus eventBus;

    private RightAnswerTextViewPresenter presenter;

    public RightAnswerTextView(Context context) {
        super(context);
    }

    public RightAnswerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RightAnswerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterInject
    public void init() {
        PresenterFactory presenterFactory = presenterFactoryProvider.get();
        presenter = presenterFactory.create(this);
    }

    @Override
    @UiThread
    public void onNewValue(String newValue) {
        setText(newValue);
    }

    @Override
    public void answerHasBeenSeen() {
        eventBus.post(new AnswerHasBeenRevealedEM());
    }

    @Override
    public void turnAnswerToLink(String value) {
        this.setText(value);
    }

    @Override
    public void openGoogleTranslate(String input, String langFrom, String langTo) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, input);
        intent.putExtra("key_text_input", input);
        intent.putExtra("key_text_output", "");
        intent.putExtra("key_language_from", langFrom);
        intent.putExtra("key_language_to", langTo);
        intent.putExtra("key_suggest_translation", "");
        intent.putExtra("key_from_floating_window", false);
        intent.setComponent(new ComponentName(
                "com.google.android.apps.translate",
                "com.google.android.apps.translate.TranslateActivity"));
        getContext().startActivity(intent);
    }

    @Override
    public void onActivityNotFoundException() {
        Toast.makeText(getContext(), "Sorry, No Google Translation Installed", Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewSentenceEM event) {
        presenter.setModel(event.getSentence(), event.getWord());
        presenter.unlock();
        presenter.mask();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ExerciseGotAnsweredEM event) {
        presenter.lock();
        presenter.turnAnswerToLink();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PracticeHalfFinishedEM event) {
        presenter.enableHideAllMode();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RightAnswerUntouchedEM event) {
        presenter.mask();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RightAnswerTouchedEM event) {
        presenter.rightAnswerTouched();
    }
}