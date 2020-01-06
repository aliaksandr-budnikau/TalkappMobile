package talkapp.org.talkappmobile.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.design.widget.NavigationView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.greenrobot.eventbus.EventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.Date;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.DaoHelper;
import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.events.UserExpUpdatedEM;
import talkapp.org.talkappmobile.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.TopicServiceImpl;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.RequestExecutor;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.service.impl.UserExpServiceImpl;
import talkapp.org.talkappmobile.service.mapper.ExpAuditMapper;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class MainActivityTest {

    private MainActivity mainActivity;
    private EventBus eventBus;
    private PackageManager packageManager;
    private PackageInfo packageInfo;
    private TextView applicationVersion;
    private TextView userExp;
    private DaoHelper daoHelper;

    @Before
    public void setup() throws SQLException {
        daoHelper = new DaoHelper();
        LoggerBean logger = new LoggerBean();
        ObjectMapper mapper = new ObjectMapper();
        packageManager = mock(PackageManager.class);
        mainActivity = new MainActivity() {
            @Override
            public Context getApplicationContext() {
                Context context = mock(Context.class);
                when(context.getPackageManager()).thenReturn(packageManager);
                return context;
            }
        };
        eventBus = mock(EventBus.class);
        Whitebox.setInternalState(mainActivity, "eventBus", eventBus);

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", logger);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());

        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);

        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        when(mockServiceFactoryBean.getUserExpService()).thenReturn(new UserExpServiceImpl(daoHelper.getExpAuditDao(), mock(ExpAuditMapper.class)));
        TopicServiceImpl localDataService = new TopicServiceImpl(mock(TopicDao.class));
        when(mockServiceFactoryBean.getTopicService()).thenReturn(localDataService);

        PresenterFactory presenterFactory = new PresenterFactory();
        Whitebox.setInternalState(presenterFactory, "backendServerFactory", factory);
        Whitebox.setInternalState(presenterFactory, "serviceFactory", mockServiceFactoryBean);

        Whitebox.setInternalState(mainActivity, "presenterFactory", presenterFactory);

        applicationVersion = mock(TextView.class);
        userExp = mock(TextView.class);
        NavigationView navigationViewMock = mock(NavigationView.class, RETURNS_DEEP_STUBS);
        when(navigationViewMock.getHeaderView(0).findViewById(R.id.applicationVersion)).thenReturn(applicationVersion);
        when(navigationViewMock.getHeaderView(0).findViewById(R.id.userExp)).thenReturn(userExp);
        Whitebox.setInternalState(mainActivity, "navigationView", navigationViewMock);
    }

    @After
    public void tearDown() {
        daoHelper.releaseHelper();
    }

    @Test
    public void test() throws PackageManager.NameNotFoundException, SQLException {
        packageInfo = new PackageInfo();
        packageInfo.versionName = "1.3";
        when(packageManager.getPackageInfo(anyString(), anyInt())).thenReturn(packageInfo);

        ExpAuditMapping mapping = new ExpAuditMapping();
        mapping.setExpScore(10);
        mapping.setDate(new Date(3));
        mapping.setActivityType("TYPE");
        daoHelper.getExpAuditDao().save(mapping);
        mapping = new ExpAuditMapping();
        mapping.setExpScore(50);
        mapping.setDate(new Date(3));
        mapping.setActivityType("TYPE");
        daoHelper.getExpAuditDao().save(mapping);

        mainActivity.initPresenter();
        verify(applicationVersion).setText("v" + packageInfo.versionName);
        verify(userExp).setText("EXP " + 60.0);
        reset(userExp);

        mapping = new ExpAuditMapping();
        mapping.setExpScore(50);
        mapping.setDate(new Date(3));
        mapping.setActivityType("TYPE");
        daoHelper.getExpAuditDao().save(mapping);

        mainActivity.onMessageEvent(new UserExpUpdatedEM(3));
        verify(userExp).setText("EXP " + 110.0);
    }
}