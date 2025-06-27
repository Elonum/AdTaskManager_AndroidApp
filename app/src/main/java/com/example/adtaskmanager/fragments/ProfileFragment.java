package com.example.adtaskmanager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.adtaskmanager.R;
import com.example.adtaskmanager.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {
    private TextView textEmail, textCreatedAt;
    private EditText editName, editSurname;
    private Button btnSave, btnLogout;
    private ImageView btnDeleteAccount;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        textEmail = view.findViewById(R.id.text_profile_email);
        editName = view.findViewById(R.id.edit_profile_name);
        editSurname = view.findViewById(R.id.edit_profile_surname);
        textCreatedAt = view.findViewById(R.id.text_profile_created_at);
        btnSave = view.findViewById(R.id.btn_save_profile);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnDeleteAccount = view.findViewById(R.id.btn_delete_account);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            usersRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        textEmail.setText(user.getEmail());
                        editName.setText(user.getName());
                        editSurname.setText(user.getSurname());
                        String dateStr = formatDate(user.getCreatedAt());
                        textCreatedAt.setText("Дата регистрации: " + dateStr);
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {}
            });
        }

        btnSave.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();
            String newSurname = editSurname.getText().toString().trim();
            if (!validateName(newName)) {
                editName.setError("Только буквы, без пробелов, первая заглавная");
                return;
            }
            if (!validateName(newSurname)) {
                editSurname.setError("Только буквы, без пробелов, первая заглавная");
                return;
            }
            newName = capitalizeFirstLetter(newName);
            newSurname = capitalizeFirstLetter(newSurname);
            editName.setText(newName);
            editSurname.setText(newSurname);
            if (firebaseUser != null) {
                usersRef.child(firebaseUser.getUid()).child("name").setValue(newName);
                usersRef.child(firebaseUser.getUid()).child("surname").setValue(newSurname);
                Toast.makeText(getContext(), "Профиль обновлён", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeleteAccount.setOnClickListener(v -> {
            if (firebaseUser != null) {
                String uid = firebaseUser.getUid();
                usersRef.child(uid).removeValue();
                firebaseUser.delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Аккаунт удалён", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), com.example.adtaskmanager.LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        if (getActivity() != null) getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), "Ошибка удаления аккаунта", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), com.example.adtaskmanager.LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            if (getActivity() != null) getActivity().finish();
        });

        return view;
    }

    private boolean validateName(String s) {
        return !TextUtils.isEmpty(s) && s.matches("^[A-Za-zА-Яа-яЁё]+$") && !s.contains(" ");
    }

    private String capitalizeFirstLetter(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private String formatDate(long millis) {
        if (millis <= 0) return "-";
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }
} 