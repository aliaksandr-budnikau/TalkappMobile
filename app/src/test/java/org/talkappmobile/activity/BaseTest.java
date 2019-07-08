package org.talkappmobile.activity;

import org.greenrobot.eventbus.EventBus;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BaseTest {

    <T> T getEM(Class<T> clazz, EventBus eventBus, int times) {
        ArgumentCaptor<T> captor = ArgumentCaptor.forClass(clazz);
        verify(eventBus, times(times)).post(captor.capture());
        reset(eventBus);
        return captor.getValue();
    }

    <T> T getEM(Class<T> clazz, EventBus eventBus) {
        return getEM(clazz, eventBus, 1);
    }
}