package talkapp.org.talkappmobile.events;

import android.support.annotation.NonNull;

import java.util.List;

import talkapp.org.talkappmobile.model.ExpAudit;

public class ExpAuditLoadedEM {
    private final List<ExpAudit> expAudits;

    public ExpAuditLoadedEM(@NonNull List<ExpAudit> expAudits) {
        this.expAudits = expAudits;
    }

    public List<ExpAudit> getExpAudits() {
        return expAudits;
    }
}