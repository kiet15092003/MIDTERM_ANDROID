package com.example.androidmidterm_firebase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AddCertificateActivity extends AppCompatActivity {
    TextInputLayout StudentNameInputLayout,CertificateInputLayout,ScoreInputLayout;
    TextInputEditText StudentNameEditText,CertificateEditText,ScoreEditText;
    MaterialButton btn_back_to_student;
    Button btn_add_certificate;
    boolean checkErrorLength = false;
    String studentId;
    Button btn_import_file;
    private static final int REQUEST_CODE = 1;
    private static final int PICK_FILE_REQUEST_CODE = 123;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_certificate);
        StudentNameInputLayout = (TextInputLayout) findViewById(R.id.StudentNameInputLayout);
        CertificateInputLayout = (TextInputLayout)  findViewById(R.id.CertificateInputLayout);
        ScoreInputLayout = (TextInputLayout)  findViewById(R.id.ScoreInputLayout);
        StudentNameEditText = (TextInputEditText) findViewById(R.id.StudentNameEditText);
        CertificateEditText = (TextInputEditText) findViewById(R.id.CertificateEditText);
        ScoreEditText = (TextInputEditText) findViewById(R.id.ScoreEditText);
        btn_back_to_student = findViewById(R.id.btn_back_to_student);
        btn_add_certificate = findViewById(R.id.btn_add_certificate);
        LoadEditTextAndInputLayout(CertificateInputLayout,CertificateEditText);
        LoadEditTextAndInputLayout(ScoreInputLayout,ScoreEditText);
        Intent intent = getIntent();
        // Retrieve the data from the Intent using the key
        String intentValue = intent.getStringExtra("studentId");
        String intentFrom = intent.getStringExtra("from");
        studentId = intentValue;
        LoadStudentNameByStudentId(studentId);
        ImportCerFormFile();
        btn_add_certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add certificate
                String certificateName = CertificateEditText.getText().toString();
                String score = ScoreEditText.getText().toString();
                if (certificateName.isEmpty() || score.isEmpty()){
                    Toast.makeText(AddCertificateActivity.this, "Please enter all information", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkErrorLength) {
                        Toast.makeText(AddCertificateActivity.this, "Please check length of information", Toast.LENGTH_SHORT).show();
                    } else {
                        double scoreValue = 0;
                        try {
                            scoreValue = Double.parseDouble(score);
                        } catch (Exception e) {
                            Toast.makeText(AddCertificateActivity.this, "Invalid score", Toast.LENGTH_SHORT).show();
                        }
                        if (scoreValue > 0)
                        {
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("certificates");
                            String newCertificateId = usersRef.push().getKey();
                            CertificateModel certificate = new CertificateModel(certificateName,studentId,scoreValue);
                            usersRef.child(newCertificateId).setValue(certificate);
                            CertificateEditText.setText("");
                            ScoreEditText.setText("");
                            Toast.makeText(AddCertificateActivity.this, "Add certificate successful", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(AddCertificateActivity.this, "Invalid score", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        btn_back_to_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if intent fro add student
                if (intentFrom.equals("AddStudentActivity")){
                    Intent intent = new Intent(AddCertificateActivity.this, AddStudentActivity.class);
                    startActivity(intent);
                } else{
                    // if intent from detail
                    Intent intent = new Intent(AddCertificateActivity.this, StudentDetailActivity.class);
                    intent.putExtra("studentId", studentId);
                    // Start the new activity with the Intent, expecting a result
                    startActivityForResult(intent,REQUEST_CODE);
                }
            }
        });
    }
    public void LoadStudentNameByStudentId(String stdId){
        DatabaseReference studentReference = FirebaseDatabase.getInstance().getReference().child("students").child(stdId);
        studentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = snapshot.child("name").getValue(String.class);
                    StudentNameEditText.setText(name);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void LoadEditTextAndInputLayout(TextInputLayout textInputLayout, TextInputEditText textInputEditText){
        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInputEditText.setText("");
            }
        });
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > textInputLayout.getCounterMaxLength()){
                    textInputLayout.setError("Max character length is " + textInputLayout.getCounterMaxLength());
                    checkErrorLength = true;
                }
                else{
                    textInputLayout.setError(null);
                    checkErrorLength=false;
                }
            }
        });
    }

    //Import file
    public void ImportCerFormFile(){
        btn_import_file = findViewById(R.id.btn_add_cer_fromFile);
        btn_import_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFile();
            }
        });

    }
    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // Specify the mime type of Excel files if needed.
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
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
                    String certificateName = csvData.get(i)[0];

                    Double scoreValue = Double.parseDouble(csvData.get(i)[1]);

                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("certificates");
                    String newCertificateId = usersRef.push().getKey();
                    CertificateModel certificate = new CertificateModel(certificateName,studentId,scoreValue);
                    usersRef.child(newCertificateId).setValue(certificate);


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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if the result is for the correct request code
        if (requestCode == REQUEST_CODE) {
            // Check if the operation was successful
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

}
