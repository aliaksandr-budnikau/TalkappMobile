package talkapp.org.talkappmobile.model;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;
import java.util.Objects;

public class ExpAudit {
    private final Date date;
    private final ExpActivityType activityType;
    private double expScore;

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

        return new EqualsBuilder()
                .append(expScore, expAudit.expScore)
                .append(date, expAudit.date)
                .append(activityType, expAudit.activityType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(date)
                .append(activityType)
                .append(expScore)
                .toHashCode();
    }

    public void increaseExpScore(int extraValue) {
        this.expScore += extraValue;
    }
}