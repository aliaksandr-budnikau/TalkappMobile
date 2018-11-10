package talkapp.org.talkappmobile.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.adapter.AdaptersFactory;
import talkapp.org.talkappmobile.activity.interactor.TopicsFragmentInteractor;
import talkapp.org.talkappmobile.activity.presenter.TopicsFragmentPresenter;
import talkapp.org.talkappmobile.activity.view.TopicsFragmentView;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.Topic;

import static talkapp.org.talkappmobile.activity.AllWordSetsFragment.TOPIC_ID_MAPPING;

public class TopicsFragment extends Fragment implements AdapterView.OnItemClickListener, TopicsFragmentView {
    @Inject
    AdaptersFactory adaptersFactory;
    @Inject
    TopicsFragmentInteractor interactor;
    @Inject
    Executor executor;
    @Inject
    Handler uiEventHandler;
    @Inject
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;

    private WaitingForProgressBarManager waitingForProgressBarManager;

    private ArrayAdapter<Topic> adapter;

    private TopicsFragmentPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_topics_layout, container, false);
        DIContextUtils.get().inject(this);

        adapter = adaptersFactory.createTopicListAdapter(this.getActivity());

        ListView topicsListView = view.findViewById(R.id.topicsListView);
        topicsListView.setAdapter(adapter);
        topicsListView.setOnItemClickListener(this);

        View progressBarView = view.findViewById(R.id.please_wait_progress_bar);
        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(progressBarView, topicsListView);

        presenter = new TopicsFragmentPresenter(this, interactor);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                presenter.initialize();
                return null;
            }
        }.executeOnExecutor(executor);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Topic topic = adapter.getItem(position);
        presenter.onTopicClick(topic);
    }

    @Override
    public void setTopics(final List<Topic> topics) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                adapter.addAll(topics);
            }
        });
    }

    @Override
    public void openTopicWordSetsFragment(Topic topic) {
        Bundle args = new Bundle();
        args.putInt(TOPIC_ID_MAPPING, topic.getId());
        AllWordSetsFragment fragment = new AllWordSetsFragment();
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void onInitializeBeginning() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                waitingForProgressBarManager.showProgressBar();
            }
        });
    }

    @Override
    public void onInitializeEnd() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                waitingForProgressBarManager.hideProgressBar();
            }
        });
    }
}
