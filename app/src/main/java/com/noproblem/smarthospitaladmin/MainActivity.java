package com.noproblem.smarthospitaladmin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.noproblem.smarthospitaladmin.screen.HospitalActivity;
import com.noproblem.smarthospitaladmin.screen.LoginActivity;

public class MainActivity extends AppCompatActivity {

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (lacksCurrentUser()) {
            startLoginActivity();
            return;
        }

        setContentView(R.layout.activity_main);

        TextView emailTextView = (TextView) findViewById(R.id.text_view_email);
        emailTextView.setText(getUserEmail());

        findViewById(R.id.button_add_hospital).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                startActivity(HospitalActivity.getStartIntent(MainActivity.this));
            }
        });

        findViewById(R.id.button_log_out).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                startLoginActivity();
            }
        });
    }

    boolean lacksCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser() == null;
    }

    String getUserEmail() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    void startLoginActivity() {
        startActivity(LoginActivity.getStartIntent(MainActivity.this));
        finish();
    }
}
