package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import talkapp.org.talkappmobile.activity.event.wordset.WordSetVocabularyLoadedEM;
import talkapp.org.talkappmobile.model.WordTranslation;

@EView
public class WordSetVocabularyView extends RecyclerView {
    @EventBusGreenRobot
    EventBus eventBus;

    public WordSetVocabularyView(Context context) {
        super(context);
    }

    public WordSetVocabularyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterInject
    public void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WordSetVocabularyLoadedEM event) {
        this.setAdapter(new VocabularyAdapter(event.getTranslations()));
    }

    private static class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.ViewHolder> {
        private final WordTranslation[] translations;

        private VocabularyAdapter(WordTranslation[] translations) {
            this.translations = translations;
        }

        @Override
        public VocabularyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = WordSetVocabularyItemView_.build(parent.getContext());
            return new ViewHolder((WordSetVocabularyItemView) v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(this.translations[position]);
        }

        @Override
        public int getItemCount() {
            return translations.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final WordSetVocabularyItemView view;

            private ViewHolder(WordSetVocabularyItemView view) {
                super(view);
                this.view = view;
            }

            void bind(final WordTranslation translation) {
                view.setModel(translation);
                view.refreshModel();
            }
        }
    }
}