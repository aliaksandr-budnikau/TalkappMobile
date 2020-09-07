package talkapp.org.talkappmobile.service.impl;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import talkapp.org.talkappmobile.model.ExpAudit;
import talkapp.org.talkappmobile.service.BuildConfig;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.ServiceFactoryImpl;
import talkapp.org.talkappmobile.service.UserExpService;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.junit.Assert.assertEquals;
import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class UserExpServiceImplIntegTest {

    private ServiceFactory serviceFactory;

    @Before
    public void setUp() throws Exception {
        serviceFactory = new ServiceFactoryImpl(RuntimeEnvironment.application);
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Test
    public void increaseForRepetition_checkDuplicationBug() {
        UserExpService userExpService = serviceFactory.getUserExpService();
        double repetition = userExpService.increaseForRepetition(1, WORD_SET_PRACTICE);
        assertEquals(1, repetition, 0);
        repetition = userExpService.increaseForRepetition(1, WORD_SET_PRACTICE);
        assertEquals(1, repetition, 0);
        List<ExpAudit> all = userExpService.findAllByTypeOrderedByDate(WORD_SET_PRACTICE);
        assertEquals(1, all.size());
    }
}