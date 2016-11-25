package com.noproblem.smarthospitaladmin.screen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.noproblem.smarthospitaladmin.BaseActivity;
import com.noproblem.smarthospitaladmin.MainActivity;
import com.noproblem.smarthospitaladmin.R;

/**
 * Created by Victor Artemyev on 24/11/2016.
 */

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    EditText mEmailEditText;
    EditText mPasswordEditText;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mEmailEditText = (EditText) findViewById(R.id.edit_text_email);
        mPasswordEditText = (EditText) findViewById(R.id.edit_text_password);

        findViewById(R.id.button_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onSignInButtonClick();
            }
        });

        findViewById(R.id.button_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onSignUpButtonClick();
            }
        });
    }

    private void onSignInButtonClick() {
        sigIn(getEmail(), getPassword());
    }

    void sigIn(String email, String password) {
        if (validateEmailAndPassword(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                            hideProgressDialog();

                            if (task.isSuccessful()) {
                                onAuthSuccess();
                            } else {
                                showToast("Sign In Failed");
                            }
                        }
                    });
        }
    }

    private void onSignUpButtonClick() {
        signUp(getEmail(), getPassword());
    }

    void signUp(String email, String password) {
        if (validateEmailAndPassword(email, password)) {
            showProgressDialog();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                            hideProgressDialog();
                            if (task.isSuccessful()) {
                                onAuthSuccess();
                            } else {
                               showToast("Sign Up Failed");
                            }
                        }
                    });
        }
    }

    String getEmail() {
        return mEmailEditText.getText().toString().trim();
    }

    String getPassword() {
        return mPasswordEditText.getText().toString().trim();
    }

    boolean validateEmailAndPassword(String email, String password) {
        return validateEmail(email) && validatePassword(password);
    }

    boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError("Email is incorrect");
            return false;
        }
        return true;
    }

    boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError("Password is incorrect");
            return false;
        }
        return true;
    }

    void onAuthSuccess() {
        startActivity(MainActivity.getStartIntent(LoginActivity.this));
        finish();
    }

    void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
