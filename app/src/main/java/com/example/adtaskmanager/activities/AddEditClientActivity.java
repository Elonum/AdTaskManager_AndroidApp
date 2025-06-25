package com.example.adtaskmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.view.Window;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText; // Обратите внимание на импорт
import com.example.adtaskmanager.R;
import com.example.adtaskmanager.models.Client; // Убедитесь, что ваша модель Client существует

public class AddEditClientActivity extends AppCompatActivity {

    private TextInputEditText editTextClientName, editTextClientPhone, editTextClientEmail,
            editTextClientAddress, editTextClientNotes;
    private TextView toolbarTitle;
    private ImageButton btnSaveClient;

    private int clientId = -1; // -1 означает новый клиент, иначе это ID существующего

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_client);

        // Прозрачный status bar и immersive mode
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        window.getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        Toolbar toolbar = findViewById(R.id.toolbar_add_edit_client);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageButton btnBack = findViewById(R.id.btn_back_add_edit_client);
        toolbarTitle = findViewById(R.id.toolbar_title_add_edit_client);
        btnSaveClient = findViewById(R.id.btn_save_client);

        editTextClientName = findViewById(R.id.edit_text_client_name);
        editTextClientPhone = findViewById(R.id.edit_text_client_phone);
        editTextClientEmail = findViewById(R.id.edit_text_client_email);
        editTextClientAddress = findViewById(R.id.edit_text_client_address);
        editTextClientNotes = findViewById(R.id.edit_text_client_notes);

        // Получение ID клиента, если это режим редактирования
        clientId = getIntent().getIntExtra("client_id", -1);
        if (clientId != -1) {
            toolbarTitle.setText("Редактировать клиента");
            loadClientData(clientId); // Загрузка данных существующего клиента
        } else {
            toolbarTitle.setText("Новый клиент");
        }

        // Обработчики нажатий на кнопки AppBar
        btnBack.setOnClickListener(v -> onBackPressed()); // Возврат на предыдущий экран
        btnSaveClient.setOnClickListener(v -> saveClient());
    }

    private void loadClientData(int clientId) {
        // TODO: Здесь будет запрос к ASP.NET Core Web API для получения данных клиента по clientId
        // Пока что используем заглушку, имитирующую загрузку
        Client clientToEdit = new Client(
                clientId,
                "ООО \"МегаБизнес\"",
                "+79123456789",
                "info@megabusiness.com",
                "г. Москва, ул. Примерная, д. 15",
                "Крупный заказчик, требовательный к срокам."
        );

        if (clientToEdit != null) {
            editTextClientName.setText(clientToEdit.getName());
            editTextClientPhone.setText(clientToEdit.getPhone());
            editTextClientEmail.setText(clientToEdit.getEmail());
            editTextClientAddress.setText(clientToEdit.getAddress());
            editTextClientNotes.setText(clientToEdit.getNotes());
        } else {
            Toast.makeText(this, "Ошибка загрузки данных клиента.", Toast.LENGTH_SHORT).show();
            // Возможно, стоит закрыть Activity, если данные не найдены
            finish();
        }
    }

    private void saveClient() {
        String name = editTextClientName.getText().toString().trim();
        String phone = editTextClientPhone.getText().toString().trim();
        String email = editTextClientEmail.getText().toString().trim();
        String address = editTextClientAddress.getText().toString().trim();
        String notes = editTextClientNotes.getText().toString().trim();

        // Валидация данных
        if (TextUtils.isEmpty(name)) {
            editTextClientName.setError("Название клиента обязательно");
            Toast.makeText(this, "Пожалуйста, введите название клиента", Toast.LENGTH_SHORT).show();
            return;
        }
        // Добавьте больше валидации по мере необходимости, например, для email, телефона.
        if (!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextClientEmail.setError("Некорректный формат Email");
            Toast.makeText(this, "Пожалуйста, введите корректный Email", Toast.LENGTH_SHORT).show();
            return;
        }


        // Здесь будет отправка данных на ASP.NET Core Web API
        // В зависимости от clientId:
        if (clientId == -1) {
            // Создание нового клиента (POST запрос)
            Toast.makeText(this, "Создание нового клиента: " + name, Toast.LENGTH_SHORT).show();
            // TODO: Выполнить POST запрос через Retrofit
            // Client newClient = new Client(0, name, phone, email, address, notes); // ID будет присвоен сервером
        } else {
            // Обновление существующего клиента (PUT запрос)
            Toast.makeText(this, "Обновление клиента ID " + clientId + ": " + name, Toast.LENGTH_SHORT).show();
            // TODO: Выполнить PUT запрос через Retrofit
            // Client updatedClient = new Client(clientId, name, phone, email, address, notes);
        }

        // После успешного сохранения/обновления (имитация):
        Toast.makeText(this, "Клиент успешно сохранен!", Toast.LENGTH_SHORT).show();
        finish(); // Закрываем этот экран и возвращаемся на предыдущий
    }
}