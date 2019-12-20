package talkapp.org.talkappmobile.widget.adapter.filterable;

import java.util.List;

interface FilterableAdapterInteractor<T> {

    T get(int position);

    List<T> getAll();

    void replaceAll(List<T> list);

    void filterOut(AbstractFilter<T> filter, FilterableAdapterListener listener);

    int getSize();

    void set(T item, int position, FilterableAdapterListener listener);

    void remove(int position, FilterableAdapterListener listener);
}