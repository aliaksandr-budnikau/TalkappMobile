package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.events.AnswerHasBeenRevealedEM;
import talkapp.org.talkappmobile.events.NewSentenceEM;

@EView
public class WordSetsOriginalTextLayout extends RelativeLayout {

    @EventBusGreenRobot
    EventBus eventBus;
    private LayerDrawable background;

    public WordSetsOriginalTextLayout(Context context) {
        super(context);
    }

    public WordSetsOriginalTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WordSetsOriginalTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterInject
    public void init() {
        background = (LayerDrawable) getBackground().mutate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewSentenceEM event) {
        GradientDrawable drawable = (GradientDrawable) background.getDrawable(1);
        drawable.setColor(getResources().getColor(R.color.color_layout_answerNotSeen_background));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AnswerHasBeenRevealedEM event) {
        GradientDrawable drawable = (GradientDrawable) background.getDrawable(1);
        drawable.setColor(getResources().getColor(R.color.color_layout_answerSeen_background));
    }
}