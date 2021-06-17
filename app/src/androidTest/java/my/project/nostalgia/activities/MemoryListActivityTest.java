package my.project.nostalgia.activities;

import android.view.Gravity;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Before;
import org.junit.Test;
import my.project.nostalgia.R;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class MemoryListActivityTest {

    @Before
    public void setUp(){
        ActivityScenario.launch(MemoryListActivity.class);
    }

    @Test
    public void test_isNavigationDrawerInView() {

        checkNavigationDrawerOpen();

        testThisViewPresence(R.id.navigation_view);
        testThisViewPresence(R.id.nav_header_imageView);
        testThisViewPresence(R.id.nav_header_textView);
        testThisTextPresence(R.string.all);
        testThisTextPresence(R.string.settings);
    }

    @Test
    public void test_isFragmentinView() {

        testThisViewPresence(R.id.fragment_container);
        testThisViewPresence(R.id.memory_recycler_view);
        testThisViewPresence(R.id.memory_fab);
        testThisViewPresence(R.id.memory_search_menu);

    }

    @Test
    public void test_createsNewViewPagerOfMemories() {

        performClickOnThis(R.id.memory_fab);
        testThisViewPresence(R.id.memory_view_pager);
    }

    @Test
    public void test_goesToSettings() {

        checkNavigationDrawerOpen();
        Espresso.onView(withId(R.id.navigation_view))
                .perform(NavigationViewActions.navigateTo(R.id.user_settings));
        testThisViewPresence(R.id.fragment_container);
    }
    @Test
    public void test_goesToAllEvents() {

        checkNavigationDrawerOpen();
        Espresso.onView(withId(R.id.navigation_view))
                .perform(NavigationViewActions.navigateTo(R.id.all));

        testThisViewPresence(R.id.fragment_container);
        testThisViewPresence(R.id.memory_recycler_view);
        testThisViewPresence(R.id.memory_fab);
        testThisViewPresence(R.id.memory_search_menu);
    }
    private void checkNavigationDrawerOpen() {
        Espresso.onView(withId(R.id.main_drawerLayout))
                .check(ViewAssertions.matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
    }


    private void testThisViewPresence(int viewID) {
        Espresso.onView(withId(viewID)).check(ViewAssertions.matches(isDisplayed()));}
    private void testThisTextPresence(int p) {
        Espresso.onView(ViewMatchers.withText(p)).check(ViewAssertions.matches(isDisplayed()));}
    private void performClickOnThis(int viewID) {
        Espresso.onView(withId(viewID)).perform(click());}

}