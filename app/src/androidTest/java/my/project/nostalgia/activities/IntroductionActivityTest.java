package my.project.nostalgia.activities;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import my.project.nostalgia.R;

@RunWith(AndroidJUnit4ClassRunner.class)
public class IntroductionActivityTest extends TestCase {

    @Test
    public void test_isActivityinView() {
        /*ActivityScenario activityScenario = ActivityScenario.launch(IntroductionActivity.class);
        Espresso.onView(ViewMatchers.withId(R.id.welcome)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));*/
    }
}