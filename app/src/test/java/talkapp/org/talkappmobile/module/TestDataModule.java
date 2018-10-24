package talkapp.org.talkappmobile.module;

import talkapp.org.talkappmobile.component.SaveSharedPreference;

import static org.mockito.Mockito.mock;

public class TestDataModule extends DataModule {
    @Override
    public SaveSharedPreference provideSaveSharedPreference() {
        return mock(SaveSharedPreference.class);
    }
}