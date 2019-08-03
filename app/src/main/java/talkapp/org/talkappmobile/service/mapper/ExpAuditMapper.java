package talkapp.org.talkappmobile.service.mapper;

import talkapp.org.talkappmobile.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.model.ExpActivityType;
import talkapp.org.talkappmobile.model.ExpAudit;

public class ExpAuditMapper {
    public ExpAuditMapping toMapping(ExpAudit expAudit) {
        ExpAuditMapping mapping = new ExpAuditMapping();
        mapping.setId(expAudit.getId());
        mapping.setExpScore(expAudit.getExpScore());
        mapping.setDate(expAudit.getDate());
        mapping.setActivityType(expAudit.getActivityType().name());
        return mapping;
    }

    public ExpAudit toDto(ExpAuditMapping mapping) {
        ExpAudit expAudit = new ExpAudit(
                mapping.getId(),
                mapping.getDate(),
                mapping.getExpScore(),
                ExpActivityType.valueOf(mapping.getActivityType()));
        return expAudit;
    }
}