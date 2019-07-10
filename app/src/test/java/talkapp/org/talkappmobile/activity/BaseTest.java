package talkapp.org.talkappmobile.activity;

import org.greenrobot.eventbus.EventBus;
import org.mockito.ArgumentCaptor;
import org.mockito.verification.VerificationMode;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BaseTest {

    <T> T getEM(Class<T> clazz, EventBus eventBus, int times) {
        return getValue(clazz, eventBus, times(times));
    }

    <T> T getEM(Class<T> clazz, EventBus eventBus) {
        return getValue(clazz, eventBus, atLeastOnce());
    }

    private <T> T getValue(Class<T> clazz, EventBus eventBus, VerificationMode mode) {
        ArgumentCaptor<T> captor = forClass(clazz);
        verify(eventBus, mode).post(captor.capture());
        reset(eventBus);
        return captor.getValue();
    }
}