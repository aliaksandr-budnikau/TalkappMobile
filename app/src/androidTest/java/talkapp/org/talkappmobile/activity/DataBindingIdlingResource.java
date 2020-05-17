package talkapp.org.talkappmobile.activity;

import androidx.test.espresso.IdlingResource;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DataBindingIdlingResource implements IdlingResource {
    private String id = UUID.randomUUID().toString();
    private List<ResourceCallback> callbacks = new LinkedList<>();

    @Override
    public String getName() {
        return "DataBinding " + id;
    }

    @Override
    public boolean isIdleNow() {
        return false;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        callbacks.add(callback);
    }
}
