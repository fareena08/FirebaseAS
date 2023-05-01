package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter recyclerViewAdapter;
    private EditText addTaskBox;
    private Button addTaskButton;
    private List<Task> allTask;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allTask = new ArrayList<Task>();
        //Reference the Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.task_list);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        addTaskBox = findViewById(R.id.et_add_task);
        addTaskButton = findViewById(R.id.btn_add_task);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the text from the EditText
                String enteredTask = addTaskBox.getText().toString();

                //Conditions to check if the EditText is empty/less than six characters
                if(TextUtils.isEmpty(enteredTask)){
                    Toast.makeText(MainActivity.this, "You Need to Input Something", Toast.LENGTH_LONG).show();
                    return;
                }
                else if (enteredTask.length()<6){
                    Toast.makeText(MainActivity.this, "Too Short Please Make It Longer", Toast.LENGTH_SHORT).show();
                }
                else{
                    Task taskObject = new Task(enteredTask);
                    //Push new Data into the Database in Firebase
                    databaseReference.push().setValue(taskObject);
                    addTaskBox.setText("");
                }
            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                getAllTask(dataSnapshot);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                getAllTask(dataSnapshot);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                taskDeletion(dataSnapshot);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAllTask(DataSnapshot dataSnapshot){
        for (DataSnapshot singlesnapshot : dataSnapshot.getChildren()){
            String TaskTitle = singlesnapshot.getValue(String.class);
            allTask.add(new Task(TaskTitle));
            recyclerViewAdapter = new RecyclerViewAdapter(allTask,MainActivity.this);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }

    private void taskDeletion(DataSnapshot dataSnapshot){
        for (DataSnapshot singlesnapshot:dataSnapshot.getChildren()){
            String taskTitle = singlesnapshot.getValue(String.class);
            for (int i=0;i<allTask.size();i++){
                if (allTask.get(i).getTask().equals(taskTitle));{
                    allTask.remove(i);
                }
            }
            Log.d(TAG,"Task Title" + taskTitle);
            recyclerViewAdapter.notifyDataSetChanged();
            recyclerViewAdapter = new RecyclerViewAdapter(allTask,MainActivity.this);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }

}