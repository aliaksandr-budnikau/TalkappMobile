package talkapp.org.talkappmobile.repository;

import talkapp.org.talkappmobile.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.model.ExpActivityType;
import talkapp.org.talkappmobile.model.ExpAudit;

public class ExpAuditMapper {

    public ExpAudit toDto(ExpAuditMapping mapping) {
        return new ExpAudit(
                mapping.getDate(),
                mapping.getExpScore(),
                ExpActivityType.valueOf(mapping.getActivityType()));
    }

    public ExpAuditMapping toMapping(ExpAudit dto) {
        ExpAuditMapping mapping = new ExpAuditMapping();
        mapping.setActivityType(dto.getActivityType().name());
        mapping.setDate(dto.getDate());
        mapping.setExpScore(dto.getExpScore());
        return mapping;
    }
}