package com.codeian.mvvm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;

    private int notePriority;

    EditText editTitle, editDescription;
    ImageButton btnClose;
    NumberPicker np;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.edit_description);
        btnClose = findViewById(R.id.close_btn);

        LinearLayout bottomSheetViewgroup = (LinearLayout) findViewById(R.id.bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetViewgroup);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        FloatingActionButton fab = findViewById(R.id.btn_add_note);

        RecyclerView recyclerView = findViewById(R.id.note_list_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        np = findViewById(R.id.numberPicker);

        np.setMinValue(1);
        np.setMaxValue(10);

        NumberPicker.OnValueChangeListener onValueChangeListener =
                (numberPicker, i, i1) -> notePriority = numberPicker.getValue();

        np.setOnValueChangedListener(onValueChangeListener);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, notes -> adapter.setNotes(notes));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_save, null));
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_note, null));
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        fab.setOnClickListener(view -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                if(saveNote()){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    editTitle.setText("");
                    editDescription.setText("");
                    np.setValue(1);
                }
            }
        });

        btnClose.setOnClickListener(view -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));

    }

    private boolean saveNote(){
        String title = editTitle.getText().toString();
        String description = editDescription.getText().toString();
        int priority = np.getValue();

        if(title.trim().isEmpty() || description.trim().isEmpty()){
            Toast.makeText(this, "Please Insert Title and Description.", Toast.LENGTH_SHORT).show();
        }else{
            Note note = new Note(title,description,priority);
            noteViewModel.insert(note);
            Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
