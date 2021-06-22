package my.project.nostalgia.activities;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import my.project.nostalgia.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Appears when the user visits the app for the first time.<br>
 * Creates a viewPager to show what the user sees and sets it up to its adapter.<br>
 * Uses TabLayout to show dots indicating where the user is in the viewPager.
 */
public class IntroductionActivity extends AppCompatActivity {

    public static final String FIRST_TIME = "firsttime";
    public static final String USER_ID = "userid";
    public static final String SEND_USERNAME = "username";
    public static final String LANGUAGE = "GlobalLanguage";
    public static final String APPLICABLE_EVENTS = "true_events";

    private FirebaseAuth mAuth;
    public static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduction_login);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
            Toast.makeText(this,"Already Signed in",Toast.LENGTH_SHORT).show();
    }

}