/**
 * NIM : 10120117
 * NAMA : Septyan Surya Jatnika
 * KELAS : IF-3
 */

package com.uas.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.uas.notes.auth.LoginActivity;


public class SpashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);

        new Handler().postDelayed(() -> {
            // Intent is used to switch from one activity to another.
            Intent i = new Intent(SpashScreenActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i); // invoke the SecondActivity.
            finish(); // the current activity will get finished.
        }, 1500);

    }
}
