package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EView;

import java.util.List;

import talkapp.org.talkappmobile.model.WordSet;

@EView
public class WordSetsListListView extends ListView {
    @Bean
    WordSetListAdapter adapter;

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
}