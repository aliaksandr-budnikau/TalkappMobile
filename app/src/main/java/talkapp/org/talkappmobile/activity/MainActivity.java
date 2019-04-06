package talkapp.org.talkappmobile.activity;

import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.interactor.MainActivityInteractor;
import talkapp.org.talkappmobile.activity.presenter.MainActivityPresenter;
import talkapp.org.talkappmobile.activity.view.MainActivityView;
import talkapp.org.talkappmobile.component.backend.BackendServerFactory;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.database.ServiceFactory;
import talkapp.org.talkappmobile.component.database.impl.ServiceFactoryBean;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements MainActivityView {
    @Bean(BackendServerFactoryBean.class)
    BackendServerFactory backendServerFactory;
    @Bean(ServiceFactoryBean.class)
    ServiceFactory serviceFactory;

    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.drawer_layout)
    DrawerLayout drawer;
    @ViewById(R.id.nav_view)
    NavigationView navigationView;

    private MainActivityPresenter presenter;

    @AfterViews
    public void init() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        MainActivityInteractor interactor = new MainActivityInteractor(backendServerFactory.get(), serviceFactory.getUserExpService(), getApplicationContext());
        initPresenter(interactor);

        final FragmentManager fragmentManager = getFragmentManager();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                if (id == R.id.default_fragment) {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, new MainActivityDefaultFragment_()).commit();
                } else if (id == R.id.add_new_set) {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, new AddingNewWordSetFragment_()).commit();
                } else if (id == R.id.word_set_practise) {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, new WordSetsListFragment_()).commit();
                } else if (id == R.id.topic_practise) {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, new TopicsFragment_()).commit();
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        fragmentManager.beginTransaction().replace(R.id.content_frame, new MainActivityDefaultFragment_()).commit();
    }

    @Background
    public void initPresenter(MainActivityInteractor interactor) {
        presenter = new MainActivityPresenter(this, interactor);
        presenter.checkServerAvailability();
        presenter.initAppVersion();
        presenter.initYourExp();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    @UiThread
    public void setAppVersion(String packageName) {
        TextView applicationVersion = navigationView.getHeaderView(0).findViewById(R.id.applicationVersion);
        applicationVersion.setText(packageName);
    }

    @Override
    @UiThread
    public void setYourExp(String text) {
        TextView userExp = navigationView.getHeaderView(0).findViewById(R.id.userExp);
        userExp.setText(text);
    }
}
