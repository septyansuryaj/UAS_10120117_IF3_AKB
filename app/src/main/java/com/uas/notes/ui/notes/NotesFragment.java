/**
 * NIM : 10120074
 * NAMA : Arif Firdaus
 * KELAS : IF-2
 */
package com.uas.notes.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uas.notes.Config;
import com.uas.notes.R;
import com.uas.notes.adapter.NotesCategoryRecycleViewAdapter;
import com.uas.notes.databinding.FragmentNotesBinding;
import com.uas.notes.model.NoteCategory;

import java.util.ArrayList;

public class NotesFragment extends Fragment {

    private FragmentNotesBinding binding;
    private RecyclerView recyclerView;
    private FloatingActionButton bAdd;
    private NotesCategoryRecycleViewAdapter notesView;
    private DatabaseReference DB;
    private FirebaseAuth Auth;
    private ArrayList<NoteCategory> listCategory;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Auth = FirebaseAuth.getInstance();

        // Set component
        recyclerView = root.findViewById(R.id.recycle_note);
        bAdd = root.findViewById(R.id.note_add_btn);

        // Move to add note activity
        bAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddNotesActivity.class);
            intent.putExtra("backTo", "note_category");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getActivity().startActivity(intent);
            getActivity().finish();
        });

        // Get list category
        DB = FirebaseDatabase.getInstance(Config.getDB_URL()).getReference("notes/" + Auth.getUid());
        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listCategory = new ArrayList<>();

                for (DataSnapshot categorySnapshot: dataSnapshot.getChildren()) {
                    long total = categorySnapshot.getChildrenCount();

                    NoteCategory category = new NoteCategory();
                    category.total = String.valueOf(total);
                    category.title = categorySnapshot.getKey();
                    listCategory.add(category);
                }

                notesView = new NotesCategoryRecycleViewAdapter(getActivity(), listCategory);
                recyclerView.setAdapter(notesView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}