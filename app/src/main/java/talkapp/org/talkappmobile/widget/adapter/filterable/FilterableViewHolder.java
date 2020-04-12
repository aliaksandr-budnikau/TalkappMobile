package talkapp.org.talkappmobile.widget.adapter.filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public abstract class FilterableViewHolder<T> extends RecyclerView.ViewHolder {
    public FilterableViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(T item, int position);
}