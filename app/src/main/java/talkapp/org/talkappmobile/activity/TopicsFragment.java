package talkapp.org.talkappmobile.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.adapter.AdaptersFactory;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.AuthSign;
import talkapp.org.talkappmobile.service.TopicService;

import static talkapp.org.talkappmobile.activity.AllWordSetsFragment.TOPIC_ID_MAPPING;

public class TopicsFragment extends Fragment implements AdapterView.OnItemClickListener {
    @Inject
    TopicService topicService;
    @Inject
    AdaptersFactory adaptersFactory;
    @Inject
    AuthSign authSign;
    private ListView topicsListView;
    private ArrayAdapter<Topic> adapter;

    private AsyncTask<String, Object, List<Topic>> loadingTopics = new AsyncTask<String, Object, List<Topic>>() {
        @Override
        protected List<Topic> doInBackground(String... params) {
            Call<List<Topic>> topicCall = topicService.findAll(authSign);
            Response<List<Topic>> topics = null;
            try {
                topics = topicCall.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return topics.body();
        }

        @Override
        protected void onPostExecute(List<Topic> topics) {
            adapter.addAll(topics);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_topics_layout, container, false);
        DIContext.get().inject(this);

        adapter = adaptersFactory.createTopicListAdapter(this.getActivity());

        topicsListView = view.findViewById(R.id.topicsListView);
        topicsListView.setAdapter(adapter);
        topicsListView.setOnItemClickListener(this);

        loadingTopics.execute();
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Topic topic = adapter.getItem(position);
        FragmentManager fragmentManager = getFragmentManager();
        AllWordSetsFragment fragment = new AllWordSetsFragment();
        Bundle args = new Bundle();
        args.putString(TOPIC_ID_MAPPING, topic.getId());
        fragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }
}
