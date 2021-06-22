package my.project.nostalgia.activities;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import my.project.nostalgia.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Appears when the user visits the app for the first time.<br>
 * Creates a viewPager to show what the user sees and sets it up to its adapter.<br>
 * Uses TabLayout to show dots indicating where the user is in the viewPager.
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mEmail, mPassword;
    private Button mLogin, mForgot, mRegister;
    private ProgressDialog mProgressDialog;

    public static final String SEND_USERNAME= "username";
    public static final String LANGUAGE = "GlobalLanguage";
    public static final String APPLICABLE_EVENTS = "true_events";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduction_login);

        mAuth = FirebaseAuth.getInstance();

        mEmail = (EditText) findViewById(R.id.login_email);
        mPassword = (EditText) findViewById(R.id.login_password);
        mRegister = (Button) findViewById(R.id.login_register);
        mForgot = (Button) findViewById(R.id.login_forgot);
        mLogin = (Button) findViewById(R.id.login_button);
        mProgressDialog = new ProgressDialog(this);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        setGeneralInfo("Username","Student Life");

    }
    private void Login() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            mEmail.setError("Enter your email");return;}

        if(TextUtils.isEmpty(password)){
            mPassword.setError("Enter your password");return;}


        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
        mProgressDialog.setCanceledOnTouchOutside(false);

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Successfully logged in."
                            , Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,MemoryListActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, "Incorrect combination of email and password !"
                            , Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }
        });
    }
    private void setGeneralInfo(String userName, String combinedEvents) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(SEND_USERNAME, userName);
        editor.putString(APPLICABLE_EVENTS, combinedEvents);
        editor.apply();
    }
}