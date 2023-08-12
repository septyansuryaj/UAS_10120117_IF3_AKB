/**
 * NIM : 10120117
 * NAMA : Septyan Surya Jatnika
 * KELAS : IF-3
 */

package com.uas.notes.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uas.notes.Config;
import com.uas.notes.R;
import com.uas.notes.helper.DBHelper;
import com.uas.notes.helper.StringHelper;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference DB;
    private FirebaseAuth Auth;
    private EditText fEmail;
    private EditText fPassword;
    private Button bBack;
    private Button bSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Create instance firebase
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();
        Auth = FirebaseAuth.getInstance();

        // Set component
        fEmail = findViewById(R.id.signup_email);
        fPassword = findViewById(R.id.signup_password);
        bBack = findViewById(R.id.signup_back_btn);
        bSignup = findViewById(R.id.signup_btn);

        // Btn on click action
        bBack.setOnClickListener(this);
        bSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signup_back_btn) {
            back();
        } else if (i == R.id.signup_btn) {
            signUp();
        }
    }

    // Back To Login
    private void back() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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

        // Min 6
        if(fPassword.getText().toString().length() < 6) {
            Toast.makeText(RegisterActivity.this, "Password min 6 character",
                    Toast.LENGTH_SHORT).show();
        }

        // Must contain @
        if(!fEmail.getText().toString().contains("@")) {
            Toast.makeText(RegisterActivity.this, "Email does not comply with the conditions",
                    Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    // Register
    private void signUp() {
        if (!validateForm()) return;

        String email = fEmail.getText().toString();
        String password = fPassword.getText().toString();

        Auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()) {
                        onAuthSuccess(task.getResult().getUser());
                    } else {
                        Toast.makeText(RegisterActivity.this, "Sign Up Gagal",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Auth Success
    private void onAuthSuccess(FirebaseUser user) {
        String name = StringHelper.usernameFromEmail(user.getEmail());

        // Create User If Not Exist
        DBHelper.saveUser(DB, user.getUid(), name, user.getEmail());

        // Make alert
        Toast.makeText(RegisterActivity.this, "Sign Up Berhasil !",
                Toast.LENGTH_SHORT).show();

        // Move to Main Activity
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
}