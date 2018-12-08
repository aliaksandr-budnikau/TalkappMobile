package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.activity.custom.interactor.WordSetListAdapterInteractor;
import talkapp.org.talkappmobile.activity.custom.presenter.WordSetListAdapterPresenter;
import talkapp.org.talkappmobile.activity.custom.view.WordSetListAdapterView;
import talkapp.org.talkappmobile.component.database.WordSetExperienceService;
import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

/**
 * @author Budnikau Aliaksandr
 */
@EBean
public class WordSetListAdapter extends ArrayAdapter<WordSet> implements WordSetListAdapterView {
    @Inject
    WordSetExperienceService experienceService;
    @RootContext
    Context context;
    private SparseArray<WordSetsListItemView> childViews;
    private WordSetListAdapterPresenter presenter;

    public WordSetListAdapter(@NonNull final Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @AfterInject
    public void init() {
        DIContextUtils.get().inject(this);
        WordSetListAdapterInteractor interactor = new WordSetListAdapterInteractor(experienceService);
        presenter = new WordSetListAdapterPresenter(interactor, this);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        WordSetsListItemView itemView;
        if (convertView == null) {
            itemView = childViews.get(position);
            if (itemView == null) {
                itemView = WordSetsListItemView_.build(context);
                childViews.put(position, itemView);
            }
        } else {
            itemView = (WordSetsListItemView) convertView;
        }
        WordSet wordSet = presenter.getWordSet(position);
        WordSetExperience experience = presenter.getWordSetExperience(position);
        itemView.setModel(wordSet, experience);
        if (wordSet.getId() == 0 || experience == null || experience.getTrainingExperience() == 0) {
            itemView.hideProgress();
        } else {
            itemView.showProgress();
        }
        itemView.refreshModel();
        return itemView;
    }

    public WordSet getWordSet(int position) {
        return presenter.getWordSet(position);
    }

    public WordSetExperience getWordSetExperience(int position) {
        return presenter.getWordSetExperience(position);
    }

    public void addAll(List<WordSet> wordSetList) {
        childViews = new SparseArray<>(wordSetList.size());
        presenter.setModel(wordSetList);
    }

    public void refreshModel() {
        presenter.refreshModel();
    }

    @Override
    public void onModelPrepared(List<WordSet> wordSetList) {
        super.addAll(wordSetList);
    }
}
