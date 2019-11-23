package talkapp.org.talkappmobile.activity.listener;

import java.util.List;

import talkapp.org.talkappmobile.model.ExpAudit;

public interface OnStatisticActivityListener {
    void onExpAuditLoadedEM(List<ExpAudit> allByType);
}