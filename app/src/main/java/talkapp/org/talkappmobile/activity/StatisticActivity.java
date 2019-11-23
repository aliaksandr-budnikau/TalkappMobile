package talkapp.org.talkappmobile.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.presenter.StatisticActivityPresenter;
import talkapp.org.talkappmobile.activity.view.StatisticActivityView;
import talkapp.org.talkappmobile.model.ExpAudit;
import talkapp.org.talkappmobile.model.ExpAuditMonthly;

import static com.github.mikephil.charting.components.XAxis.XAxisPosition.TOP;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static talkapp.org.talkappmobile.activity.StatisticActivity.Tab.DAILY;
import static talkapp.org.talkappmobile.activity.StatisticActivity.Tab.MONTHLY;
import static talkapp.org.talkappmobile.activity.StatisticActivity.Tab.TOTAL;
import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;

@EActivity(R.layout.activity_statistic)
public class StatisticActivity extends AppCompatActivity implements StatisticActivityView {

    private final DateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yy");
    @Bean
    PresenterFactory presenterFactory;

    @EventBusGreenRobot
    EventBus eventBus;

    @ViewById(R.id.tabHost)
    TabHost tabHost;

    @ViewById(R.id.barChart)
    BarChart barChart;

    @StringRes(R.string.activity_statistic_tab_today_label)
    String tabTodayLabel;
    @StringRes(R.string.activity_statistic_tab_daily_label)
    String tabDailyLabel;
    @StringRes(R.string.activity_statistic_tab_monthly_label)
    String tabMonthlyLabel;
    @StringRes(R.string.activity_statistic_tab_total_label)
    String tabTotalLabel;

    private StatisticActivityPresenter presenter;
    private ArrayList<Date> dates;

    @AfterViews
    public void init() {
        presenter = presenterFactory.create(this);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                pickStatistic(Tab.valueOf(tabId));
            }
        });
        tabHost.setup();
        //Tab 1
        TabHost.TabSpec spec = tabHost.newTabSpec(Tab.TODAY.name());
        spec.setContent(R.id.barChart);
        spec.setIndicator(tabTodayLabel);
        tabHost.addTab(spec);

        //Tab 2
        spec = tabHost.newTabSpec(DAILY.name());
        spec.setContent(R.id.barChart);
        spec.setIndicator(tabDailyLabel);
        tabHost.addTab(spec);

        //Tab 3
        spec = tabHost.newTabSpec(Tab.MONTHLY.name());
        spec.setContent(R.id.barChart);
        spec.setIndicator(tabMonthlyLabel);
        tabHost.addTab(spec);

        //Tab 4
        spec = tabHost.newTabSpec(TOTAL.name());
        spec.setContent(R.id.barChart);
        spec.setIndicator(tabTotalLabel);
        tabHost.addTab(spec);

        tabHost.setCurrentTabByTag(DAILY.name());
        barChart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        barChart.setDrawBarShadow(false);

        barChart.setDrawGridBackground(false);

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setYOffset(0f);
        l.setXOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(false);
        xAxis.setDrawGridLines(true);
        xAxis.setPosition(TOP);
        xAxis.setDrawAxisLine(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {

            @Override
            public String getFormattedValue(float index) {
                Date date;
                if (dates.size() <= index) {
                    date = new Date();
                } else if (index < 0) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dates.get(0));
                    calendar.add(Calendar.DATE, (int) index);
                    date = calendar.getTime();
                } else {
                    date = dates.get((int) index);
                }
                return DATE_FORMAT.format(date);
            }
        });
        xAxis.setAxisMinimum(-1f); // this replaces setStartAtZero(true)

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setEnabled(true);
        leftAxis.setDrawLabels(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setEnabled(true);
        rightAxis.setDrawLabels(false);
        rightAxis.setAxisMinimum(0f);

        loadDailyStat();
    }

    @Background
    public void loadDailyStat() {
        Calendar calendar = Calendar.getInstance();
        presenter.loadDailyStat(WORD_SET_PRACTICE, calendar.get(YEAR), calendar.get(MONTH));
    }

    @Override
    public void setMonthlyStat(List<ExpAuditMonthly> stat) {

    }

    @Background
    public void pickStatistic(Tab tab) {
        Calendar calendar = Calendar.getInstance();
        if (tab == DAILY) {
            presenter.loadDailyStat(WORD_SET_PRACTICE, calendar.get(YEAR), calendar.get(MONTH));
        } else if (tab == MONTHLY) {
            presenter.loadMonthlyStat(WORD_SET_PRACTICE, calendar.get(YEAR));
        }
    }

    @Override
    @UiThread
    public void setDailyStat(List<ExpAudit> stat) {
        ArrayList<BarEntry> wordSetPracticeValues = new ArrayList<>();

        dates = new ArrayList<>(stat.size());
        for (ExpAudit expAudit : stat) {
            wordSetPracticeValues.add(new BarEntry(dates.size(), (float) expAudit.getExpScore()));
            dates.add(expAudit.getDate());
        }

        BarDataSet set = new BarDataSet(wordSetPracticeValues, WORD_SET_PRACTICE.name());
        set.setColor(Color.rgb(104, 241, 175));

        BarData data = new BarData(set);
        data.setValueFormatter(new LargeValueFormatter());

        barChart.setData(data);
    }

    enum Tab {
        TODAY, DAILY, MONTHLY, TOTAL
    }
}