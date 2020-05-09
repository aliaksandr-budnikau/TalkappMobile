package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.UiThread;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.component.PresenterFactoryProvider;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.impl.SpeakerBean;
import talkapp.org.talkappmobile.events.AnswerHasBeenRevealedEM;
import talkapp.org.talkappmobile.events.AnswerPronunciationStartedEM;
import talkapp.org.talkappmobile.events.AnswerPronunciationStoppedEM;
import talkapp.org.talkappmobile.events.ExerciseGotAnsweredEM;
import talkapp.org.talkappmobile.events.NewSentenceEM;
import talkapp.org.talkappmobile.presenter.PresenterFactory;
import talkapp.org.talkappmobile.presenter.PronounceRightAnswerButtonPresenter;
import talkapp.org.talkappmobile.view.PronounceRightAnswerButtonView;

@EView
public class PronounceRightAnswerButton extends androidx.appcompat.widget.AppCompatButton implements PronounceRightAnswerButtonView {
    @Bean(SpeakerBean.class)
    Speaker speaker;
    @Bean
    PresenterFactoryProvider presenterFactoryProvider;
    @EventBusGreenRobot
    EventBus eventBus;
    PronounceRightAnswerButtonPresenter presenter;

    public PronounceRightAnswerButton(Context context) {
        super(context);
    }

    public PronounceRightAnswerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PronounceRightAnswerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterInject
    public void init() {
        PresenterFactory presenterFactory = presenterFactoryProvider.get();
        presenter = presenterFactory.create(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewSentenceEM event) {
        presenter.setModel(event.getSentence());
        presenter.unlock();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ExerciseGotAnsweredEM event) {
        presenter.lock();
    }

    @Click(R.id.pronounceRightAnswerButton)
    @Background
    public void onPronounceRightAnswerButtonClick() {
        presenter.pronounceRightAnswerButtonClick();
    }

    @Override
    public void onStartSpeaking() {
        eventBus.post(new AnswerPronunciationStartedEM());
    }

    @Override
    public void onStopSpeaking() {
        eventBus.post(new AnswerPronunciationStoppedEM());
    }

    @Override
    public void onAnswerHasBeenRevealed() {
        eventBus.post(new AnswerHasBeenRevealedEM());
    }

    @Override
    @UiThread
    public void onPronounceRightAnswer(String text) {
        speaker.speak(text);
    }
}