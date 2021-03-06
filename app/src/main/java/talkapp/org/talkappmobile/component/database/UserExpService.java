package talkapp.org.talkappmobile.component.database;

import talkapp.org.talkappmobile.model.ExpActivityType;

public interface UserExpService {
    double getOverallExp();

    double increaseForRepetition(int repetitionCounter, ExpActivityType type);
}