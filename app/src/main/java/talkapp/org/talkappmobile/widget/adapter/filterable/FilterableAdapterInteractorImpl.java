package talkapp.org.talkappmobile.widget.adapter.filterable;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

class FilterableAdapterInteractorImpl<T> implements FilterableAdapterInteractor<T> {
    private final List<T> origContent;
    private final List<Integer> filteredContent = new ArrayList<>();

    FilterableAdapterInteractorImpl(List<T> list) {
        origContent = new ArrayList<>(list);
    }

    @Override
    public T get(int position) {
        Integer index = filteredContent.get(position);
        return origContent.get(index);
    }

    @Override
    public List<T> getAll() {
        return unmodifiableList(origContent);
    }

    @Override
    public void replaceAll(List<T> list) {
        filteredContent.clear();
        for (T item : list) {
            int index = origContent.indexOf(item);
            this.filteredContent.add(index);
        }
    }

    @Override
    public void filterOut(AbstractFilter<T> filter, FilterableAdapterListener listener) {
        filteredContent.clear();
        for (int i = 0; i < origContent.size(); i++) {
            if (filter.filter(origContent.get(i))) {
                filteredContent.add(i);
            }
        }
        listener.onContentChanged();
    }

    @Override
    public int getSize() {
        return filteredContent.size();
    }

    @Override
    public void set(T item, int position, FilterableAdapterListener listener) {
        Integer index = filteredContent.get(position);
        origContent.set(index, item);
        listener.onContentChanged();
    }

    @Override
    public void remove(int position, FilterableAdapterListener listener) {
        Integer index = filteredContent.remove(position);
        origContent.remove(index.intValue());
        listener.onContentChanged();
    }
}