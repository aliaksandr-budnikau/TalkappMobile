package talkapp.org.talkappmobile;

import android.content.Context;

import org.powermock.reflect.Whitebox;

import java.sql.SQLException;

import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

import static org.mockito.Mockito.mock;

public class ServiceHelper {
    private final DaoHelper daoHelper;
    private ServiceFactoryBean serviceFactoryBean;

    public ServiceHelper(DaoHelper daoHelper) {
        this.daoHelper = daoHelper;
    }

    public synchronized ServiceFactoryBean getServiceFactoryBean() throws SQLException {
        if (serviceFactoryBean == null) {
            serviceFactoryBean = new ServiceFactoryBean();
            serviceFactoryBean.setContext(mock(Context.class));
            Whitebox.setInternalState(serviceFactoryBean, "wordSetDao", daoHelper.getWordSetDao());
            Whitebox.setInternalState(serviceFactoryBean, "newWordSetDraftDao", daoHelper.getNewWordSetDraftDao());
            Whitebox.setInternalState(serviceFactoryBean, "databaseHelper", daoHelper.getDatabaseHelper());
        }
        return serviceFactoryBean;
    }
}