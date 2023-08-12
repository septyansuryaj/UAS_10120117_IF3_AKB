/**
 * NIM : 10120074
 * NAMA : Arif Firdaus
 * KELAS : IF-2
 */
package com.uas.notes.ui.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uas.notes.Config;
import com.uas.notes.MainActivity;
import com.uas.notes.R;
import com.uas.notes.adapter.NotesRecycleViewAdapter;
import com.uas.notes.model.Note;

import java.util.ArrayList;

public class DetailNotesActivity extends AppCompatActivity {

    private String category;
    private RecyclerView recyclerView;
    private NotesRecycleViewAdapter notesView;
    private FloatingActionButton bBack;
    private FloatingActionButton bAdd;
    private DatabaseReference DB;
    private FirebaseAuth Auth;
    private ArrayList<Note> list_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_notes);
        Intent intent = getIntent();

        Auth = FirebaseAuth.getInstance();

        // Set component
        bAdd = findViewById(R.id.note_detail_add_btn);
        bBack = findViewById(R.id.note_detail_back_btn);
        recyclerView = findViewById(R.id.recycle_note_detail);

        // Set category
        this.category = intent.getStringExtra("category");

        // Set title for this activity
        this.setTitle("Note " + this.category);

        // Move to previous activity
        bBack.setOnClickListener(v -> {
            Intent moveToMain = new Intent(this, MainActivity.class);
            moveToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(moveToMain);
            finish();
        });

        // Add button on click
        bAdd.setOnClickListener(v -> {
            Intent moveToAdd = new Intent(this, AddNotesActivity.class);
            moveToAdd.putExtra("backTo", "note_detail");
            moveToAdd.putExtra("category", this.category);
            moveToAdd.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(moveToAdd);
            finish();
        });

        // Get list category
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("notes/" + Auth.getUid() + "/" + this.category);
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list_note = new ArrayList<>();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Note note = snapshot.getValue(Note.class);
                    list_note.add(note);
                }

                // Set recyclerView
                notesView = new NotesRecycleViewAdapter(DetailNotesActivity.this, list_note, category);
                recyclerView.setAdapter(notesView);
                recyclerView.setLayoutManager(new LinearLayoutManager(DetailNotesActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
            }
        });
    }
}