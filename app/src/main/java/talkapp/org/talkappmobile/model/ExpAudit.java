package talkapp.org.talkappmobile.model;

import android.support.annotation.NonNull;

import java.util.Date;

public class ExpAudit {
    private final int id;
    private final Date date;
    private final double expScore;
    private final ExpActivityType activityType;

    public ExpAudit(int id, @NonNull Date date, double expScore, @NonNull ExpActivityType activityType) {
        this.id = id;
        this.date = date;
        this.expScore = expScore;
        this.activityType = activityType;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public double getExpScore() {
        return expScore;
    }

    public ExpActivityType getActivityType() {
        return activityType;
    }
}