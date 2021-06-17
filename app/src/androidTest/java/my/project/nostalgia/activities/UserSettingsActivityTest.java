package my.project.nostalgia.activities;
import androidx.preference.Preference;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.PreferenceMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import my.project.nostalgia.R;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.PreferenceMatchers.withKey;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

public class UserSettingsActivityTest {

    @Before
    public void setUp(){
        ActivityScenario.launch(UserSettingsActivity.class);
    }

    @Test
    public void test_isFragmentinView() {

        testThisViewPresence(R.id.fragment_container);
        testThisTextPresence(R.string.settings_name);
        testThisTextPresence(R.string.settings_events);
        testThisTextPresence(R.string.settings_themes);
        testThisTextPresence(R.string.settings_language);
        testThisTextPresence(R.string.settings_feedback);
        testThisTextPresence(R.string.settings_invite);
        testThisTextPresence(R.string.settings_aboutme);
    }

    /*@Test
    public void test_isUserNameChangeable() {

        //onData(allOf(is(instanceOf(Preference.class)), withKey("username"))).check(matches(isDisplayed()));
        //onData(PreferenceMatchers.withKey("username")).perform(click());
        //onData(PreferenceMatchers.withKey("username")).check(matches(isDisplayed()));
    }*/

    private void testThisViewPresence(int viewID) {
        Espresso.onView(withId(viewID)).check(ViewAssertions.matches(isDisplayed()));}
    private void testThisTextPresence(int p) {
        Espresso.onView(ViewMatchers.withText(p)).check(matches(isDisplayed()));}
}