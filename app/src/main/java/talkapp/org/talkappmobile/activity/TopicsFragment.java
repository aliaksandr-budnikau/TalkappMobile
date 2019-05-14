package talkapp.org.talkappmobile.activity;

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

import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.TopicListAdapter;
import talkapp.org.talkappmobile.activity.interactor.TopicsFragmentInteractor;
import talkapp.org.talkappmobile.activity.presenter.TopicsFragmentPresenter;
import talkapp.org.talkappmobile.activity.view.TopicsFragmentView;
import talkapp.org.talkappmobile.component.backend.BackendServerFactory;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.model.Topic;

import static talkapp.org.talkappmobile.activity.FragmentFactory.createWordSetsListFragment;

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