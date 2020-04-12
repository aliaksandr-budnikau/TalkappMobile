package talkapp.org.talkappmobile.widget.adapter.filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

public class FilterableAdapter<T, H extends FilterableViewHolder<T>> extends RecyclerView.Adapter<FilterableViewHolder<T>> implements FilterableAdapterView {
    private final FilterableAdapterPresenter<T> presenter;
    private final ViewHolderFactory<H> viewHolderFactory;

    public FilterableAdapter(List<T> inputItemsList, ViewHolderFactory<H> viewHolderFactory) {
        this.presenter = new FilterableAdapterPresenter<>(new FilterableAdapterInteractorImpl<>(inputItemsList), this);
        this.viewHolderFactory = viewHolderFactory;
    }

    public T get(int position) {
        return presenter.get(position);
    }

    public List<T> getItems() {
        return presenter.getAll();
    }

    public void addAll(List<T> inputItemsList) {
        presenter.replaceAll(inputItemsList);
    }

    public void filterOut(AbstractFilter<T> filter) {
        presenter.filterOut(filter);
    }

    @NonNull
    @Override
    public FilterableViewHolder<T> onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return viewHolderFactory.get(viewGroup.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull FilterableViewHolder<T> viewHolder, int position) {
        T item = presenter.get(position);
        viewHolder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return presenter.getSize();
    }

    public void setItem(T item, int position) {
        presenter.setItem(item, position);
    }

    public void removeItem(int position) {
        presenter.remove(position);
    }

    @Override
    public void onContentChanged() {
        notifyDataSetChanged();
    }
}