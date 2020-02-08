package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.model.ExpAudit;
import talkapp.org.talkappmobile.repository.ExpAuditRepository;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;

@RunWith(MockitoJUnitRunner.class)
public class UserExpServiceImplTest {
    @Mock
    private ExpAuditRepository expAuditRepository;
    @InjectMocks
    private UserExpServiceImpl userExpService;

    @Test
    public void testFindAllByTypeOrderedByDate_emptyExp() {
        // setup
        Calendar calendar = getCalendarWithoutTime();
        LinkedList<ExpAudit> actual = new LinkedList<>();
        for (int i = 9; i >= 0; i--) {
            calendar.add(Calendar.DATE, -i);
            actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
            calendar.add(Calendar.DATE, i); // return to current day
        }

        // when
        when(expAuditRepository.findAllByType(WORD_SET_PRACTICE))
                .thenReturn(Collections.<ExpAudit>emptyList());
        List<ExpAudit> result = userExpService.findAllByTypeOrderedByDate(WORD_SET_PRACTICE);

        // then
        assertEquals(10, result.size());
        assertEquals(actual, result);

        calendar = getCalendarWithoutTime();
        assertEquals(calendar.getTime(), result.get(result.size() - 1).getDate());
    }

    @NonNull
    private Calendar getCalendarWithoutTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    @Test
    public void testFindAllByTypeOrderedByDate_notEmptyReturn1() {
        // setup
        Calendar calendar = getCalendarWithoutTime();

        LinkedList<ExpAudit> actual = new LinkedList<>();
        actual.add(new ExpAudit(calendar.getTime(), 10, WORD_SET_PRACTICE));

        ExpAudit mapping = new ExpAudit(calendar.getTime(), 10, WORD_SET_PRACTICE);

        // when
        when(expAuditRepository.findAllByType(WORD_SET_PRACTICE))
                .thenReturn(singletonList(mapping));
        List<ExpAudit> result = userExpService.findAllByTypeOrderedByDate(WORD_SET_PRACTICE);

        // then
        assertEquals(1, result.size());
        assertEquals(actual, result);

        calendar = getCalendarWithoutTime();
        assertEquals(calendar.getTime(), result.get(result.size() - 1).getDate());
    }

    @Test
    public void testFindAllByTypeOrderedByDate_notEmptyReturn1ButForYesterday() {
        // setup
        Calendar calendar = getCalendarWithoutTime();
        calendar.add(Calendar.DATE, -1);

        LinkedList<ExpAudit> actual = new LinkedList<>();
        actual.add(new ExpAudit(calendar.getTime(), 10, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));

        calendar.add(Calendar.DATE, -1);
        ExpAudit mapping = new ExpAudit(calendar.getTime(), 10, WORD_SET_PRACTICE);

        // when
        when(expAuditRepository.findAllByType(WORD_SET_PRACTICE))
                .thenReturn(singletonList(mapping));
        List<ExpAudit> result = userExpService.findAllByTypeOrderedByDate(WORD_SET_PRACTICE);

        // then
        assertEquals(2, result.size());
        assertEquals(actual, result);

        calendar = getCalendarWithoutTime();
        assertEquals(calendar.getTime(), result.get(result.size() - 1).getDate());
    }

    @Test
    public void testFindAllByTypeOrderedByDate_notEmptyReturn1ButFor10DaysAgo() {
        // setup
        Calendar calendar = getCalendarWithoutTime();
        calendar.add(Calendar.DATE, -10);

        LinkedList<ExpAudit> actual = new LinkedList<>();
        actual.add(new ExpAudit(calendar.getTime(), 10, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));

        calendar.add(Calendar.DATE, -10);
        ExpAudit mapping = new ExpAudit(calendar.getTime(), 10, WORD_SET_PRACTICE);

        // when
        when(expAuditRepository.findAllByType(WORD_SET_PRACTICE))
                .thenReturn(singletonList(mapping));
        List<ExpAudit> result = userExpService.findAllByTypeOrderedByDate(WORD_SET_PRACTICE);

        // then
        assertEquals(11, result.size());
        assertEquals(actual, result);

        calendar = getCalendarWithoutTime();
        assertEquals(calendar.getTime(), result.get(result.size() - 1).getDate());
    }

    @Test
    public void testFindAllByTypeOrderedByDate_notEmptyReturn1ButFor10DaysAgoAnd5DaysAgo() {
        // setup
        Calendar calendar = getCalendarWithoutTime();
        calendar.add(Calendar.DATE, -10);

        LinkedList<ExpAudit> actual = new LinkedList<>();
        actual.add(new ExpAudit(calendar.getTime(), 10, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 15, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));
        calendar.add(Calendar.DATE, 1);
        actual.add(new ExpAudit(calendar.getTime(), 0, WORD_SET_PRACTICE));

        calendar.add(Calendar.DATE, -10);
        ExpAudit mapping10 = new ExpAudit(calendar.getTime(), 10, WORD_SET_PRACTICE);

        calendar.add(Calendar.DATE, 5);
        ExpAudit mapping5 = new ExpAudit(calendar.getTime(), 15, WORD_SET_PRACTICE);

        // when
        when(expAuditRepository.findAllByType(WORD_SET_PRACTICE)).thenReturn(asList(mapping10, mapping5));
        List<ExpAudit> result = userExpService.findAllByTypeOrderedByDate(WORD_SET_PRACTICE);

        // then
        assertEquals(11, result.size());
        assertEquals(actual, result);
        assertEquals(10, result.get(0).getExpScore(), 0);
        assertEquals(15, result.get(5).getExpScore(), 0);

        calendar = getCalendarWithoutTime();
        assertEquals(calendar.getTime(), result.get(result.size() - 1).getDate());
    }
}