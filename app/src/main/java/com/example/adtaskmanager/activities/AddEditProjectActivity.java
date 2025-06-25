package com.example.adtaskmanager.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.adtaskmanager.R; // Убедитесь, что это ваш R файл
import com.example.adtaskmanager.models.Client; // Ваш класс Client
import com.example.adtaskmanager.models.Project; // Ваш класс Project

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.os.Build;
import android.view.Window;
import android.graphics.Color;

public class AddEditProjectActivity extends AppCompatActivity {

    private EditText editTextProjectName, editTextProjectDescription;
    private AutoCompleteTextView spinnerClient, spinnerProjectStatus;
    private EditText editTextStartDate, editTextEndDate; // Используем EditText для дат
    private TextView toolbarTitle;
    private ImageButton btnSaveProject;

    private int projectId = -1; // -1 означает новый проект, иначе это ID существующего проекта
    private Calendar startDateCalendar = Calendar.getInstance();
    private Calendar endDateCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    // Заглушки для данных клиентов и статусов
    private List<Client> clients;
    private String[] projectStatuses = {"Активный", "Завершенный", "На паузе", "Отменен"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_project);

        // Прозрачный status bar и immersive mode
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        window.getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        Toolbar toolbar = findViewById(R.id.toolbar_add_edit_project);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageButton btnBack = findViewById(R.id.btn_back_add_edit_project);
        toolbarTitle = findViewById(R.id.toolbar_title_add_edit_project);
        btnSaveProject = findViewById(R.id.btn_save_project);

        editTextProjectName = findViewById(R.id.edit_text_project_name);
        editTextProjectDescription = findViewById(R.id.edit_text_project_description);
        spinnerClient = findViewById(R.id.spinner_client);
        spinnerProjectStatus = findViewById(R.id.spinner_project_status);
        editTextStartDate = findViewById(R.id.edit_text_start_date);
        editTextEndDate = findViewById(R.id.edit_text_end_date);


        // Получение ID проекта, если это режим редактирования
        projectId = getIntent().getIntExtra("project_id", -1);
        if (projectId != -1) {
            toolbarTitle.setText("Редактировать проект");
            loadProjectData(projectId); // Загрузка данных существующего проекта
        } else {
            toolbarTitle.setText("Новый проект");
        }

        // Заполнение спиннеров
        setupSpinners();

        // Обработчики для выбора дат
        editTextStartDate.setOnClickListener(v -> showDatePickerDialog(editTextStartDate, startDateCalendar));
        editTextEndDate.setOnClickListener(v -> showDatePickerDialog(editTextEndDate, endDateCalendar));
        // Также обработчик для иконки календаря, если используется
        // (TextInputLayout.setEndIconOnClickListener)
        // Но обычно достаточно клика по полю, т.к. InputType="none" и focusable="false"

