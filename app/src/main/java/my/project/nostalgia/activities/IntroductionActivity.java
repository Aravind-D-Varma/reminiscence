package my.project.nostalgia.activities;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import my.project.nostalgia.R;
import my.project.nostalgia.adapters.IntroductionPagerAdapter;
import my.project.nostalgia.supplementary.transformationViewPager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

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
        setContentView(R.layout.introduction_pages);

        mAuth = FirebaseAuth.getInstance();

        IntroductionPagerAdapter introductionPagerAdapter = new IntroductionPagerAdapter(this);
        ViewPager pager = findViewById(R.id.pager);
        pager.setClipToPadding(false);
        pager.setPadding(20, 100, 20, 100);
        pager.setPageMargin(20);
        pager.setAdapter(introductionPagerAdapter);
        pager.setPageTransformer(false, new transformationViewPager());
        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(pager, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
            Toast.makeText(this,"Already Signed in",Toast.LENGTH_SHORT).show();
    }

}