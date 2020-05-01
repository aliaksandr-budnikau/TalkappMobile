package talkapp.org.talkappmobile.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.greenrobot.eventbus.EventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.sql.SQLException;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.component.BeanFactory;
import talkapp.org.talkappmobile.events.UserExpUpdatedEM;
import talkapp.org.talkappmobile.presenter.PresenterFactory;
import talkapp.org.talkappmobile.presenter.PresenterFactoryImpl;
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactoryImpl;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.ServiceFactoryImpl;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class MainActivityTest {

    private MainActivity mainActivity;
    private EventBus eventBus;
    private PackageManager packageManager;
    private PackageInfo packageInfo;
    private TextView applicationVersion;
    private TextView userExp;
    private ServiceFactory serviceFactory;

    @Before
    public void setup() throws SQLException {
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

        RepositoryFactory repositoryFactory = new RepositoryFactoryImpl(RuntimeEnvironment.application);

        serviceFactory = new ServiceFactoryImpl(repositoryFactory);

        PresenterFactory presenterFactory = new PresenterFactoryImpl(serviceFactory);

        new BeanFactory(presenterFactory);

        applicationVersion = mock(TextView.class);
        userExp = mock(TextView.class);
        NavigationView navigationViewMock = mock(NavigationView.class, RETURNS_DEEP_STUBS);
        when(navigationViewMock.getHeaderView(0).findViewById(R.id.applicationVersion)).thenReturn(applicationVersion);
        when(navigationViewMock.getHeaderView(0).findViewById(R.id.userExp)).thenReturn(userExp);
        Whitebox.setInternalState(mainActivity, "navigationView", navigationViewMock);
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Test
    public void test() throws PackageManager.NameNotFoundException, SQLException {
        packageInfo = new PackageInfo();
        packageInfo.versionName = "1.3";
        when(packageManager.getPackageInfo(anyString(), anyInt())).thenReturn(packageInfo);

        serviceFactory.getUserExpService().increaseForRepetition(10, WORD_SET_PRACTICE);
        serviceFactory.getUserExpService().increaseForRepetition(50, WORD_SET_PRACTICE);
        mainActivity.initPresenter();
        verify(applicationVersion).setText("v" + packageInfo.versionName);
        verify(userExp).setText("EXP " + 60.0);
        reset(userExp);
        serviceFactory.getUserExpService().increaseForRepetition(50, WORD_SET_PRACTICE);

        mainActivity.onMessageEvent(new UserExpUpdatedEM(3));
        verify(userExp).setText("EXP " + 110.0);
    }
}