        // Обработчики нажатий на кнопки AppBar
        btnBack.setOnClickListener(v -> onBackPressed()); // Возврат на предыдущий экран
        btnSaveProject.setOnClickListener(v -> saveProject());
    }

    private void setupSpinners() {
        // Заглушка для клиентов (в реальном приложении будет загрузка из API)
        clients = new ArrayList<>();
        clients.add(new Client(1, "Матвей", "matvey@example.com"));
        clients.add(new Client(2, "Индира", "indira@example.com"));
        clients.add(new Client(3, "Анвар", "anvar@example.com"));

        ArrayAdapter<Client> clientAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, clients);
        spinnerClient.setAdapter(clientAdapter);


        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, projectStatuses);
        spinnerProjectStatus.setAdapter(statusAdapter);

        // Устанавливаем слушатель для spinnerClient, чтобы при выборе он корректно отображался
        spinnerClient.setOnItemClickListener((parent, view, position, id) -> {
            Client selectedClient = (Client) parent.getItemAtPosition(position);
            spinnerClient.setText(selectedClient.getName(), false); // Устанавливаем текст без фильтрации
        });

        // Устанавливаем слушатель для spinnerProjectStatus
        spinnerProjectStatus.setOnItemClickListener((parent, view, position, id) -> {
            String selectedStatus = (String) parent.getItemAtPosition(position);
            spinnerProjectStatus.setText(selectedStatus, false);
        });
    }

    private void showDatePickerDialog(final EditText targetEditText, final Calendar calendar) {
        new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    targetEditText.setText(dateFormatter.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void loadProjectData(int projectId) {
        // TODO: Здесь будет запрос к ASP.NET Core Web API для получения данных проекта по projectId
        // Пока что используем заглушку, имитирующую загрузку
        // !!! ДОБАВЛЕН АРГУМЕНТ "description" !!!
        Project projectToEdit = new Project(projectId, "Существующий проект", "Тестовый Клиент", "Детальное описание существующего проекта", "Активный", "01.01.2024", "31.12.2024", 50);

        editTextProjectName.setText(projectToEdit.getName());
        editTextProjectDescription.setText(projectToEdit.getDescription()); // Теперь это поле существует
        spinnerClient.setText(projectToEdit.getClientName(), false); // false, чтобы не активировать фильтр
        spinnerProjectStatus.setText(projectToEdit.getStatus(), false);

        // Установка дат в календари и поля ввода
        try {
            startDateCalendar.setTime(dateFormatter.parse(projectToEdit.getStartDate()));
            endDateCalendar.setTime(dateFormatter.parse(projectToEdit.getEndDate()));
            editTextStartDate.setText(projectToEdit.getStartDate());
            editTextEndDate.setText(projectToEdit.getEndDate());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка парсинга дат: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Найти и выбрать клиента и статус в спиннерах
        // Для spinnerClient:
        int clientPosition = -1;
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getName().equals(projectToEdit.getClientName())) {
                clientPosition = i;
                break;
            }
        }
        if (clientPosition != -1) {
            spinnerClient.setText(clients.get(clientPosition).getName(), false);
        }

        // Для spinnerProjectStatus:
        int statusPosition = -1;
        for (int i = 0; i < projectStatuses.length; i++) {
            if (projectStatuses[i].equals(projectToEdit.getStatus())) {
                statusPosition = i;
                break;
            }
        }
        if (statusPosition != -1) {
            spinnerProjectStatus.setText(projectStatuses[statusPosition], false);
        }
    }


    private void saveProject() {
        String name = editTextProjectName.getText().toString().trim();
        String description = editTextProjectDescription.getText().toString().trim();
        String clientName = spinnerClient.getText().toString().trim();
        String status = spinnerProjectStatus.getText().toString().trim();
        String startDate = editTextStartDate.getText().toString().trim();
        String endDate = editTextEndDate.getText().toString().trim();

        // Валидация данных
        if (TextUtils.isEmpty(name)) {
            editTextProjectName.setError("Название проекта обязательно");
            Toast.makeText(this, "Пожалуйста, введите название проекта", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(clientName)) {
            spinnerClient.setError("Клиент обязателен");
            Toast.makeText(this, "Пожалуйста, выберите клиента", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO: Добавить более полную валидацию, например, дат

        // Получить ID выбранного клиента
        int selectedClientId = -1;
        for (Client client : clients) {
            if (client.getName().equals(clientName)) {
                selectedClientId = client.getId();
                break;
            }
        }
        if (selectedClientId == -1) {
            // Если клиента с таким именем нет в списке, возможно, он новый или ошибка
            // Пока что можно считать ошибкой или добавить логику создания нового клиента
            Toast.makeText(this, "Неверный клиент или клиент не найден", Toast.LENGTH_SHORT).show();
            spinnerClient.setError("Выберите клиента из списка или создайте нового");
            return;
        }

        // Здесь будет отправка данных на ASP.NET Core Web API
        // В зависимости от projectId:
        if (projectId == -1) {
            // Создание нового проекта (POST запрос)
            Toast.makeText(this, "Создание нового проекта: " + name, Toast.LENGTH_SHORT).show();
            // TODO: Выполнить POST запрос через Retrofit
        } else {
            // Обновление существующего проекта (PUT запрос)
            Toast.makeText(this, "Обновление проекта ID " + projectId + ": " + name, Toast.LENGTH_SHORT).show();
            // TODO: Выполнить PUT запрос через Retrofit
        }

        // После успешного сохранения/обновления (имитация):
        Toast.makeText(this, "Проект успешно сохранен!", Toast.LENGTH_SHORT).show();
        finish(); // Закрываем этот экран и возвращаемся на предыдущий
    }
}
