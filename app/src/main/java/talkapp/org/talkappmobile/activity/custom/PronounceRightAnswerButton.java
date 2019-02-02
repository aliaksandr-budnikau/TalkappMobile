package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import talkapp.org.talkappmobile.activity.custom.presenter.PronounceRightAnswerButtonPresenter;
import talkapp.org.talkappmobile.activity.event.wordset.NewSentenceEM;

@EView
public class PronounceRightAnswerButton extends android.support.v7.widget.AppCompatButton {

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
        presenter = new PronounceRightAnswerButtonPresenter();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewSentenceEM event) {
        presenter.setModel(event.getSentence());
        presenter.unlock();
    }
}