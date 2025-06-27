package com.example.adtaskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.adtaskmanager.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private TextInputEditText editTextName, editTextSurname, editTextEmail, editTextPassword;
    private TextInputLayout layoutName, layoutSurname, layoutEmail, layoutPassword;
    private MaterialButton btnRegister;
    private View textLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        editTextName = findViewById(R.id.edit_text_name);
        editTextSurname = findViewById(R.id.edit_text_surname);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        layoutName = findViewById(R.id.layout_name);
        layoutSurname = findViewById(R.id.layout_surname);
        layoutEmail = findViewById(R.id.layout_email);
        layoutPassword = findViewById(R.id.layout_password);
        btnRegister = findViewById(R.id.btn_register);
        textLogin = findViewById(R.id.text_login);

        btnRegister.setOnClickListener(v -> registerUser());
        textLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = editTextName.getText() != null ? editTextName.getText().toString().trim() : "";
        String surname = editTextSurname.getText() != null ? editTextSurname.getText().toString().trim() : "";
        String email = editTextEmail.getText() != null ? editTextEmail.getText().toString().trim() : "";
        String password = editTextPassword.getText() != null ? editTextPassword.getText().toString().trim() : "";
        layoutName.setError(null);
        layoutSurname.setError(null);
        layoutEmail.setError(null);
        layoutPassword.setError(null);

        boolean hasError = false;

        // Проверка имени: только буквы, без пробелов, без цифр
        if (TextUtils.isEmpty(name)) {
            layoutName.setError(getString(R.string.error_empty_name));
            hasError = true;
        } else if (!name.matches("^[A-Za-zА-Яа-яЁё]+$") || name.contains(" ")) {
            layoutName.setError(getString(R.string.error_invalid_name));
            hasError = true;
        }

        // Проверка фамилии: только буквы, без пробелов, без цифр
        if (TextUtils.isEmpty(surname)) {
            layoutSurname.setError(getString(R.string.error_empty_surname));
            hasError = true;
        } else if (!surname.matches("^[A-Za-zА-Яа-яЁё]+$") || surname.contains(" ")) {
            layoutSurname.setError(getString(R.string.error_invalid_surname));
            hasError = true;
        }

        // Проверка email
        if (TextUtils.isEmpty(email)) {
            layoutEmail.setError(getString(R.string.error_invalid_email));
            hasError = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError(getString(R.string.error_invalid_email_format));
            hasError = true;
        } else if (email.contains(" ")) {
            layoutEmail.setError(getString(R.string.error_no_spaces));
            hasError = true;
        }

        // Проверка пароля
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            layoutPassword.setError(getString(R.string.error_invalid_password));
            hasError = true;
        } else if (password.contains(" ")) {
            layoutPassword.setError(getString(R.string.error_no_spaces));
            hasError = true;
        }

        if (hasError) return;

        // Форматирование имени и фамилии: первая буква заглавная, остальные маленькие
        name = capitalizeFirstLetter(name);
        surname = capitalizeFirstLetter(surname);
        editTextName.setText(name);
        editTextSurname.setText(surname);

        btnRegister.setEnabled(false);
        String finalName = name;
        String finalSurname = surname;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    btnRegister.setEnabled(true);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            long createdAt = System.currentTimeMillis();
                            User userObj = new User(user.getUid(), finalName, finalSurname, email, createdAt);
                            usersRef.child(user.getUid()).setValue(userObj)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(this, getString(R.string.registration_success), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Ошибка сохранения профиля", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.error_auth_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String capitalizeFirstLetter(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
} 