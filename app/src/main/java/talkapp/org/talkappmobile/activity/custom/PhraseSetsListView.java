package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import talkapp.org.talkappmobile.model.WordSet;

import static talkapp.org.talkappmobile.model.RepetitionClass.LEARNED;
import static talkapp.org.talkappmobile.model.RepetitionClass.NEW;
import static talkapp.org.talkappmobile.model.RepetitionClass.REPEATED;
import static talkapp.org.talkappmobile.model.RepetitionClass.SEEN;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;

@EView
public class PhraseSetsListView extends RecyclerView {
    public PhraseSetsListView(@NonNull Context context) {
        super(context);
    }

    public PhraseSetsListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PhraseSetsListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterInject
    public void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        addItemDecoration(itemDecor);
    }

    @Nullable
    @Override
    public PhraseSetsListView.Adapter getAdapter() {
        return (Adapter) super.getAdapter();
    }

    public static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private final List<ExpandablePhraseSets> origWordSetList;
        private final List<Integer> filteredWordSetList = new ArrayList<>();
        private final Filter onlyNewWordSets;
        private final Filter onlyStartedWordSets;
        private final Filter onlyFinishedWordSets;
        private final Filter onlyNewRepWordSets;
        private final Filter onlySeenRepWordSets;
        private final Filter onlyRepeatedRepWordSets;
        private final Filter onlyLearnedRepWordSets;

        private OnItemLongClickListener itemLongClickListener;
        private OnItemClickListener itemClickListener;

        public Adapter(List<WordSet> wordSetsList, OnItemLongClickListener itemLongClickListener, OnItemClickListener itemClickListener) {
            origWordSetList = getNewUnmodifiableList(wordSetsList);

            onlyNewWordSets = new Filter() {
                @Override
                public boolean filter(WordSet wordSet) {
                    return wordSet.getTrainingExperience() == 0;
                }
            };
            onlyStartedWordSets = new Filter() {
                @Override
                public boolean filter(WordSet wordSet) {
                    return wordSet.getTrainingExperience() != 0 && !FINISHED.equals(wordSet.getStatus());
                }
            };
            onlyFinishedWordSets = new Filter() {
                @Override
                public boolean filter(WordSet wordSet) {
                    return FINISHED.equals(wordSet.getStatus());
                }
            };
            onlyNewRepWordSets = new Filter() {
                @Override
                public boolean filter(WordSet wordSet) {
                    return wordSet.getRepetitionClass() == NEW;
                }
            };
            onlySeenRepWordSets = new Filter() {
                @Override
                public boolean filter(WordSet wordSet) {
                    return wordSet.getRepetitionClass() == SEEN;
                }
            };
            onlyRepeatedRepWordSets = new Filter() {
                @Override
                public boolean filter(WordSet wordSet) {
                    return wordSet.getRepetitionClass() == REPEATED;
                }
            };
            onlyLearnedRepWordSets = new Filter() {
                @Override
                public boolean filter(WordSet wordSet) {
                    return wordSet.getRepetitionClass() == LEARNED;
                }
            };
            this.itemLongClickListener = itemLongClickListener;
            this.itemClickListener = itemClickListener;
        }

        public Adapter(List<WordSet> wordSetsList) {
            this(wordSetsList, null, null);
        }

        private List<ExpandablePhraseSets> getNewUnmodifiableList(List<WordSet> wordSetsList) {
            ArrayList<ExpandablePhraseSets> expandablePhraseSets = new ArrayList<>();
            for (WordSet wordSet : wordSetsList) {
                expandablePhraseSets.add(new ExpandablePhraseSets(wordSet));
            }
            return expandablePhraseSets;
        }

        public WordSet getByPosition(int position) {
            Integer index = filteredWordSetList.get(position);
            return origWordSetList.get(index).getWordSet();
        }

        public List<WordSet> getWordSets() {
            return getWordSets(origWordSetList);
        }

        public void addAll(List<WordSet> wordSetList) {
            setWordSet(wordSetList);
        }

        private void filter(List<WordSet> origList, Filter filter) {
            LinkedList<WordSet> filtered = new LinkedList<>();
            for (WordSet wordSet : origList) {
                if (filter.filter(wordSet)) {
                    filtered.add(wordSet);
                }
            }
            setWordSet(filtered);
        }

        private List<WordSet> getWordSets(List<ExpandablePhraseSets> expandablePhraseSets) {
            ArrayList<WordSet> wordSets = new ArrayList<>();
            for (ExpandablePhraseSets expandable : expandablePhraseSets) {
                wordSets.add(expandable.getWordSet());
            }
            return wordSets;
        }

        public void filterNew() {
            filter(getWordSets(origWordSetList), onlyNewWordSets);
            notifyDataSetChanged();
        }

        public void filterStarted() {
            filter(getWordSets(origWordSetList), onlyStartedWordSets);
            notifyDataSetChanged();
        }

        public void filterFinished() {
            filter(getWordSets(origWordSetList), onlyFinishedWordSets);
            notifyDataSetChanged();
        }

        public void filterNewRep() {
            filter(getWordSets(origWordSetList), onlyNewRepWordSets);
            notifyDataSetChanged();
        }

        public void filterSeenRep() {
            filter(getWordSets(origWordSetList), onlySeenRepWordSets);
            notifyDataSetChanged();
        }

        public void filterRepeatedRep() {
            filter(getWordSets(origWordSetList), onlyRepeatedRepWordSets);
            notifyDataSetChanged();
        }

        public void filterLearnedRep() {
            filter(getWordSets(origWordSetList), onlyLearnedRepWordSets);
            notifyDataSetChanged();
        }

        private void setWordSet(List<WordSet> wordSetsList) {
            filteredWordSetList.clear();
            for (WordSet wordSet : wordSetsList) {
                int index = origWordSetList.indexOf(new ExpandablePhraseSets(wordSet));
                this.filteredWordSetList.add(index);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            WordSetsListItemView v = WordSetsListItemView_.build(viewGroup.getContext());
            return new Adapter.ViewHolder(v, itemClickListener, itemLongClickListener);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            Integer index = this.filteredWordSetList.get(position);
            viewHolder.bind(origWordSetList.get(index), position);
        }

        @Override
        public int getItemCount() {
            return this.filteredWordSetList.size();
        }

        public void setWordSet(WordSet wordSet, int itemNumber) {
            Integer index = filteredWordSetList.get(itemNumber);
            origWordSetList.set(index, new ExpandablePhraseSets(wordSet));
            notifyDataSetChanged();
        }

        public interface OnItemLongClickListener {
            void onItemLongClick(int position);
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        private static abstract class Filter {
            public abstract boolean filter(WordSet wordSet);
        }

        private static class ViewHolder extends RecyclerView.ViewHolder {

            private final WordSetsListItemView view;
            private final OnItemLongClickListener onItemLongClickListener;
            private final OnItemClickListener onItemClickListener;

            private ViewHolder(WordSetsListItemView view,
                               OnItemClickListener onItemClickListener,
                               OnItemLongClickListener onItemLongClickListener) {
                super(view);
                this.view = view;
                this.onItemClickListener = onItemClickListener;
                this.onItemLongClickListener = onItemLongClickListener;
            }

            void bind(final ExpandablePhraseSets phraseSets, final int position) {
                view.setModel(phraseSets.getWordSet());
                view.refreshModel();
                view.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (onItemLongClickListener != null) {
                            onItemLongClickListener.onItemLongClick(position);
                        }
                        return true;
                    }
                });
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                });
            }
        }
    }

    private static class ExpandablePhraseSets {
        private final WordSet wordSet;
        private boolean expanded;

        public ExpandablePhraseSets(WordSet wordSet) {
            this.wordSet = new WordSet(wordSet);
        }

        public WordSet getWordSet() {
            return new WordSet(wordSet);
        }

        public boolean isExpanded() {
            return expanded;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExpandablePhraseSets that = (ExpandablePhraseSets) o;
            if (wordSet.equals(that.wordSet)) {
                return wordSet.getWords().equals(that.wordSet.getWords());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(wordSet);
        }
    }
}