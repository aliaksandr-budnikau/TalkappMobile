package talkapp.org.talkappmobile.model;

import android.support.annotation.NonNull;

public class ExpAuditMonthly {
    private final int month;
    private final int year;
    private final double expScore;
    private final ExpActivityType activityType;

    public ExpAuditMonthly(int month, int year, double expScore, @NonNull ExpActivityType activityType) {
        this.month = month;
        this.year = year;
        this.expScore = expScore;
        this.activityType = activityType;
    }

    public double getExpScore() {
        return expScore;
    }

    public ExpActivityType getActivityType() {
        return activityType;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}