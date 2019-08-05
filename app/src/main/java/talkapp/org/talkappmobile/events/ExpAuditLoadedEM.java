package talkapp.org.talkappmobile.events;

import android.support.annotation.NonNull;

import java.util.List;

import talkapp.org.talkappmobile.model.ExpAudit;

public class ExpAuditLoadedEM {
    private final List<ExpAudit> wordSetPracticeExp;

    public ExpAuditLoadedEM(@NonNull List<ExpAudit> wordSetPracticeExp) {
        this.wordSetPracticeExp = wordSetPracticeExp;
    }

    public List<ExpAudit> getWordSetPracticeExp() {
        return wordSetPracticeExp;
    }
}