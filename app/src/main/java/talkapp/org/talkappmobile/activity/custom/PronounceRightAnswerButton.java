package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.interactor.PronounceRightAnswerButtonInteractor;
import talkapp.org.talkappmobile.activity.custom.presenter.PronounceRightAnswerButtonPresenter;
import talkapp.org.talkappmobile.activity.custom.view.PronounceRightAnswerButtonView;
import org.talkappmobile.events.AnswerHasBeenRevealedEM;
import org.talkappmobile.events.AnswerPronunciationStartedEM;
import org.talkappmobile.events.AnswerPronunciationStoppedEM;
import org.talkappmobile.events.ExerciseGotAnsweredEM;
import org.talkappmobile.events.NewSentenceEM;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.impl.SpeakerBean;

@EView
public class PronounceRightAnswerButton extends android.support.v7.widget.AppCompatButton implements PronounceRightAnswerButtonView {
    @Bean(SpeakerBean.class)
    Speaker speaker;
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
        PronounceRightAnswerButtonInteractor interactor = new PronounceRightAnswerButtonInteractor(speaker);
        presenter = new PronounceRightAnswerButtonPresenter(interactor, this);
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
}