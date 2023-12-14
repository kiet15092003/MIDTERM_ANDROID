package com.example.androidmidterm_firebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.buildtools.utils.FileUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;


public class StudentListActivity extends AppCompatActivity {
    RecyclerView rv_student;
    Toolbar toolbar;
    EditText edtSearch;
    TextView txtbyName;
    TextView txtbyClass;
    TextView txtbyScore;
    TextView txtbyAge;
    Spinner spnsort;
    Button btnImportStudent;
    Button btnExportStudent;
    private static  int searchClick = 0;
    private static  int nameClick = 1;
    private static  int classClick = 0;
    private static  int ageClick = 0;
    private static  int scoreClick = 0;
    private static  String spinnervalue = "None";
    private static final int REQUEST_CODE_DETAIL = 1;
    private static final int REQUEST_CODE_EDIT = 2;
    private static final int PICK_FILE_REQUEST_CODE = 123;
    private List<StudentModel> studentList = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        rv_student = findViewById(R.id.rv_student);
        edtSearch = findViewById(R.id.studentsearch);
        txtbyName = findViewById(R.id.byname);
        importFileStudent();
        exportFileStudent();
        txtbyName.setTextColor(Color.parseColor("#4E4953"));
        setItemForSniper();
        LoadToolBar();
        //LoadRecyclerView();
        //search();

    }
    public void importFileStudent(){
        btnImportStudent = findViewById(R.id.btnImportStudent);
        btnImportStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFile();
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void exportToCsv() {


        String folderName = "Student"; // Tên thư mục bạn muốn tạo
        String fileName = "file.csv";

        // Sử dụng DIRECTORY_DOCUMENTS thay vì DIRECTORY_DOWNLOADS
        File appDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), folderName);

        if (!appDirectory.exists()) {
            appDirectory.mkdirs();
            //Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
            // Sử dụng mkdirs() để tạo cả thư mục cha nếu cần
        }
        Toast.makeText(this, "Export successfully", Toast.LENGTH_SHORT).show();

        String filePath = appDirectory.getPath() + "/" + fileName;

        List<StudentModel> students = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("students");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    StudentModel student = userSnapshot.getValue(StudentModel.class);
                    students.add(student);
                }

                try (CSVWriter writer = new CSVWriter(new FileWriter(new File(filePath)))) {
                    // Header
                    String[] header = {"Name", "Age", "Score", "Class", "Address"};
                    writer.writeNext(header);

                    // Data
                    for (StudentModel student : students) {
                        String[] data = {
                                student.getName(),
                                String.valueOf(student.getAge()),
                                String.valueOf(student.getScore()),
                                student.getClassName(),
                                student.getAddress()
                        };
                        writer.writeNext(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });





    }
    public void exportFileStudent(){
        btnExportStudent = findViewById(R.id.btnExportStudent);

        btnExportStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportToCsv();
            }
        });
    }
    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // Specify the mime type of Excel files if needed.
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }


    public void onByNameClick(View view) {
        txtbyName = findViewById(R.id.byname);
        searchClick = 0;
        if(nameClick == 0){
            nameClick =1;

            txtbyName.setTextColor(Color.parseColor("#4E4953"));
        }
        else {
            nameClick =0;
            txtbyName.setTextColor(Color.parseColor("#000000"));
        }
        // Code to be executed when byNameTextView is clicked


    }
    public void onByClassClick(View view) {
        txtbyClass = findViewById(R.id.byclass);
        searchClick = 1;
        if(classClick == 0){
            classClick = 1;
            txtbyClass.setTextColor(Color.parseColor("#4E4953"));
        }
        else{
            classClick =0;
            txtbyClass.setTextColor(Color.parseColor("#000000"));
        }
        // Code to be executed when byNameTextView is clicked


    }
    public void onByScoreClick(View view) {
        txtbyScore = findViewById(R.id.byscore);

        searchClick = 3;
        if(scoreClick == 0){
            scoreClick =1;
            txtbyScore.setTextColor(Color.parseColor("#4E4953"));
        }
        else {
            scoreClick =0;
            txtbyScore.setTextColor(Color.parseColor("#000000"));
        }
        // Code to be executed when byNameTextView is clicked


    }
    public void onByAgeClick(View view) {
        txtbyAge = findViewById(R.id.byage);
        searchClick = 2;
        if (ageClick == 0){
            ageClick =1;

            txtbyAge.setTextColor(Color.parseColor("#000000"));

        }
        else {
            ageClick =0;
            txtbyAge.setTextColor(Color.parseColor("#4E4953"));
        }
        // Code to be executed when byNameTextView is clicked


    }
    public void setColorForSearchOption(){

        if(searchClick == 0){
            txtbyName = findViewById(R.id.byname);
            txtbyName.setTextColor(Color.parseColor("#4E4953"));

            txtbyClass = findViewById(R.id.byclass);
            txtbyClass.setTextColor(Color.parseColor("#000000"));

            txtbyScore = findViewById(R.id.byscore);
            txtbyScore.setTextColor(Color.parseColor("#000000"));

            txtbyAge = findViewById(R.id.byage);
            txtbyAge.setTextColor(Color.parseColor("#000000"));
        }
        else if(searchClick == 1) {
            txtbyName = findViewById(R.id.byname);
            txtbyName.setTextColor(Color.parseColor("#000000"));

            txtbyClass = findViewById(R.id.byclass);
            txtbyClass.setTextColor(Color.parseColor("#4E4953"));

            txtbyScore = findViewById(R.id.byscore);
            txtbyScore.setTextColor(Color.parseColor("#000000"));

            txtbyAge = findViewById(R.id.byage);
            txtbyAge.setTextColor(Color.parseColor("#000000"));
        }
        else if(searchClick == 2){
            txtbyName = findViewById(R.id.byname);
            txtbyName.setTextColor(Color.parseColor("#000000"));

            txtbyClass = findViewById(R.id.byclass);
            txtbyClass.setTextColor(Color.parseColor("#000000"));

            txtbyScore = findViewById(R.id.byscore);
            txtbyScore.setTextColor(Color.parseColor("#000000"));

            txtbyAge = findViewById(R.id.byage);
            txtbyAge.setTextColor(Color.parseColor("#4E4953"));
        }
        else {
            txtbyName = findViewById(R.id.byname);
            txtbyName.setTextColor(Color.parseColor("#000000"));

            txtbyClass = findViewById(R.id.byclass);
            txtbyClass.setTextColor(Color.parseColor("#000000"));

            txtbyScore = findViewById(R.id.byscore);
            txtbyScore.setTextColor(Color.parseColor("#4E4953"));

            txtbyAge = findViewById(R.id.byage);
            txtbyAge.setTextColor(Color.parseColor("#000000"));
        }
    }

    public String setItemForSniper(){

        spnsort = findViewById(R.id.snpsort);
        String[] arraySpinner = new String[] {
                "None", "Name", "Age", "Score", "Class"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        spnsort.setAdapter(adapter);

        spnsort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item change here
                String selectedValue = spnsort.getSelectedItem().toString();
               spinnervalue = selectedValue;
               LoadRecyclerView(selectedValue);
               search(selectedValue);
               //LoadRecyclerView(selectedValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here, as it's optional
            }
        });

        String selectedVal = spnsort.getSelectedItem().toString();
        return  selectedVal;
    }


    private void setupEditTextClickListener() {
        edtSearch = findViewById(R.id.studentsearch);

        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to search functionality when the EditText is clicked

                //search();
            }
        });
    }

    public void LoadRecyclerView(String setItemForSniper){
        //Load users to recycler view
        //Toast.makeText(this, setItemForSniper(), Toast.LENGTH_SHORT).show();
        rv_student = findViewById(R.id.rv_student);
        rv_student.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("students");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<StudentModel> students = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    StudentModel student = userSnapshot.getValue(StudentModel.class);
                    students.add(student);
                }


                if(setItemForSniper.equals("Name")){
                    Collections.sort(students, new StudentNameComparator());
                }
                else if(setItemForSniper.equals("Age")){
                    Collections.sort(students,new StudentAgeComparator());
                }
                else if(setItemForSniper.equals("Class")){
                    Collections.sort(students,new StudentClassComparator());
                }
                else if(setItemForSniper.equals("Score")){
                    Collections.sort(students,new StudentScoreComparator());
                }

               StudentAdapter adapter = new StudentAdapter(StudentListActivity.this,students);
                rv_student.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if the result is for the correct request code
        if (requestCode == REQUEST_CODE_DETAIL) {
            // Check if the operation was successful
            if (resultCode == RESULT_OK) {
                // Retrieve the result data from the Intent
                String resultData = data.getStringExtra("result_key");
                // Do something with the result data
            } else {
                // Handle the case where the operation was not successful
            }
        } else if (resultCode == REQUEST_CODE_EDIT) {
            if (resultCode == RESULT_OK) {
                // Retrieve the result data from the Intent
                String resultData = data.getStringExtra("result_key");
                // Do something with the result data
            } else {
                // Handle the case where the operation was not successful
            }
        }

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                if (uri != null) {


                    try {
                        uploadFileToDirectory(uri);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    // `uri` is null, handle this case appropriately
                    Toast.makeText(this, "Invalid file URI", Toast.LENGTH_SHORT).show();
                }
                //processExcelFile(uri);
            }
        }
    }

    private void uploadFileToDirectory(Uri uri) throws IOException {
        try {

            InputStream inputStream = getContentResolver().openInputStream(uri);
            File directory = new File(getFilesDir(), "uploads");
            String fileName = getFileName(uri);
            // Create the directory if it doesn't exist
            if (!directory.exists()) {
                directory.mkdir();
                //Toast.makeText(this, uri.getPath(), Toast.LENGTH_SHORT).show();
            }

            File destinationFile = new File(directory, fileName);
            if (destinationFile.exists()) {
                // Tệp tin tồn tại
                // Thực hiện các hành động bạn muốn ở đây

                List<String[]> csvData = readCsvFile(destinationFile);
                for(int i =0; i<csvData.size();i++){
                    String name = csvData.get(i)[0];
                    String className = csvData.get(i)[1];
                    String address = csvData.get(i)[2];
                    Long ageValue = (long)Integer.parseInt(csvData.get(i)[3]);
                    Double scoreValue = Double.parseDouble(csvData.get(i)[4]);
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("students");
                    String newStudentId = usersRef.push().getKey();

                    StudentModel student = new StudentModel(newStudentId,name,className,address,ageValue,scoreValue);
                    usersRef.child(newStudentId).setValue(student);
                }

                Toast.makeText(this, "Import successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Tệp tin không tồn tại
                Toast.makeText(this, "File does not exist", Toast.LENGTH_SHORT).show();
            }


            // Copy the file to the destination path

            copyInputStreamToFile(inputStream, destinationFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    private String readFromFile(File file) {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }
    private List<String[]> readCsvFile(File file) {
        List<String[]> csvData = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                csvData.add(line);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return csvData;
    }
    public static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        }
    }
    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }


    public void LoadToolBar(){
        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_logout) {
            // Handle the Logout action
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(StudentListActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_profile) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            Intent intent = new Intent(StudentListActivity.this, ProfileActivity.class);
            intent.putExtra("userName", email);
            startActivity(intent);
            return true;
        }  else if (itemId == R.id.action_history) {
            Intent intent = new Intent(StudentListActivity.this, LoginHistoryActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_view_student_list) {
//            Intent intent = new Intent(EditUserActivity.this, StudentListActivity.class);
//            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_add_student) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
                Intent intent = new Intent(StudentListActivity.this, AddStudentActivity.class);
                startActivity(intent);
            } else{
                Toast.makeText(StudentListActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_add_user) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("admin")){
                Intent intent = new Intent(StudentListActivity.this, AddUserActivity.class);
                startActivity(intent);
            } else{
                Toast.makeText(StudentListActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if (itemId == R.id.action_view_user_list) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            String email = currentUser.getEmail();
            if (email.toLowerCase().contains("admin")){
                Intent intent = new Intent(StudentListActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else{
                Toast.makeText(StudentListActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_view_student_list) {
//            FirebaseAuth auth = FirebaseAuth.getInstance();
//            FirebaseUser currentUser = auth.getCurrentUser();
//            String email = currentUser.getEmail();
//            if (email.toLowerCase().contains("manager") || email.toLowerCase().contains("admin")){
//                Intent intent = new Intent(StudentListActivity.this, StudentListActivity.class);
//                startActivity(intent);
//                return true;
//            } else{
//                Toast.makeText(StudentListActivity.this, "user don't have permission to do this task", Toast.LENGTH_SHORT).show();
//            }
            return true;
        }  else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void search(String setItemForSniper)
    {
        edtSearch = findViewById(R.id.studentsearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*if(searchClick ==0){
                    filterStudentList(s.toString());
                }
                else if(searchClick == 1) {
                    filterStudentListByClass(s.toString());
                }
                else if(searchClick == 2){
                    filterStudentListByAge(s.toString());
                }
                else {
                    filterStudentListByScore(s.toString());
                }*/
                filterStudentList(s.toString(),nameClick,classClick,ageClick,scoreClick,setItemForSniper);


            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void filterStudentList(String searchText, int nameClick, int classClick, int ageClick, int scoreClick, String setItemForSniper ) {
        rv_student = findViewById(R.id.rv_student);
        rv_student.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("students");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<StudentModel> students = new ArrayList<>();
                if(searchText == null){
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        StudentModel student = userSnapshot.getValue(StudentModel.class);
                        // Filter based on student name or any other criteria

                       /* if (student != null && student.getName().toLowerCase().contains(searchText.toLowerCase())) {
                            students.add(student);
                        }*/
                        if (student != null){
                            students.add(student);
                        }
                    }

                    StudentAdapter adapter = new StudentAdapter(StudentListActivity.this, students);
                    rv_student.setAdapter(adapter);
                }
                else {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        StudentModel student = userSnapshot.getValue(StudentModel.class);
                        // Filter based on student name or any other criteria
                        if(nameClick == 0 && classClick == 0 && ageClick == 0 && scoreClick ==0){
                            if (student != null){
                                //students.add(student);
                            }
                        }
                        else if (nameClick == 1 && classClick == 0 && ageClick == 0 && scoreClick ==0) {
                            if (student != null && student.getName().toLowerCase().contains(searchText.toLowerCase())) {
                                students.add(student);
                            }
                        }
                        else if (nameClick == 0 && classClick == 1 && ageClick == 0 && scoreClick ==0) {
                            if (student != null && student.getClassName().toLowerCase().contains(searchText.toLowerCase())) {
                                students.add(student);
                            }
                        }
                        else if (nameClick == 1 && classClick == 1 && ageClick == 0 && scoreClick ==0) {
                            if (student != null && (student.getClassName().toLowerCase().contains(searchText.toLowerCase()) || student.getName().toLowerCase().contains(searchText.toLowerCase()))) {
                                students.add(student);
                            }
                        }
                        else if (nameClick == 0 && classClick == 0 && ageClick == 1 && scoreClick ==0) {
                            if (student != null && String.valueOf(student.getAge()).toLowerCase().contains(searchText.toLowerCase())) {
                                students.add(student);
                            }
                        }
                        else if (nameClick == 1 && classClick == 0 && ageClick == 1 && scoreClick ==0) {
                            if (student != null && (String.valueOf(student.getAge()).toLowerCase().contains(searchText.toLowerCase()) || student.getName().toLowerCase().contains(searchText.toLowerCase())  )) {
                                students.add(student);
                            }
                        }
                        else if (nameClick == 0 && classClick == 1 && ageClick == 1 && scoreClick ==0) {
                            if (student != null && (String.valueOf(student.getAge()).toLowerCase().contains(searchText.toLowerCase()) || student.getClassName().toLowerCase().contains(searchText.toLowerCase())  )) {
                                students.add(student);
                            }
                        }

                        else if (nameClick == 1 && classClick == 1 && ageClick == 1 && scoreClick ==0) {
                            if (student != null && (String.valueOf(student.getAge()).toLowerCase().contains(searchText.toLowerCase()) || student.getClassName().toLowerCase().contains(searchText.toLowerCase()) || student.getName().toLowerCase().contains(searchText.toLowerCase())  )) {
                                students.add(student);
                            }
                        }
                        else if (nameClick == 0 && classClick == 0 && ageClick == 0 && scoreClick ==1) {
                            if (student != null &&  (String.valueOf(student.getScore()).toLowerCase().contains(searchText.toLowerCase())  )) {
                                students.add(student);
                            }
                        }
                        else if (nameClick == 1 && classClick == 0 && ageClick == 0 && scoreClick ==1) {
                            if (student != null &&  (String.valueOf(student.getScore()).toLowerCase().contains(searchText.toLowerCase()) || student.getName().toLowerCase().contains(searchText.toLowerCase())  )) {
                                students.add(student);
                            }
                        }

                        else if (nameClick == 0 && classClick == 1 && ageClick == 0 && scoreClick ==1) {
                            if (student != null &&  (String.valueOf(student.getScore()).toLowerCase().contains(searchText.toLowerCase()) || student.getClassName().toLowerCase().contains(searchText.toLowerCase())  )) {
                                students.add(student);
                            }
                        }
                        else if (nameClick == 1 && classClick == 1 && ageClick == 0 && scoreClick ==1) {
                            if (student != null &&  (String.valueOf(student.getScore()).toLowerCase().contains(searchText.toLowerCase()) || student.getClassName().toLowerCase().contains(searchText.toLowerCase()) || student.getName().toLowerCase().contains(searchText.toLowerCase())  )) {
                                students.add(student);
                            }
                        }
                        else if (nameClick == 0 && classClick == 0 && ageClick == 1 && scoreClick ==1) {
                            if (student != null &&  (String.valueOf(student.getScore()).toLowerCase().contains(searchText.toLowerCase()) || String.valueOf(student.getAge()).toLowerCase().contains(searchText.toLowerCase())  )) {
                                students.add(student);
                            }
                        }
                        else if (nameClick == 1 && classClick == 0 && ageClick == 1 && scoreClick ==1) {
                            if (student != null &&  (String.valueOf(student.getScore()).toLowerCase().contains(searchText.toLowerCase()) || String.valueOf(student.getAge()).toLowerCase().contains(searchText.toLowerCase()) ||  student.getName().toLowerCase().contains(searchText.toLowerCase())  )) {
                                students.add(student);
                            }
                        }
                        else if (nameClick == 0 && classClick == 1 && ageClick == 1 && scoreClick ==1) {
                            if (student != null &&  (String.valueOf(student.getScore()).toLowerCase().contains(searchText.toLowerCase()) || String.valueOf(student.getAge()).toLowerCase().contains(searchText.toLowerCase()) ||  student.getClassName().toLowerCase().contains(searchText.toLowerCase())  )) {
                                students.add(student);
                            }
                        }
                        else {
                            if (student != null &&  (String.valueOf(student.getScore()).toLowerCase().contains(searchText.toLowerCase()) || student.getClassName().toLowerCase().contains(searchText.toLowerCase()) || student.getName().toLowerCase().contains(searchText.toLowerCase()) || String.valueOf(student.getAge()).toLowerCase().contains(searchText.toLowerCase())  )) {
                                students.add(student);
                            }
                        }


                    /*if (student != null){
                        students.add(student);
                    }*/
                    }
                   /* if(setItemForSniper.equals("Name")){
                        Collections.sort(students, new StudentNameComparator());
                    }
                    else if(setItemForSniper.equals("Age")){
                        Collections.sort(students,new StudentAgeComparator());
                    }
                    else if(setItemForSniper.equals("Class")){
                        Collections.sort(students,new StudentClassComparator());
                    }
                    else if(setItemForSniper.equals("Score")){
                        Collections.sort(students,new StudentScoreComparator());
                    }*/

                    StudentAdapter adapter = new StudentAdapter(StudentListActivity.this, students);
                    rv_student.setAdapter(adapter);
                }

            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
            }
        });
    }




    public class StudentNameComparator implements Comparator<StudentModel> {
        @Override
        public int compare(StudentModel student1, StudentModel student2) {
            return student1.getName().compareToIgnoreCase(student2.getName());
        }
    }

    public class StudentClassComparator implements Comparator<StudentModel> {
        @Override
        public int compare(StudentModel student1, StudentModel student2) {
            return student1.getClassName().compareToIgnoreCase(student2.getClassName());
        }
    }
    public class StudentScoreComparator implements Comparator<StudentModel> {
        @Override
        public int compare(StudentModel student1, StudentModel student2) {
            return student1.getScore().compareTo(student2.getScore());
        }
    }

    public class StudentAgeComparator implements Comparator<StudentModel> {
        @Override
        public int compare(StudentModel student1, StudentModel student2) {
            return student1.getAge().compareTo(student2.getAge());
        }
    }



    private void filterStudentListByScore(String searchText) {
        rv_student = findViewById(R.id.rv_student);
        rv_student.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("students");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<StudentModel> students = new ArrayList<>();
                if(searchText == null){
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        StudentModel student = userSnapshot.getValue(StudentModel.class);
                        // Filter based on student name or any other criteria

                       /* if (student != null && student.getName().toLowerCase().contains(searchText.toLowerCase())) {
                            students.add(student);
                        }*/
                        if (student != null){
                            students.add(student);
                        }
                    }

                    StudentAdapter adapter = new StudentAdapter(StudentListActivity.this, students);
                    rv_student.setAdapter(adapter);
                }
                else {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        StudentModel student = userSnapshot.getValue(StudentModel.class);
                        // Filter based on student name or any other criteria

                        if (student != null && String.valueOf(student.getScore()).toLowerCase().contains(searchText.toLowerCase())) {

                            students.add(student);
                        }
                    /*if (student != null){
                        students.add(student);
                    }*/
                    }


                    StudentAdapter adapter = new StudentAdapter(StudentListActivity.this, students);
                    rv_student.setAdapter(adapter);
                }


            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
            }
        });


    }

    //ImportFile



}
