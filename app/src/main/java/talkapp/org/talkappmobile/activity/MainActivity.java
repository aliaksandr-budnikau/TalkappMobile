package talkapp.org.talkappmobile.activity;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.presenter.MainActivityPresenter;
import talkapp.org.talkappmobile.activity.view.MainActivityView;
import talkapp.org.talkappmobile.events.UserExpUpdatedEM;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.service.WordSetQRImporter;
import talkapp.org.talkappmobile.service.impl.WordSetQRImporterBean;

import static talkapp.org.talkappmobile.activity.FragmentFactory.createWordSetsListFragment;
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.REPETITION_CLASS_MAPPING;
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.REPETITION_MODE_MAPPING;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements MainActivityView {
    @Bean
    PresenterFactory presenterFactory;
    @Bean(WordSetQRImporterBean.class)
    WordSetQRImporter wordSetQRImporter;
    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.drawer_layout)
    DrawerLayout drawer;
    @ViewById(R.id.nav_view)
    NavigationView navigationView;
    @StringRes(R.string.menu_exercises_learn_words_option_add_new_word_set)
    String optionAddNewWordSet;
    @StringRes(R.string.menu_exercises_learn_words_option_add_new_word_set_by_qrc)
    String optionAddNewWordSetByQRC;
    @StringRes(R.string.menu_exercises_learn_words_option_open_custom_word_sets)
    String optionOpenCustomWordSets;
    @StringRes(R.string.menu_exercises_learn_words_option_open_word_sets)
    String optionOpenWordSet;
    @StringRes(R.string.menu_exercises_learn_words_option_open_word_sets_by_topics)
    String optionOpenWordSetsByTopics;

    @EventBusGreenRobot
    EventBus eventBus;

    private MainActivityPresenter presenter;

    @AfterViews
    public void init() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        initPresenter();


        TextView userExp = navigationView.getHeaderView(0).findViewById(R.id.userExp);
        userExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StatisticActivity_.class);
                startActivity(intent);
            }
        });

        final FragmentManager fragmentManager = getFragmentManager();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                if (id == R.id.default_fragment) {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, new MainActivityDefaultFragment_()).commit();
                } else if (id == R.id.word_set_practise_rep) {
                    Bundle args = new Bundle();
                    args.putBoolean(REPETITION_MODE_MAPPING, true);
                    args.putSerializable(REPETITION_CLASS_MAPPING, RepetitionClass.NEW);
                    WordSetsListFragment fragment = new WordSetsListFragment_();
                    fragment.setArguments(args);
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                } else if (id == R.id.word_set_practise) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder
                            .setItems((new String[]{optionAddNewWordSet, optionAddNewWordSetByQRC, optionOpenCustomWordSets, optionOpenWordSet, optionOpenWordSetsByTopics}), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        fragmentManager.beginTransaction().replace(R.id.content_frame, new AddingNewWordSetFragment_()).commit();
                                    } else if (which == 1) {
                                        wordSetQRImporter.startScanActivity(MainActivity.this);
                                    } else if (which == 2) {
                                        Topic topic = new Topic();
                                        topic.setId(43);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.content_frame, createWordSetsListFragment(topic)).commit();
                                    } else if (which == 3) {
                                        fragmentManager.beginTransaction().replace(R.id.content_frame, new WordSetsListFragment_()).commit();
                                    } else if (which == 4) {
                                        fragmentManager.beginTransaction().replace(R.id.content_frame, new TopicsFragment_()).commit();
                                    }
                                    dialog.cancel();
                                }
                            });
                    builder.create().show();
                } else if (id == R.id.statistic_activity) {
                    Intent intent = new Intent(MainActivity.this, StatisticActivity_.class);
                    startActivity(intent);
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        fragmentManager.beginTransaction().replace(R.id.content_frame, new MainActivityDefaultFragment_()).commit();
    }

    @Background
    public void initPresenter() {
        presenter = presenterFactory.create(this, getApplicationContext());
        presenter.checkServerAvailability();
        presenter.initAppVersion();
        presenter.initYourExp();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(UserExpUpdatedEM event) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        wordSetQRImporter.onActivityResult(this, requestCode, resultCode, data);
    }
}
