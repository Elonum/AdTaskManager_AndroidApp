package com.example.adtaskmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.adtaskmanager.R;
import com.example.adtaskmanager.models.Client;

public class ClientDetailsActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private TextView textDetailClientName, textDetailClientPhone, textDetailClientEmail,
            textDetailClientAddress, textDetailClientNotes;
    private LinearLayout layoutClientPhone, layoutClientEmail, layoutClientAddress, layoutClientNotes;

    private int clientId = -1; // ID текущего клиента

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_details);

        Toolbar toolbar = findViewById(R.id.toolbar_client_details);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageButton btnBack = findViewById(R.id.btn_back_client_details);
        ImageButton btnEdit = findViewById(R.id.btn_edit_client);
        toolbarTitle = findViewById(R.id.toolbar_title_client_details);

        textDetailClientName = findViewById(R.id.text_detail_client_name);
        textDetailClientPhone = findViewById(R.id.text_detail_client_phone);
        textDetailClientEmail = findViewById(R.id.text_detail_client_email);
        textDetailClientAddress = findViewById(R.id.text_detail_client_address);
        textDetailClientNotes = findViewById(R.id.text_detail_client_notes);

        layoutClientPhone = findViewById(R.id.layout_client_phone);
        layoutClientEmail = findViewById(R.id.layout_client_email);
        layoutClientAddress = findViewById(R.id.layout_client_address);
        layoutClientNotes = findViewById(R.id.layout_client_notes);

        // Получаем данные клиента из Intent
        Intent intent = getIntent();
        if (intent != null) {
            clientId = intent.getIntExtra("client_id", -1);
            if (clientId != -1) {
                toolbarTitle.setText("Детали клиента");
                loadClientDetails(clientId);
            } else {
                Toast.makeText(this, "Ошибка: ID клиента не передан.", Toast.LENGTH_SHORT).show();
                finish(); // Закрываем Activity, если нет ID
            }
        }

        btnBack.setOnClickListener(v -> onBackPressed());

        btnEdit.setOnClickListener(v -> {
            // Переход на экран редактирования клиента
            if (clientId != -1) {
                Intent editIntent = new Intent(ClientDetailsActivity.this, AddEditClientActivity.class);
                editIntent.putExtra("client_id", clientId);
                // Можно передать и другие данные, чтобы не делать повторный запрос
                startActivity(editIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Перезагружаем данные при возвращении, чтобы обновить, если были изменения
        if (clientId != -1) {
            loadClientDetails(clientId);
        }
    }

    private void loadClientDetails(int id) {
        // TODO: Здесь будет запрос к ASP.NET Core Web API для получения данных клиента по ID
        // Пока что используем заглушку
        Client client = new Client(id, "ООО \"Рога и Копыта\"", "+79001234567", "rogi.kopyta@example.com", "ул. Ленина, д.1, офис 10", "Важный VIP-клиент. Предпочитает звонки по будням.");

        if (client != null) {
            toolbarTitle.setText(client.getName()); // Заголовок тулбара - имя клиента
            textDetailClientName.setText(client.getName());

            // Устанавливаем текст и видимость для полей
            if (client.getPhone() != null && !client.getPhone().isEmpty()) {
                textDetailClientPhone.setText("Телефон: " + client.getPhone());
                layoutClientPhone.setVisibility(View.VISIBLE);
            } else {
                layoutClientPhone.setVisibility(View.GONE);
            }

            if (client.getEmail() != null && !client.getEmail().isEmpty()) {
                textDetailClientEmail.setText("Email: " + client.getEmail());
                layoutClientEmail.setVisibility(View.VISIBLE);
            } else {
                layoutClientEmail.setVisibility(View.GONE);
            }

            if (client.getAddress() != null && !client.getAddress().isEmpty()) {
                textDetailClientAddress.setText("Адрес: " + client.getAddress());
                layoutClientAddress.setVisibility(View.VISIBLE);
            } else {
                layoutClientAddress.setVisibility(View.GONE);
            }

            if (client.getNotes() != null && !client.getNotes().isEmpty()) {
                textDetailClientNotes.setText(client.getNotes());
                layoutClientNotes.setVisibility(View.VISIBLE);
            } else {
                layoutClientNotes.setVisibility(View.GONE);
            }

        } else {
            Toast.makeText(this, "Клиент не найден.", Toast.LENGTH_SHORT).show();
            finish(); // Закрываем, если клиент не найден
        }
    }
}