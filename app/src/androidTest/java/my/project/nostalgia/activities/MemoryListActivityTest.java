package my.project.nostalgia.activities;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import org.junit.Test;
import my.project.nostalgia.R;

public class MemoryListActivityTest {
    @Test
    public void test_isFragmentinView() {
        ActivityScenario.launch(MemoryListActivity.class);
        Espresso.onView(ViewMatchers.withId(R.id.fragment_container)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.memory_recycler_view)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.memory_fab)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}