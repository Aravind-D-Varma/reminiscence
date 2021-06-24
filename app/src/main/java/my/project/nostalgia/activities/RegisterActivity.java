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

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import my.project.nostalgia.R;
import my.project.nostalgia.supplementary.memoryEvents;

import static my.project.nostalgia.activities.LoginActivity.APPLICABLE_EVENTS;
import static my.project.nostalgia.activities.LoginActivity.FIRST_TIME;
import static my.project.nostalgia.activities.LoginActivity.SEND_USERNAME;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mName, mEmail, mPassword, mPassword2;
    private Button mRegister;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduction_register);

        mAuth = FirebaseAuth.getInstance();
        mName = (EditText) findViewById(R.id.register_name);
        mEmail = (EditText) findViewById(R.id.register_email);
        mPassword = (EditText) findViewById(R.id.register_password);
        mPassword2 = (EditText) findViewById(R.id.register_password_reenter);
        mRegister = (Button) findViewById(R.id.register_button);
        mProgressDialog = new ProgressDialog(this);
        mRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Register();
            }
        });
    }

    private void Register() {
        String name = mName.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String password2 = mPassword2.getText().toString();

        if(TextUtils.isEmpty(name)){
            mName.setError("Enter your Name");return;}

        if(TextUtils.isEmpty(email)){
            mEmail.setError("Enter your email");return;}
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmail.setError("Enter valid email ID");return;}

        if(TextUtils.isEmpty(password)){
            mPassword.setError("Enter your password");return;}
        else if(password.length() < 6){
            mPassword.setError("Password too short/weak");return;}
        if(TextUtils.isEmpty(password2)){
            mPassword.setError("Confirm your password");return;}
        else if(!password.equals(password2)){
            mPassword2.setError("Different Password");return;}

        setGeneralInfo(mName.getText().toString(),new memoryEvents(this).getJoinedEvents());

        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
        mProgressDialog.setCanceledOnTouchOutside(false);

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Successfully registered."
                            , Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this,MemoryListActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Registration Failed!"
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
        editor.putBoolean(FIRST_TIME,true);
        editor.apply();
    }
}
