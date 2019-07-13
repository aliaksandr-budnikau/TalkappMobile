package talkapp.org.talkappmobile.activity;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.greenrobot.eventbus.EventBus;
import org.mockito.ArgumentCaptor;
import org.mockito.verification.VerificationMode;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;

import java.sql.SQLException;
import java.util.List;

import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.dao.impl.ExpAuditDaoImpl;
import talkapp.org.talkappmobile.dao.impl.SentenceDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordRepetitionProgressDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordSetDaoImpl;
import talkapp.org.talkappmobile.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class BaseTest {

    private DatabaseHelper databaseHelper;
    private WordSetDao wordSetDao;
    private EventBus eventBus;
    private SentenceDao sentenceDao;
    private ExpAuditDao expAuditDao;
    private WordRepetitionProgressDao wordRepetitionProgressDao;

    protected <T> T getEM(Class<T> clazz, EventBus eventBus, int times) {
        return getValue(clazz, eventBus, times(times));
    }

    protected <T> T getEM(Class<T> clazz, EventBus eventBus) {
        return getValue(clazz, eventBus, atLeastOnce());
    }

    protected <T> T getEM(Class<T> clazz, int times) {
        return getValue(clazz, eventBus, times(times));
    }

    protected <T> T getEM(Class<T> clazz) {
        return getValue(clazz, eventBus, atLeastOnce());
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

    private <T> T getValue(Class<T> clazz, VerificationMode mode) {
        return getValue(clazz, eventBus, mode);
    }

    protected ServiceFactoryBean getServiceFactoryBean() throws SQLException {
        ServiceFactoryBean serviceFactoryBean = new ServiceFactoryBean();
        Whitebox.setInternalState(serviceFactoryBean, "logger", new LoggerBean());
        Whitebox.setInternalState(serviceFactoryBean, "context", mock(Context.class));
        Whitebox.setInternalState(serviceFactoryBean, "wordSetDao", getWordSetDao());
        return serviceFactoryBean;
    }

    protected DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    protected WordSetDao getWordSetDao() throws SQLException {
        if (wordSetDao == null) {
            wordSetDao = new WordSetDaoImpl(getDatabaseHelper().getConnectionSource(), WordSetMapping.class);
        }
        return wordSetDao;
    }

    protected SentenceDao getSentenceDao() throws SQLException {
        if (sentenceDao == null) {
            sentenceDao = new SentenceDaoImpl(getDatabaseHelper().getConnectionSource(), SentenceMapping.class);
        }
        return sentenceDao;
    }

    protected ExpAuditDao getExpAuditDao() throws SQLException {
        if (expAuditDao == null) {
            expAuditDao = new ExpAuditDaoImpl(getDatabaseHelper().getConnectionSource(), ExpAuditMapping.class);
        }
        return expAuditDao;
    }

    protected WordRepetitionProgressDao getWordRepetitionProgressDao() throws SQLException {
        if (wordRepetitionProgressDao == null) {
            wordRepetitionProgressDao = new WordRepetitionProgressDaoImpl(getDatabaseHelper().getConnectionSource(), WordRepetitionProgressMapping.class);
        }
        return wordRepetitionProgressDao;
    }

    protected EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = mock(EventBus.class);
        }
        return eventBus;
    }
}