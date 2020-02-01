package talkapp.org.talkappmobile.service;

import java.util.Date;
import java.util.List;

import talkapp.org.talkappmobile.model.ExpActivityType;
import talkapp.org.talkappmobile.model.ExpAudit;

public interface ExpAuditRepository {
    double getOverallExp();

    ExpAudit findByDateAndActivityType(Date date, ExpActivityType activityType);

    void createNewOrUpdate(ExpAudit expAudit);

    List<ExpAudit> findAllByType(ExpActivityType activityType);
}