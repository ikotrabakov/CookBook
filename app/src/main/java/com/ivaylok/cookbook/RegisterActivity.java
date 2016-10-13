package com.ivaylok.cookbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirm;
    private Button mRegister;
    private CoordinatorLayout coordinatorLayout;

    private ProgressDialog mProgress;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = (EditText) findViewById(R.id.register_email);
        mPassword = (EditText) findViewById(R.id.register_password);
        mConfirm = (EditText) findViewById(R.id.register_confirm);
        mRegister = (Button) findViewById(R.id.register_register);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference("user");

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String confirm = mConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Please enter valid email!", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (TextUtils.isEmpty(password) && mPassword.length() < 6) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Please enter valid password!", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (TextUtils.isEmpty(confirm) || !password.equals(confirm)) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Please confirm your password!", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            mProgress.setMessage("Singing Up...");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        String userId = mAuth.getCurrentUser().getUid();

                        DatabaseReference id = mDataRef.child(userId);
                        id.child("email").setValue(mEmail);

                        mProgress.dismiss();

                        Intent overviewIntent = new Intent(RegisterActivity.this, OverviewActivity.class);
                        overviewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(overviewIntent);
                    }
                }
            });
        }



    }

}
