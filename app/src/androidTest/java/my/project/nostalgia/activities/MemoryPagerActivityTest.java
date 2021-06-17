package my.project.nostalgia.activities;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Before;
import org.junit.Test;
import my.project.nostalgia.R;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class MemoryPagerActivityTest {

    @Before
    public void setUp(){
        ActivityScenario.launch(MemoryPagerActivity.class);
    }

    @Test
    public void test_isFragmentinView() {
        testThisViewPresence(R.id.memory_view_pager);
    }
    private void testThisViewPresence(int viewID) {
        Espresso.onView(withId(viewID)).check(ViewAssertions.matches(isDisplayed()));}
    private void testThisTextPresence(int p) {
        Espresso.onView(ViewMatchers.withText(p)).check(matches(isDisplayed()));}

}