package talkapp.org.talkappmobile.activity.listener;

import java.util.List;

import talkapp.org.talkappmobile.model.ExpAudit;
import talkapp.org.talkappmobile.model.ExpAuditMonthly;

public interface OnStatisticActivityListener {
    void onMonthlyStatLoaded(List<ExpAuditMonthly> stat);

    void onDailyStatLoaded(List<ExpAudit> stat);
}