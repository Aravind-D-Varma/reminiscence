package my.project.nostalgia.activities;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import org.junit.Test;
import my.project.nostalgia.R;

public class UserSettingsActivityTest {
    @Test
    public void test_isFragmentinView() {
        ActivityScenario.launch(UserSettingsActivity.class);
        Espresso.onView(ViewMatchers.withId(R.id.fragment_container)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}