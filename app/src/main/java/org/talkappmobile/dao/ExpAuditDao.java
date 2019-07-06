package org.talkappmobile.dao;

import java.util.Date;
import java.util.List;

import org.talkappmobile.mappings.ExpAuditMapping;

public interface ExpAuditDao {
    List<ExpAuditMapping> findAll();

    void save(ExpAuditMapping mapping);

    ExpAuditMapping findByDateAndActivityType(Date today, String type);
}