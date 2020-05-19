package talkapp.org.talkappmobile.activity;

import android.content.Intent;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import talkapp.org.talkappmobile.BeanModule;
import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.OriginalTextTextView;
import talkapp.org.talkappmobile.activity.custom.RightAnswerTextView;
import talkapp.org.talkappmobile.presenter.PresenterFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static talkapp.org.talkappmobile.activity.PracticeWordSetFragment.CHEAT_SEND_WRITE_ANSWER;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddingWordSetsTest {

    public static final String ADDING_NEW_WORD_SET_FRAGMENT = "AddingNewWordSetFragment";
    @Rule
    public ActivityTestRule<MainActivity_> mainActivityRule;

    private List<String> originalWordsList;
    private List<String> wordsToBeDisplayed;
    private CountingIdlingResource countingIdlingResource;

    private static ViewAction clickXY(final int x, final int y) {
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }

    @Before
    public void setup() {
        mainActivityRule = new ActivityTestRule<>(MainActivity_.class);

        String targetPhrasalVerb = "look for";
        originalWordsList = asList("solemn", "grip", "wink", "adoption", "Voluntary", targetPhrasalVerb + "|искать", "preamble",
                "conquer", "adore", "deplete", "cease", "ratification");

        wordsToBeDisplayed = new ArrayList<>();
        for (String word : originalWordsList) {
            String[] split = word.split("\\|");
            if (split.length == 2) {
                wordsToBeDisplayed.add(split[0]);
                wordsToBeDisplayed.add(split[0]);
                wordsToBeDisplayed.add(split[1]);
                wordsToBeDisplayed.add(split[1]);
            }
        }
        countingIdlingResource = new CountingIdlingResource("countingIdlingResource");
        BeanModule beanModule = BeanModule.getInstance();
        PresenterFactory origPresenterFactory = beanModule.presenterFactory();
        beanModule.setPresenterFactory(new IdleResourcePresenterFactoryDecorator(origPresenterFactory, countingIdlingResource));
        
        IdlingRegistry.getInstance().register(countingIdlingResource);
    }

    @Test
    public void testCapitalLetterInNewWord() throws InterruptedException {
        MainActivity_ mainActivity = mainActivityRule.launchActivity(new Intent());

        mainActivity.getFragmentManager().beginTransaction().replace(R.id.content_frame, new AddingNewWordSetFragment_(), ADDING_NEW_WORD_SET_FRAGMENT).commit();
        addWords(originalWordsList);
        onView(withId(R.id.buttonSubmit)).perform(scrollTo(), click());
        onView(withId(R.id.pagerTabStrip)).check(matches(isDisplayed())).perform(swipeLeft());

        int iteration_number = 24;
        for (int i = 0; i < iteration_number; i++) {
            onView(withId(R.id.word_set_practise_form)).check(matches(isDisplayed())).perform(swipeUp());
            onView(withId(R.id.answerText)).perform(clearText(), typeText(CHEAT_SEND_WRITE_ANSWER));
            Espresso.closeSoftKeyboard();
            onView(withId(R.id.checkButton)).perform(click());
            onView(withId(R.id.rightAnswer)).check(new ViewAssertion() {
                @Override
                public void check(View view, NoMatchingViewException noViewFoundException) {
                    wordsToBeDisplayed.remove(((RightAnswerTextView) view).getText());
                }
            }).noActivity();
            onView(withId(R.id.originalText)).check(new ViewAssertion() {
                @Override
                public void check(View view, NoMatchingViewException noViewFoundException) {
                    wordsToBeDisplayed.remove(((OriginalTextTextView) view).getText());
                }
            }).noActivity();
            if (i + 1 < iteration_number) {
                onView(withId(R.id.nextButton)).check(matches(isDisplayed())).perform(click());
            } else {
                onView(withId(R.id.closeButton)).check(matches(isDisplayed())).perform(click());
            }
        }
        assertTrue(wordsToBeDisplayed.isEmpty());
    }


    @Test
    public void testAddingOfTwoWordsets() throws InterruptedException {
        MainActivity_ mainActivity = mainActivityRule.launchActivity(new Intent());

        mainActivity.getFragmentManager().beginTransaction().replace(R.id.content_frame, new AddingNewWordSetFragment_(), ADDING_NEW_WORD_SET_FRAGMENT).commit();
        for (int i = 0; i < 2; i++) {
            addWords(originalWordsList);
            onView(withId(R.id.buttonSubmit)).perform(scrollTo(), click());
            pressBack();
        }
    }

    private void addWords(List<String> wordsList) throws InterruptedException {
        onView(withId(R.id.mainForm)).check(matches(isDisplayed()))
                .perform(swipeDown());
        onView(withId(R.id.mainForm)).check(matches(isDisplayed()))
                .perform(swipeDown());
        for (int i = 0; i < wordsList.size(); i++) {
            onView(withId(R.id.wordSetVocabularyView)).perform(actionOnItemAtPosition(i, swipeLeft()));

            onView(withId(R.id.wordSetVocabularyView))
                    .perform(actionOnItemAtPosition(i, clickXY(1310, 0)));

            String word = wordsList.get(i);
            String[] split = word.split("\\|");
            if (split.length == 2) {
                onView(withId(R.id.addEditDialogPhraseBox)).perform(clearText(), typeText(split[0]));
                onView(withId(R.id.addEditDialogTranslationBox)).perform(clearText(), replaceText(split[1]));
            } else {
                onView(withId(R.id.addEditDialogPhraseBox)).perform(clearText(), typeText(word));
            }
            onView(withText(R.string.phrase_translation_input_text_view_popup_button_ok))
                    .inRoot(isDialog())
                    .check(matches(isDisplayed()))
                    .perform(click());

            onView(withId(R.id.wordSetVocabularyView)).check(matches(isDisplayed()))
                    .perform(actionOnItemAtPosition(i, swipeUp()));
        }
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(countingIdlingResource);
    }
}