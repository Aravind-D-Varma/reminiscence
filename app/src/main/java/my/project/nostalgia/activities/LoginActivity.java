package my.project.nostalgia.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import my.project.nostalgia.R;
import my.project.nostalgia.supplementary.memoryEvents;

/**
 * Appears when the user visits the app for the first time.<br>
 * Creates a viewPager to show what the user sees and sets it up to its adapter.<br>
 * Uses TabLayout to show dots indicating where the user is in the viewPager.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String SEND_USERNAME = "username";
    public static final String LANGUAGE = "GlobalLanguage";
    public static final String APPLICABLE_EVENTS = "true_events";
    public static final String FIRST_TIME = "first_time";
    private FirebaseAuth mAuth;
    private EditText mEmail, mPassword, mName;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduction_login);

        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);
        mName = findViewById(R.id.login_name);
        Button register = findViewById(R.id.login_register);
        Button forgot = findViewById(R.id.login_forgot);
        Button login = findViewById(R.id.login_button);
        mProgressDialog = new ProgressDialog(this);
        register.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        login.setOnClickListener(v -> Login());
        forgot.setOnClickListener(v -> ForgotPassword());

    }

    private void ForgotPassword() {
        final String email = mEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Enter your email");
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MemoryListActivity.class));
            finish();
        }
    }

    private void Login() {
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Enter your email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Enter your password");
            return;
        }

        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
        mProgressDialog.setCanceledOnTouchOutside(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        mProgressDialog.dismiss();
                        setGeneralInfo(mName.getText().toString(), new memoryEvents(getApplicationContext()).getJoinedEvents());
                        startActivity(new Intent(LoginActivity.this, MemoryListActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Incorrect combination of email and password !"
                                , Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                });
    }

    private void setGeneralInfo(String userName, String combinedEvents) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(SEND_USERNAME, userName);
        editor.putBoolean(FIRST_TIME, true);
        editor.putString(APPLICABLE_EVENTS, combinedEvents);
        editor.apply();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}