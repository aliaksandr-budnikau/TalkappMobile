package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

import java.util.List;

import talkapp.org.talkappmobile.activity.custom.interactor.WordSetListAdapterInteractor;
import talkapp.org.talkappmobile.activity.custom.presenter.WordSetListAdapterPresenter;
import talkapp.org.talkappmobile.activity.custom.view.WordSetListAdapterView;

import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;

/**
 * @author Budnikau Aliaksandr
 */
@EBean
public class WordSetListAdapter extends ArrayAdapter<WordSet> implements WordSetListAdapterView {
    @Bean(ServiceFactoryBean.class)
    ServiceFactory serviceFactory;
    @RootContext
    Context context;
    private SparseArray<WordSetsListItemView> childViews;
    private WordSetListAdapterPresenter presenter;

    public WordSetListAdapter(@NonNull final Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @AfterInject
    public void init() {
        WordSetListAdapterInteractor interactor = new WordSetListAdapterInteractor();
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
        itemView.setModel(wordSet);
        if (wordSet.getId() == 0 || wordSet.getTrainingExperience() == 0 || wordSet.getStatus() == FINISHED) {
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

    public void addAll(List<WordSet> wordSetList) {
        childViews = new SparseArray<>(wordSetList.size());
        presenter.setModel(wordSetList);
    }

    public void refreshModel() {
        presenter.refreshModel();
    }

    @Override
    public void onModelPrepared(List<WordSet> wordSetList) {
        super.clear();
        childViews = new SparseArray<>(wordSetList.size());
        super.addAll(wordSetList);
    }

    @Override
    public void onWordSetRemoved() {
        notifyDataSetChanged();
    }

    public void filterNew() {
        presenter.filterNew();
        notifyDataSetChanged();
    }

    public void filterStarted() {
        presenter.filterStarted();
        notifyDataSetChanged();
    }

    public void filterFinished() {
        presenter.filterFinished();
        notifyDataSetChanged();
    }

    public void filterNewRep() {
        presenter.filterNewRep();
        notifyDataSetChanged();
    }

    public void filterSeenRep() {
        presenter.filterSeenRep();
        notifyDataSetChanged();
    }

    public void filterRepeatedRep() {
        presenter.filterRepeatedRep();
        notifyDataSetChanged();
    }

    public void filterLearnedRep() {
        presenter.filterLearnedRep();
        notifyDataSetChanged();
    }

    @Override
    public void remove(@Nullable WordSet wordSet) {
        presenter.remove(wordSet);
        super.remove(wordSet);
    }
}