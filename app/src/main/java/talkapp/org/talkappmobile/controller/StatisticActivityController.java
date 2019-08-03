package talkapp.org.talkappmobile.controller;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import talkapp.org.talkappmobile.events.ExpAuditLoadedEM;
import talkapp.org.talkappmobile.events.StatisticActivityCreatedEM;
import talkapp.org.talkappmobile.model.ExpAudit;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.UserExpService;

import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;

public class StatisticActivityController {

    private final EventBus eventBus;
    private final UserExpService userExpService;

    public StatisticActivityController(@NonNull EventBus eventBus, @NonNull ServiceFactory factory) {
        this.eventBus = eventBus;
        this.userExpService = factory.getUserExpService();
    }

    public void handle(StatisticActivityCreatedEM event) {
        List<ExpAudit> allByType = userExpService.findAllByTypeOrderedByDate(WORD_SET_PRACTICE);
        eventBus.post(new ExpAuditLoadedEM(allByType));
    }
}