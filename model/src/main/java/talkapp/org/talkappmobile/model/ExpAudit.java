package talkapp.org.talkappmobile.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

public class ExpAudit {
    private final Date date;
    private final ExpActivityType activityType;
    private double expScore;

    public ExpAudit(Date date, double expScore, ExpActivityType activityType) {
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
                .append(date, expAudit.date)
                .append(activityType, expAudit.activityType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(date)
                .append(activityType)
                .toHashCode();
    }

    public void increaseExpScore(int extraValue) {
        this.expScore += extraValue;
    }
}