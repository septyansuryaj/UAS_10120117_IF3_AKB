/**
 * NIM : 10120117
 * NAMA : Septyan Surya Jatnika
 * KELAS : IF-3
 */

package com.uas.notes.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uas.notes.Config;
import com.uas.notes.MainActivity;
import com.uas.notes.R;
import com.uas.notes.helper.DBHelper;
import com.uas.notes.helper.StringHelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference DB;
    private FirebaseAuth Auth;
    private EditText fEmail;
    private EditText fPassword;
    private Button bSignin;
    private Button bSignup;
    private SignInButton getbSigninGoogle;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create instance firebase
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();
        Auth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("980680677372-ac9pjorhuhf71h37k66kbbhhr3nk1ct6.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);

        // Set component
        fEmail = findViewById(R.id.login_email);
        fPassword = findViewById(R.id.login_password);
        bSignin = findViewById(R.id.login_submit_btn);
        bSignup = findViewById(R.id.login_signup_btn);
        getbSigninGoogle = findViewById(R.id.login_google);


        // Btn on click action
        bSignup.setOnClickListener(this);
        bSignin.setOnClickListener(this);
        getbSigninGoogle.setOnClickListener(this);

        // Check if user is logged in
        if (Auth.getCurrentUser() != null) {
            Toast.makeText(LoginActivity.this, "Already logged in",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    // On click action override
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.login_submit_btn) {
            signIn();
        } else if (i == R.id.login_signup_btn) {
            signUp();
        } else {
            signInGoogle();
        }
    }

    // Text Input Vallidation
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(fEmail.getText().toString())) {
            fEmail.setError("Required");
            result = false;
        } else {
            fEmail.setError(null);
        }

        if (TextUtils.isEmpty(fPassword.getText().toString())) {
            fPassword.setError("Required");
            result = false;
        } else {
            fPassword.setError(null);
        }

        return result;
    }


    // Sign In action
    private void signIn() {
        if (!validateForm()) return;

        String email = fEmail.getText().toString();
        String password = fPassword.getText().toString();

        Auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        onAuthSuccess(task.getResult().getUser());
                    } else {
                        Toast.makeText(LoginActivity.this, "Sign In Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Sign Up action
    private void signUp() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Auth Success
    private void onAuthSuccess(FirebaseUser user) {
        String name = StringHelper.usernameFromEmail(user.getEmail());

        // Create User If Not Exist
        DBHelper.saveUser(DB, user.getUid(), name, user.getEmail());

        // Make alert
        Toast.makeText(LoginActivity.this, "Sign In Success !",
                Toast.LENGTH_SHORT).show();

        // Move to Main Activity
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void signInGoogle() {
        Intent intent = googleSignInClient.getSignInIntent();

        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100) {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            // check condition
            if (signInAccountTask.isSuccessful()) {
                String s = "Google sign in successful";
                displayToast(s);
                try {
                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null initialize auth credential
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        // Check credential
                        Auth.signInWithCredential(authCredential).addOnCompleteListener(this, task -> {
                            // Check condition
                            if (task.isSuccessful()) {
                                onAuthSuccess(task.getResult().getUser());
                                // When task is successful redirect to profile activity display Toast
                                startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                displayToast("Firebase authentication successful");
                            } else {
                                // When task is unsuccessful display Toast
                                displayToast("Authentication Failed :" + task.getException().getMessage());
                            }
                        });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}