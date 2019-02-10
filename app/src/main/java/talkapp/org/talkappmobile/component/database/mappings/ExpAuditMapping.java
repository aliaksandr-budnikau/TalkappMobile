package talkapp.org.talkappmobile.component.database.mappings;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import talkapp.org.talkappmobile.model.ExpActivityType;

@DatabaseTable(tableName = ExpAuditMapping.EXP_AUDIT_TABLE)
public class ExpAuditMapping {
    public static final String EXP_AUDIT_TABLE = "ExpAudit";
    public static final String ID_FN = "id";
    public static final String DATE_FN = "date";
    public static final String EXP_SCORE_FN = "expScore";
    public static final String ACTIVITY_TYPE_FN = "activityType";

    @DatabaseField(generatedId = true, columnName = ID_FN)
    private int id;

    @DatabaseField(canBeNull = false, columnName = DATE_FN, dataType = DataType.DATE_STRING,
            format = "yyyy-MM-dd")
    private Date date;

    @DatabaseField(canBeNull = false, columnName = EXP_SCORE_FN)
    private double expScore;

    @DatabaseField(canBeNull = false, columnName = ACTIVITY_TYPE_FN)
    private ExpActivityType activityType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getExpScore() {
        return expScore;
    }

    public void setExpScore(double expScore) {
        this.expScore = expScore;
    }

    public ExpActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ExpActivityType activityType) {
        this.activityType = activityType;
    }
}