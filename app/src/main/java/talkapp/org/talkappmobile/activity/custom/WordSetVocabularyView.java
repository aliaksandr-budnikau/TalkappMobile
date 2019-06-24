package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
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

import org.talkappmobile.events.WordSetVocabularyLoadedEM;
import org.talkappmobile.model.WordTranslation;

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
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        addItemDecoration(itemDecor);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WordSetVocabularyLoadedEM event) {
        this.setAdapter(new VocabularyAdapter(event.getTranslations()));
    }

    private static class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.ViewHolder> {
        private final WordTranslationExpandable[] translations;

        private VocabularyAdapter(WordTranslation[] translations) {
            this.translations = new WordTranslationExpandable[translations.length];
            for (int i = 0; i < translations.length; i++) {
                this.translations[i] = new WordTranslationExpandable(translations[i]);
            }
        }

        @Override
        public VocabularyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = WordSetVocabularyItemView_.build(parent.getContext());
            return new ViewHolder((WordSetVocabularyItemView) v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(this.translations[position], position);
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

            void bind(final WordTranslationExpandable translation, final int position) {
                view.setModel(translation.getTranslation());
                view.refreshModel(translation.isExpanded());
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean expanded = translation.isExpanded();
                        translation.setExpanded(!expanded);
                        notifyItemChanged(position);
                    }
                });
            }
        }
    }

    private static class WordTranslationExpandable {
        private WordTranslation translation;
        private boolean expanded;

        WordTranslationExpandable(WordTranslation translation) {
            this.translation = translation;
        }

        public WordTranslation getTranslation() {
            return translation;
        }

        public void setTranslation(WordTranslation translation) {
            this.translation = translation;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }
    }
}