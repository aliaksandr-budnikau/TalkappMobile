package org.talkappmobile.service;

import org.talkappmobile.model.ExpActivityType;

public interface UserExpService {
    double getOverallExp();

    double increaseForRepetition(int repetitionCounter, ExpActivityType type);
}