package talkapp.org.talkappmobile.component.database.dao;

import java.util.List;

import talkapp.org.talkappmobile.component.database.mappings.ExpAuditMapping;

public interface ExpAuditDao {
    List<ExpAuditMapping> findAll();
}