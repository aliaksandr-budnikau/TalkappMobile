package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import talkapp.org.talkappmobile.activity.event.wordset.WordSetsFinishedFilterAppliedEM;
import talkapp.org.talkappmobile.activity.event.wordset.WordSetsLearnedRepFilterAppliedEM;
import talkapp.org.talkappmobile.activity.event.wordset.WordSetsNewFilterAppliedEM;
import talkapp.org.talkappmobile.activity.event.wordset.WordSetsNewRepFilterAppliedEM;
import talkapp.org.talkappmobile.activity.event.wordset.WordSetsRepeatedRepFilterAppliedEM;
import talkapp.org.talkappmobile.activity.event.wordset.WordSetsSeenRepFilterAppliedEM;
import talkapp.org.talkappmobile.activity.event.wordset.WordSetsStartedFilterAppliedEM;
import talkapp.org.talkappmobile.model.WordSet;

@EView
public class WordSetsListListView extends ListView {
    @Bean
    WordSetListAdapter adapter;

    @EventBusGreenRobot
    EventBus eventBus;

    public WordSetsListListView(Context context) {
        super(context);
    }

    public WordSetsListListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WordSetsListListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterInject
    public void init() {
        this.setAdapter(adapter);
    }

    public void addAll(List<WordSet> wordSets) {
        adapter.addAll(wordSets);
    }

    public void refreshModel() {
        adapter.refreshModel();
    }

    public WordSet getWordSet(int position) {
        return adapter.getWordSet(position);
    }

    public WordSetsListItemView getClickedItemView(int clickedItemNumber) {
        return (WordSetsListItemView) adapter.getView(clickedItemNumber, null, this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WordSetsNewFilterAppliedEM event) {
        adapter.filterNew();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WordSetsStartedFilterAppliedEM event) {
        adapter.filterStarted();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WordSetsFinishedFilterAppliedEM event) {
        adapter.filterFinished();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WordSetsNewRepFilterAppliedEM event) {
        adapter.filterNewRep();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WordSetsSeenRepFilterAppliedEM event) {
        adapter.filterSeenRep();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WordSetsRepeatedRepFilterAppliedEM event) {
        adapter.filterRepeatedRep();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WordSetsLearnedRepFilterAppliedEM event) {
        adapter.filterLearnedRep();
    }
}