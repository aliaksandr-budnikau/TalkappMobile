package talkapp.org.talkappmobile.widget.adapter.filterable;

import java.util.List;

class FilterableAdapterPresenter<T> implements FilterableAdapterListener {
    private final FilterableAdapterInteractor<T> interactor;
    private final FilterableAdapterView view;

    FilterableAdapterPresenter(FilterableAdapterInteractor<T> interactor, FilterableAdapterView view) {
        this.interactor = interactor;
        this.view = view;
    }

    T get(int position) {
        return interactor.get(position);
    }

    List<T> getAll() {
        return interactor.getAll();
    }

    void replaceAll(List<T> list) {
        interactor.replaceAll(list);
    }

    @Override
    public void onContentChanged() {
        view.onContentChanged();
    }

    void filterOut(AbstractFilter<T> filter) {
        interactor.filterOut(filter, this);
    }

    int getSize() {
        return interactor.getSize();
    }

    void setItem(T item, int position) {
        interactor.set(item, position, this);
    }

    public void remove(int position) {
        interactor.remove(position, this);
    }
}