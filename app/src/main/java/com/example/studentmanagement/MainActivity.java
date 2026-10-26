package com.example.studentmanagement;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText etName, etStudentId;
    Spinner spMajor;
    Button btnAdd, btnView, btnEdit, btnDelete;
    ListView lvStudents;

    DatabaseHelper db;
    ArrayList<String> studentList;
    ArrayList<String> studentIds;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());

                    v.setPadding(systemBars.left, systemBars.top,
                            systemBars.right, systemBars.bottom);
                    return insets;
                });

        etName = findViewById(R.id.etName);
        etStudentId = findViewById(R.id.etStudentId);
        spMajor = findViewById(R.id.spMajor);

        btnAdd = findViewById(R.id.btnAdd);
        btnView = findViewById(R.id.btnView);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        lvStudents = findViewById(R.id.lvStudents);

        db = new DatabaseHelper(this);

        studentList = new ArrayList<>();
        studentIds = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                studentList);

        lvStudents.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> addStudent());
        btnView.setOnClickListener(v -> viewStudents());
        btnEdit.setOnClickListener(v -> editStudent());
        btnDelete.setOnClickListener(v -> deleteStudent());

        lvStudents.setOnItemClickListener((parent, view,
                                           position, id) -> {
            etStudentId.setText(studentIds.get(position));

            try (Cursor cursor = db.getAllStudents()) {
                while (cursor.moveToNext()) {
                    if (cursor.getString(0).equals(
                            studentIds.get(position))) {

                        etName.setText(cursor.getString(1));

                        String major = cursor.getString(2);
                        int spinnerPosition =
                                adapterPositionForMajor(major);

                        spMajor.setSelection(spinnerPosition);
                        break;
                    }
                }
            }
        });

        viewStudents();
    }

    private int adapterPositionForMajor(String major) {
        ArrayAdapter<String> majorAdapter =
                (ArrayAdapter<String>) spMajor.getAdapter();

        int position = majorAdapter.getPosition(major);
        return Math.max(position, 0);
    }

    private void addStudent() {
        String id = etStudentId.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String major = spMajor.getSelectedItem().toString();

        if (id.isEmpty() || name.isEmpty()) {
            showMessage("Please fill in all fields");
            return;
        }

        if (db.addStudent(id, name, major)) {
            showMessage("Student added successfully");
            clearFields();
            viewStudents();
        } else {
            showMessage("Failed: Student ID may already exist");
        }
    }

    private void viewStudents() {
        studentList.clear();
        studentIds.clear();

        try (Cursor cursor = db.getAllStudents()) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String major = cursor.getString(2);

                studentIds.add(id);
                studentList.add(id + " | " + name + " | " + major);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void editStudent() {
        String id = etStudentId.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String major = spMajor.getSelectedItem().toString();

        if (id.isEmpty() || name.isEmpty()) {
            showMessage("Select a student and fill in the fields");
            return;
        }

        if (db.editStudent(id, name, major)) {
            showMessage("Student updated successfully");
            clearFields();
            viewStudents();
        } else {
            showMessage("Student not found");
        }
    }

    private void deleteStudent() {
        String id = etStudentId.getText().toString().trim();

        if (id.isEmpty()) {
            showMessage("Select a student first");
            return;
        }

        if (db.deleteStudent(id)) {
            showMessage("Student deleted successfully");
            clearFields();
            viewStudents();
        } else {
            showMessage("Student not found");
        }
    }

    private void clearFields() {
        etName.setText("");
        etStudentId.setText("");
        spMajor.setSelection(0);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}