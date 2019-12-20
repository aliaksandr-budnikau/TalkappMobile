package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EView;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.widget.adapter.filterable.FilterableAdapter;
import talkapp.org.talkappmobile.widget.adapter.filterable.FilterableViewHolder;
import talkapp.org.talkappmobile.widget.adapter.filterable.OnItemClickListener;
import talkapp.org.talkappmobile.widget.adapter.filterable.OnItemLongClickListener;

@EView
public class PhraseSetsRecyclerView extends RecyclerView {
    public PhraseSetsRecyclerView(@NonNull Context context) {
        super(context);
    }

    public PhraseSetsRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PhraseSetsRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterInject
    public void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        addItemDecoration(itemDecor);
    }

    @NonNull
    @Override
    public FilterableAdapter<WordSet, ViewHolder> getAdapter() {
        Adapter adapter = super.getAdapter();
        if (adapter == null) {
            throw new RuntimeException("adapter is null");
        }
        return (FilterableAdapter<WordSet, ViewHolder>) adapter;
    }

    public static class ViewHolder extends FilterableViewHolder<WordSet> {

        private final WordSetsListItemView view;
        private final OnItemLongClickListener onItemLongClickListener;
        private final OnItemClickListener onItemClickListener;

        ViewHolder(WordSetsListItemView view,
                   OnItemClickListener onItemClickListener,
                   OnItemLongClickListener onItemLongClickListener) {
            super(view);
            this.view = view;
            this.onItemClickListener = onItemClickListener;
            this.onItemLongClickListener = onItemLongClickListener;
        }

        @Override
        public void bind(final WordSet item, final int position) {
            view.setModel(item);
            view.refreshModel();
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.onItemLongClick(position);
                    }
                    return true;
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            });
        }
    }

    public static class ViewHolderFactory extends talkapp.org.talkappmobile.widget.adapter.filterable.ViewHolderFactory<ViewHolder> {

        private final OnItemLongClickListener onItemLongClickListener;
        private final OnItemClickListener onItemClickListener;

        public ViewHolderFactory(OnItemLongClickListener onItemLongClickListener, OnItemClickListener onItemClickListener) {
            this.onItemLongClickListener = onItemLongClickListener;
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public ViewHolder get(Context context) {
            WordSetsListItemView v = WordSetsListItemView_.build(context);
            return new ViewHolder(v, onItemClickListener, onItemLongClickListener);
        }
    }
}