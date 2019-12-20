package talkapp.org.talkappmobile.widget.adapter.filterable;

import android.content.Context;

public abstract class ViewHolderFactory<T extends FilterableViewHolder> {
    public abstract T get(Context context);
}