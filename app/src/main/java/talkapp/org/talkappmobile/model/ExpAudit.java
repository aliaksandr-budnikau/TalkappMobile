package talkapp.org.talkappmobile.model;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

public class ExpAudit {
    private final Date date;
    private final double expScore;
    private final ExpActivityType activityType;

    public ExpAudit(@NonNull Date date, double expScore, @NonNull ExpActivityType activityType) {
        this.date = date;
        this.expScore = expScore;
        this.activityType = activityType;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpAudit expAudit = (ExpAudit) o;
        return Double.compare(expAudit.expScore, expScore) == 0 &&
                Objects.equals(date, expAudit.date) &&
                activityType == expAudit.activityType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, expScore, activityType);
    }
}