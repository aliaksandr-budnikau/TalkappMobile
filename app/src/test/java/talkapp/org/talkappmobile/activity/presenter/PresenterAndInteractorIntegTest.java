package talkapp.org.talkappmobile.activity.presenter;

import java.util.Random;

import talkapp.org.talkappmobile.activity.BaseTest;

public abstract class PresenterAndInteractorIntegTest extends BaseTest {
    public <T> T getRandomEnum(T[] values) {
        Random random = new Random();
        return values[random.nextInt(values.length)];
    }
}