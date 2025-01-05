package com.example.managerportal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {


    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    EditText Email, Password;
    Button loginBtn;
    ProgressBar progressbar;

    // Administrator Hardcoded information
    public final String administratorEmail = "chetangarg296290@gmail.com";
    public final String administratorPassword = "jaihanuman@1212";

    // function to validate email
    public static boolean isInValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        return !pattern.matcher(email).matches();
    }

    // function to show error and keyboard appears
    public static void showErrorAndRequestFocus(EditText editText, String errorMessage) {
        editText.setError(errorMessage);
        editText.requestFocus();
        editText.setText("");
        // Show the keyboard
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    // function to validate whole logging form
    public boolean isValidateForm(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            showErrorAndRequestFocus(Email, "Please Enter a valid email");
            return false;
        } else if (isInValidEmail(email)) {
            showErrorAndRequestFocus(Email, "Please enter a valid email");
            return false;
        } else if (TextUtils.isEmpty(password) || password.length() < 6) {
            showErrorAndRequestFocus(Password, "Please Enter a minimum 6 character password");
            return false;
        } else {
            loginBtn.setVisibility(View.GONE);
            progressbar.setVisibility(View.VISIBLE);
            return true;
        }
    }

    // function to login a user with help of static administration information
    public void loginUser(String email, String password) {
        if (email.equals(administratorEmail) && password.equals(administratorPassword)) {
            //  Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Login.this, Dashboard.class));
            finish();
        } else {
            Toast.makeText(Login.this, "Invalid Login credentials", Toast.LENGTH_SHORT).show();
            Email.setText("");
            Password.setText("");
            progressbar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        loginBtn = findViewById(R.id.loginBtn);
        progressbar = findViewById(R.id.progressbar);

        // When user click on login button first we have to validate the form and then we have to check whether the information that user given is correct or not. If information is correct then go to Dashboard Activity otherwise Toast invalid credentials.
        loginBtn.setOnClickListener(view -> {
            String email = Email.getText().toString();
            String password = Password.getText().toString();
            if (isValidateForm(email, password)) {
                loginUser(email, password);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}