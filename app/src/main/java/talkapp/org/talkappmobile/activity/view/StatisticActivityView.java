package talkapp.org.talkappmobile.activity.view;

import java.util.List;

import talkapp.org.talkappmobile.model.ExpAudit;
import talkapp.org.talkappmobile.model.ExpAuditMonthly;

public interface StatisticActivityView {
    void setMonthlyStat(List<ExpAuditMonthly> stat);

    void setDailyStat(List<ExpAudit> stat);
}