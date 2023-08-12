/**
 * NIM : 10120074
 * NAMA : Arif Firdaus
 * KELAS : IF-2
 */
package com.uas.notes.ui.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uas.notes.Config;
import com.uas.notes.R;
import com.uas.notes.helper.DBHelper;

public class UpdateNotesActivity extends AppCompatActivity {

    private DatabaseReference DB;
    private EditText fTitle;
    private EditText fCategory;
    private EditText fNote;
    private Button bSave;
    private Button bBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_notes);

        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference();
        Intent i = getIntent();

        // Set component
        fTitle = findViewById(R.id.upt_note_title);
        fCategory = findViewById(R.id.upt_note_category);
        fNote = findViewById(R.id.upt_note_description);
        bSave = findViewById(R.id.upt_note_save_btn);
        bBack = findViewById(R.id.upt_note_back_btn);

        // Set value
        fTitle.setText(i.getStringExtra("title"));
        fCategory.setText(i.getStringExtra("category"));
        fNote.setText(i.getStringExtra("description"));

        // Save button on click
        bSave.setOnClickListener(v -> {
            DBHelper.deleteNote(DB,
                    i.getStringExtra("userId"),
                    i.getStringExtra("category"),
                    i.getStringExtra("title")
            );
            DBHelper.saveNotes(DB,
                    i.getStringExtra("userId"),
                    fTitle.getText().toString(),
                    fCategory.getText().toString(),
                    fNote.getText().toString()
            );

            // Make alert
            Toast.makeText(this, "Data created !",
                    Toast.LENGTH_SHORT).show();

            goToNoteDetail(i.getStringExtra("category"));
        });


        // Back button on click
        bBack.setOnClickListener(v -> {
            goToNoteDetail(i.getStringExtra("category"));
        });
    }

    public void goToNoteDetail(String category) {
        Intent intent = new Intent(this, DetailNotesActivity.class);
        intent.putExtra("category", category);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}