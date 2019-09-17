package talkapp.org.talkappmobile.activity.presenter;

import java.util.Random;

public abstract class PresenterAndInteractorIntegTest {
    public <T> T getRandomEnum(T[] values) {
        Random random = new Random();
        return values[random.nextInt(values.length)];
    }
}