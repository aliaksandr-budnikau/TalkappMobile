package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.ExpActivityType;
import talkapp.org.talkappmobile.model.ExpAudit;

public interface UserExpService {
    double getOverallExp();

    double increaseForRepetition(int repetitionCounter, ExpActivityType type);

    List<ExpAudit> findAllByTypeOrderedByDate(ExpActivityType type);
}