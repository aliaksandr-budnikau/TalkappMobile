package org.talkappmobile.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.view.View;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.talkappmobile.model.Topic;
import org.talkappmobile.service.BackendServerFactory;
import org.talkappmobile.service.impl.BackendServerFactoryBean;

import java.util.List;

import org.talkappmobile.R;
import org.talkappmobile.activity.custom.TopicListAdapter;
import org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import org.talkappmobile.activity.interactor.TopicsFragmentInteractor;
import org.talkappmobile.activity.presenter.TopicsFragmentPresenter;
import org.talkappmobile.activity.view.TopicsFragmentView;

import static org.talkappmobile.activity.FragmentFactory.createWordSetsListFragment;

@EFragment(value = R.layout.all_topics_layout)
public class TopicsFragment extends Fragment implements TopicsFragmentView {
    @Bean
    TopicListAdapter adapter;
    @Bean(BackendServerFactoryBean.class)
    BackendServerFactory backendServerFactory;
    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;

    @ViewById(R.id.topicsListView)
    ListView topicsListView;
    @ViewById(R.id.please_wait_progress_bar)
    View progressBarView;

    private WaitingForProgressBarManager waitingForProgressBarManager;
    private TopicsFragmentPresenter presenter;

    @AfterViews
    public void init() {
        TopicsFragmentInteractor interactor = new TopicsFragmentInteractor(backendServerFactory.get());

        topicsListView.setAdapter(adapter);

        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(progressBarView, topicsListView);

        presenter = new TopicsFragmentPresenter(this, interactor);

        initPresenter();
    }

    @Background
    public void initPresenter() {
        presenter.initialize();
    }

    @ItemClick(R.id.topicsListView)
    public void onItemClick(int position) {
        Topic topic = adapter.getItem(position);
        presenter.onTopicClick(topic);
    }

    @Override
    @UiThread
    public void setTopics(final List<Topic> topics) {
        adapter.addAll(topics);
    }

    @Override
    public void openTopicWordSetsFragment(Topic topic) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, createWordSetsListFragment(topic)).commit();
    }

    @Override
    @UiThread
    public void onInitializeBeginning() {
        waitingForProgressBarManager.showProgressBar();
    }

    @Override
    @UiThread
    public void onInitializeEnd() {
        waitingForProgressBarManager.hideProgressBar();
    }
}