package talkapp.org.talkappmobile.component.impl;

import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.backend.SentenceService;

public class BackendSentenceProviderStrategy extends SentenceProviderStrategy {
    public BackendSentenceProviderStrategy(SentenceService sentenceService, AuthSign authSign) {
        super(sentenceService, authSign);
    }
}