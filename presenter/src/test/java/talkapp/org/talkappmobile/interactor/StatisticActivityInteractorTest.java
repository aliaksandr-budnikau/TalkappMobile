package talkapp.org.talkappmobile.interactor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import talkapp.org.talkappmobile.listener.OnStatisticActivityListener;
import talkapp.org.talkappmobile.model.ExpActivityType;
import talkapp.org.talkappmobile.model.ExpAudit;
import talkapp.org.talkappmobile.model.ExpAuditMonthly;
import talkapp.org.talkappmobile.service.UserExpService;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;

@RunWith(MockitoJUnitRunner.class)
public class StatisticActivityInteractorTest {
    @Mock
    private OnStatisticActivityListener listener;
    @Mock
    private UserExpService userExpService;
    @InjectMocks
    private StatisticActivityInteractor interactor;

    @Test
    public void testLoadMonthlyStat_noStat() {
        ExpActivityType type = WORD_SET_PRACTICE;

        Mockito.when(userExpService.findAllByTypeOrderedByDate(type)).thenReturn(Collections.<ExpAudit>emptyList());
        interactor.loadMonthlyStat(type, 2019, listener);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(listener).onMonthlyStatLoaded(captor.capture());
        List<ExpAuditMonthly> result = captor.getValue();
        int month = 0;
        for (ExpAuditMonthly expAudit : result) {
            assertNotNull(expAudit);
            assertNotNull(expAudit.getActivityType());
            assertEquals(0, expAudit.getExpScore(), 0);
            assertEquals(month++, expAudit.getMonth(), 0);
            assertEquals(2019, expAudit.getYear(), 0);
        }
    }

    @Test
    public void testLoadDailyStat_noStat() {
        ExpActivityType type = WORD_SET_PRACTICE;

        Mockito.when(userExpService.findAllByTypeOrderedByDate(type)).thenReturn(Collections.<ExpAudit>emptyList());
        interactor.loadDailyStat(type, 2019, 10, listener);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(listener).onDailyStatLoaded(captor.capture());
        List<ExpAudit> result = captor.getValue();
        for (ExpAudit expAudit : result) {
            assertNotNull(expAudit);
            assertNotNull(expAudit.getDate());
            assertNotNull(expAudit.getActivityType());
            assertEquals(0, expAudit.getExpScore(), 0);
        }
    }

    @Test
    public void testLoadDailyStat_oneDayInMiddleOfMonth() {
        ExpActivityType type = WORD_SET_PRACTICE;

        Calendar instance = Calendar.getInstance();
        int dayOfMonth = instance.get(Calendar.DAY_OF_MONTH);
        instance.add(Calendar.DATE, -(dayOfMonth / 2));
        Mockito.when(userExpService.findAllByTypeOrderedByDate(type)).thenReturn(asList(new ExpAudit(instance.getTime(), 20, type)));
        interactor.loadDailyStat(type, instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), listener);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(listener).onDailyStatLoaded(captor.capture());
        List<ExpAudit> result = captor.getValue();
        ExpAudit expAudit = result.get(0);
        assertNotNull(expAudit);
        assertNotNull(expAudit.getDate());
        assertNotNull(expAudit.getActivityType());
        assertEquals(20, expAudit.getExpScore(), 0);
        for (int i = 1; i < result.size(); i++) {
            expAudit = result.get(i);
            assertNotNull(expAudit);
            assertNotNull(expAudit.getDate());
            assertNotNull(expAudit.getActivityType());
            assertEquals(0, expAudit.getExpScore(), 0);
        }
    }
}