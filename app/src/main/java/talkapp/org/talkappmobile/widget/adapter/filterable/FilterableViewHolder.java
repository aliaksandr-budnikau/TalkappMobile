package talkapp.org.talkappmobile.widget.adapter.filterable;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class FilterableViewHolder<T> extends RecyclerView.ViewHolder {
    public FilterableViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(T item, int position);
}