package talkapp.org.talkappmobile.activity.interactor;

import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnStatisticActivityListener;
import talkapp.org.talkappmobile.model.ExpAudit;
import talkapp.org.talkappmobile.service.UserExpService;

import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;

public class StatisticActivityInteractor {
    private final UserExpService userExpService;

    public StatisticActivityInteractor(UserExpService userExpService) {
        this.userExpService = userExpService;
    }

    public void loadStat(OnStatisticActivityListener listener) {
        List<ExpAudit> allByType = userExpService.findAllByTypeOrderedByDate(WORD_SET_PRACTICE);
        listener.onExpAuditLoadedEM(allByType);
    }
}