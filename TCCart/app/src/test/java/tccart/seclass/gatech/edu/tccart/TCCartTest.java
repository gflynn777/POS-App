package tccart.seclass.gatech.edu.tccart;

import edu.gatech.seclass.tccart.MainActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.Matchers.*;
import android.test.suitebuilder.annotation.LargeTest;
import static com.checkdroid.crema.EspressoPlus.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TCCartTest {

  @Rule
  public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);

  /**
   * Test for TCCart
   * @author - Mike Ulasewicz 
   * Generated using Barista - http://checkdroid.com/barista
   */
  @Test
  public void test_TCCartTest() {
    onView(withXPath("")).perform(click());
    onView(withXPath("")).perform(click());
    onView(withText("Everett")).perform(click());
    onView(withXPath("/android.widget.FrameLayout/android.widget.EditText")).perform(clearText(), typeText("Everetty"));
    onView(withText("Save")).perform(click());
  }
}
