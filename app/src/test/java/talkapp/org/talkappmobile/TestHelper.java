package talkapp.org.talkappmobile;

import org.greenrobot.eventbus.EventBus;
import org.mockito.ArgumentCaptor;
import org.mockito.verification.VerificationMode;

import java.util.List;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TestHelper {
    private final EventBus eventBus;

    public TestHelper() {
        this.eventBus = mock(EventBus.class);
    }

    public EventBus getEventBusMock() {
        return eventBus;
    }

    private <T> T getValue(Class<T> clazz, EventBus eventBus, VerificationMode mode) {
        ArgumentCaptor<T> captor = forClass(clazz);
        verify(eventBus, mode).post(captor.capture());
        reset(eventBus);
        List<T> allValues = captor.getAllValues();
        for (T arg : allValues) {
            if (arg.getClass().equals(clazz)) {
                return arg;
            }
        }
        return null;
    }

    public <T> T getEM(Class<T> clazz) {
        return getValue(clazz, eventBus, atLeastOnce());
    }

    public <T> T getEM(Class<T> clazz, int times) {
        return getValue(clazz, eventBus, times(times));
    }
}