package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.UiThread;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import talkapp.org.talkappmobile.activity.custom.interactor.RightAnswerTextViewInteractor;
import talkapp.org.talkappmobile.activity.custom.presenter.RightAnswerTextViewPresenter;
import talkapp.org.talkappmobile.activity.custom.view.RightAnswerTextViewView;
import talkapp.org.talkappmobile.activity.event.wordset.NewSentenceEM;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

@EView
public class RightAnswerTextView extends AppCompatTextView implements RightAnswerTextViewView {
    @Bean(TextUtilsImpl.class)
    TextUtils textUtils;

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
        RightAnswerTextViewInteractor interacto = new RightAnswerTextViewInteractor(textUtils);
        presenter = new RightAnswerTextViewPresenter(interacto, this);
    }

    public void setModel(Sentence sentence, Word2Tokens word) {
        presenter.setModel(sentence, word);
    }

    public void maskEntirely() {
        presenter.maskEntirely();
    }

    public void maskOnlyWord() {
        presenter.maskOnlyWord();
    }

    public void unmask() {
        presenter.unmask();
    }

    public void lock() {
        presenter.lock();
    }

    @Override
    @UiThread
    public void onNewValue(String newValue) {
        setText(newValue);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewSentenceEM event) {
        presenter.setModel(event.getSentence(), event.getWord());
        presenter.unlock();
        if (event.isHideEntirely()) {
            presenter.maskEntirely();
        } else {
            presenter.maskOnlyWord();
        }
    }
}