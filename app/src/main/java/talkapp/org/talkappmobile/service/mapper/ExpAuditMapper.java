package talkapp.org.talkappmobile.service.mapper;

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
}