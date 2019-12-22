package talkapp.org.talkappmobile.language.stemmer;

import android.content.Context;
import android.content.res.AssetManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.language.stemmer.StemmerAdapterImpl.LANGUAGE_DIC_EN_US_AFF;
import static talkapp.org.talkappmobile.language.stemmer.StemmerAdapterImpl.LANGUAGE_DIC_EN_US_DIC;

@RunWith(MockitoJUnitRunner.class)
public class StemmerAdapterImplTest {
    @Mock
    private Context context;
    @Mock
    private AssetManager assetManager;

    @Test
    public void creation() throws IOException {
        when(context.getAssets()).thenReturn(assetManager);
        File affFile = new File("src/main/assets/" + LANGUAGE_DIC_EN_US_AFF);
        when(assetManager.open(LANGUAGE_DIC_EN_US_AFF)).thenReturn(new FileInputStream(affFile));
        File dicFile = new File("src/main/assets/" + LANGUAGE_DIC_EN_US_DIC);
        when(assetManager.open(LANGUAGE_DIC_EN_US_DIC)).thenReturn(new FileInputStream(dicFile));

        StemmerAdapter stemmer = new StemmerAdapterImpl(context);
        assertEquals("house", stemmer.stem("Houses").get(0));
    }
}