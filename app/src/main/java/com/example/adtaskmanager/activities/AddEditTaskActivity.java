package com.example.adtaskmanager.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Window;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import com.example.adtaskmanager.R;
import com.example.adtaskmanager.models.Task;
import com.example.adtaskmanager.models.User;
// Импорты для уведомлений (AlarmManager, PendingIntent, NotificationCompat, NotificationManager)
// Пока не добавляем, но будем иметь в виду.

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEditTaskActivity extends AppCompatActivity {

    private EditText editTextTaskTitle, editTextTaskDescription;
    private AutoCompleteTextView spinnerAssignee, spinnerTaskStatus;
    private EditText editTextDueDate, editTextDueTime;
    private SeekBar seekBarProgress;
    private TextView textProgressPercentage;
    private SwitchMaterial switchEnableNotifications;
    private AutoCompleteTextView spinnerNotificationTime;
    private TextInputLayout layoutNotificationTime;

    private TextView toolbarTitle;
    private int projectId; // ID проекта, к которому привязана задача
    private int taskId = -1; // -1 для новой задачи, иначе ID существующей

    private Calendar dueDateTimeCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    // Заглушки для данных
    private List<User> assignees;
    private String[] taskStatuses = {"Новая", "В работе", "На проверке", "Завершена", "Отложена"};
    private String[] notificationTimes = {"Нет", "За 15 минут", "За 30 минут", "За 1 час", "За 3 часа", "За 1 день"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        // Прозрачный status bar и immersive mode
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        window.getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        Toolbar toolbar = findViewById(R.id.toolbar_add_edit_task);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageButton btnBack = findViewById(R.id.btn_back_add_edit_task);
        ImageButton btnSaveTask = findViewById(R.id.btn_save_task);
        toolbarTitle = findViewById(R.id.toolbar_title_add_edit_task);

        editTextTaskTitle = findViewById(R.id.edit_text_task_title);
        editTextTaskDescription = findViewById(R.id.edit_text_task_description);
        spinnerAssignee = findViewById(R.id.spinner_task_assignee);
        spinnerTaskStatus = findViewById(R.id.spinner_task_status);
        editTextDueDate = findViewById(R.id.edit_text_task_due_date);
        editTextDueTime = findViewById(R.id.edit_text_task_due_time);
        seekBarProgress = findViewById(R.id.seek_bar_task_progress);
        textProgressPercentage = findViewById(R.id.text_task_progress_percentage);
        switchEnableNotifications = findViewById(R.id.switch_enable_notifications);
        spinnerNotificationTime = findViewById(R.id.spinner_notification_time);
        layoutNotificationTime = findViewById(R.id.layout_notification_time);


        // Получение ID проекта и ID задачи
        projectId = getIntent().getIntExtra("project_id", -1);
        taskId = getIntent().getIntExtra("task_id", -1);

        if (taskId != -1) {
            toolbarTitle.setText("Редактировать задачу");
            loadTaskData(taskId); // Загрузка данных существующей задачи
        } else {
            toolbarTitle.setText("Новая задача");
        }

        setupSpinners();
        setupDateAndTimePickers();
        setupProgressSeekBar();
        setupNotificationSwitch();


        // Обработчики нажатий на кнопки AppBar
        btnBack.setOnClickListener(v -> onBackPressed());
        btnSaveTask.setOnClickListener(v -> saveTask());
    }

    private void setupSpinners() {
        // Заглушка для исполнителей
        assignees = new ArrayList<>();
        //assignees.add(new User("user1", "Иван", "Иванов", "ivan@example.com"));
        //assignees.add(new User("user2", "Мария", "Петрова", "maria@example.com"));
        //assignees.add(new User("user3", "Ольга", "Сидорова", "olga@example.com"));

        ArrayAdapter<User> assigneeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, assignees);
        spinnerAssignee.setAdapter(assigneeAdapter);
        spinnerAssignee.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = (User) parent.getItemAtPosition(position);
            spinnerAssignee.setText(selectedUser.getName(), false);
        });


        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, taskStatuses);
        spinnerTaskStatus.setAdapter(statusAdapter);
        spinnerTaskStatus.setOnItemClickListener((parent, view, position, id) -> {
            String selectedStatus = (String) parent.getItemAtPosition(position);
            spinnerTaskStatus.setText(selectedStatus, false);
        });

        ArrayAdapter<String> notificationTimeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, notificationTimes);
        spinnerNotificationTime.setAdapter(notificationTimeAdapter);
        spinnerNotificationTime.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTime = (String) parent.getItemAtPosition(position);
            spinnerNotificationTime.setText(selectedTime, false);
        });
        // По умолчанию выбрано "Нет" или первое значение
        spinnerNotificationTime.setText(notificationTimes[0], false);
    }

    private void setupDateAndTimePickers() {
        // Для даты
        editTextDueDate.setOnClickListener(v -> {
            new DatePickerDialog(AddEditTaskActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        dueDateTimeCalendar.set(Calendar.YEAR, year);
                        dueDateTimeCalendar.set(Calendar.MONTH, monthOfYear);
                        dueDateTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        editTextDueDate.setText(dateFormatter.format(dueDateTimeCalendar.getTime()));
                    },
                    dueDateTimeCalendar.get(Calendar.YEAR),
                    dueDateTimeCalendar.get(Calendar.MONTH),
                    dueDateTimeCalendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });

        // Для времени
        editTextDueTime.setOnClickListener(v -> {
            new TimePickerDialog(AddEditTaskActivity.this,
                    (view, hourOfDay, minute) -> {
                        dueDateTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dueDateTimeCalendar.set(Calendar.MINUTE, minute);
                        editTextDueTime.setText(timeFormatter.format(dueDateTimeCalendar.getTime()));
                    },
                    dueDateTimeCalendar.get(Calendar.HOUR_OF_DAY),
                    dueDateTimeCalendar.get(Calendar.MINUTE),
                    true) // true для 24-часового формата
                    .show();
        });
    }

    private void setupProgressSeekBar() {
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textProgressPercentage.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Ничего не делаем
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Ничего не делаем
            }
        });
    }

    private void setupNotificationSwitch() {
        switchEnableNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutNotificationTime.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                spinnerNotificationTime.setText(notificationTimes[0], false); // Сбросить выбор напоминания
            }
        });
        // Установить начальное состояние при загрузке
        layoutNotificationTime.setVisibility(switchEnableNotifications.isChecked() ? View.VISIBLE : View.GONE);
    }

    private void loadTaskData(int taskId) {
        // TODO: Загрузка данных задачи по taskId из API
        // Заглушка:
        Task taskToEdit = new Task(taskId, projectId, "user1", "Существующая задача",
                "Описание существующей задачи.", "28.06.2025 15:30", "В работе", 60, "Иван Иванов");

        editTextTaskTitle.setText(taskToEdit.getTitle());
        editTextTaskDescription.setText(taskToEdit.getDescription());
        spinnerAssignee.setText(taskToEdit.getAssigneeName(), false);
        spinnerTaskStatus.setText(taskToEdit.getStatus(), false);
        seekBarProgress.setProgress(taskToEdit.getCompletionPercentage());
        textProgressPercentage.setText(taskToEdit.getCompletionPercentage() + "%");

        try {
            dueDateTimeCalendar.setTime(dateTimeFormatter.parse(taskToEdit.getDueDate()));
            editTextDueDate.setText(dateFormatter.format(dueDateTimeCalendar.getTime()));
            editTextDueTime.setText(timeFormatter.format(dueDateTimeCalendar.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: Загрузка текущих настроек уведомлений для этой задачи (если они есть)
        // Например, если уведомления были включены:
        // switchEnableNotifications.setChecked(true);
        // spinnerNotificationTime.setText("За 1 час", false);
    }

    private void saveTask() {
        String title = editTextTaskTitle.getText().toString().trim();
        String description = editTextTaskDescription.getText().toString().trim();
        String assigneeName = spinnerAssignee.getText().toString().trim();
        String status = spinnerTaskStatus.getText().toString().trim();
        String dueDateStr = editTextDueDate.getText().toString().trim();
        String dueTimeStr = editTextDueTime.getText().toString().trim();
        int progress = seekBarProgress.getProgress();
        boolean notificationsEnabled = switchEnableNotifications.isChecked();
        String notificationTimeSelected = spinnerNotificationTime.getText().toString().trim();

        // Валидация
        if (TextUtils.isEmpty(title)) {
            editTextTaskTitle.setError("Название задачи обязательно");
            Toast.makeText(this, "Введите название задачи", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(assigneeName)) {
            spinnerAssignee.setError("Исполнитель обязателен");
            Toast.makeText(this, "Выберите исполнителя", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(dueDateStr) || TextUtils.isEmpty(dueTimeStr)) {
            Toast.makeText(this, "Пожалуйста, выберите дату и время выполнения", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получить ID выбранного исполнителя
        String selectedAssigneeId = null;
        for (User user : assignees) {
            if (user.getName().equals(assigneeName)) {
                selectedAssigneeId = user.getId();
                break;
            }
        }
        if (selectedAssigneeId == null) {
            Toast.makeText(this, "Неверный исполнитель или исполнитель не найден", Toast.LENGTH_SHORT).show();
            spinnerAssignee.setError("Выберите исполнителя из списка");
            return;
        }


        // Сформировать полную дату и время выполнения
        String fullDueDate = dueDateStr + " " + dueTimeStr;

        // Здесь будет логика сохранения/обновления через API
        if (taskId == -1) {
            // Создание новой задачи (POST)
            Toast.makeText(this, "Создание новой задачи: " + title + " для проекта ID: " + projectId, Toast.LENGTH_SHORT).show();
            // TODO: Отправка POST запроса
        } else {
            // Обновление существующей задачи (PUT)
            Toast.makeText(this, "Обновление задачи ID: " + taskId + " - " + title, Toast.LENGTH_SHORT).show();
            // TODO: Отправка PUT запроса
        }

        // Логика для уведомлений
        if (notificationsEnabled && !"Нет".equals(notificationTimeSelected)) {
            // TODO: Запланировать локальное уведомление с помощью AlarmManager или WorkManager
            // Вам потребуется:
            // 1. Уникальный ID для уведомления (например, taskId)
            // 2. Время срабатывания уведомления (dueDate - offset от notificationTimeSelected)
            // 3. Содержимое уведомления (заголовок, текст)
            // 4. Переход при нажатии на уведомление (например, на TaskDetailsActivity)
            // 5. Разрешение на уведомления (POST_NOTIFICATIONS для Android 13+)
            Toast.makeText(this, "Уведомление запланировано на " + notificationTimeSelected, Toast.LENGTH_SHORT).show();
            scheduleNotification(taskId, title, fullDueDate, notificationTimeSelected);
        } else {
            // TODO: Отменить существующее уведомление, если оно было
            Toast.makeText(this, "Уведомления отключены для этой задачи", Toast.LENGTH_SHORT).show();
            cancelNotification(taskId);
        }

        Toast.makeText(this, "Задача успешно сохранена!", Toast.LENGTH_SHORT).show();
        finish(); // Возвращаемся на экран деталей проекта
    }

    // --- Методы для работы с уведомлениями (заглушки) ---
    private void scheduleNotification(int taskId, String taskTitle, String fullDueDate, String notificationOffset) {
        // Эта логика будет реализована более детально позже
        // Нужно преобразовать notificationOffset ("За 15 минут" и т.д.) в миллисекунды
        // и вычесть это время из fullDueDate, чтобы получить время срабатывания.

        // Пример: для AlarmManager
        // long triggerTime = calculateNotificationTime(fullDueDate, notificationOffset);
        // Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        // notificationIntent.putExtra("task_id", taskId);
        // notificationIntent.putExtra("task_title", taskTitle);
        // PendingIntent pendingIntent = PendingIntent.getBroadcast(this, taskId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        // AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // if (alarmManager != null && triggerTime > System.currentTimeMillis()) {
        //     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //         alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        //     } else {
        //         alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        //     }
        // }
    }

    private void cancelNotification(int taskId) {
        // Логика отмены уведомления по ID
        // Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        // PendingIntent pendingIntent = PendingIntent.getBroadcast(this, taskId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        // AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // if (alarmManager != null) {
        //     alarmManager.cancel(pendingIntent);
        // }
    }

    // Вспомогательный метод для расчета времени уведомления (будет доработан)
    private long calculateNotificationTime(String fullDueDate, String notificationOffset) {
        try {
            Calendar targetCalendar = Calendar.getInstance();
            targetCalendar.setTime(dateTimeFormatter.parse(fullDueDate));

            long offsetMillis = 0;
            switch (notificationOffset) {
                case "За 15 минут":
                    offsetMillis = 15 * 60 * 1000L;
                    break;
                case "За 30 минут":
                    offsetMillis = 30 * 60 * 1000L;
                    break;
                case "За 1 час":
                    offsetMillis = 60 * 60 * 1000L;
                    break;
                case "За 3 часа":
                    offsetMillis = 3 * 60 * 60 * 1000L;
                    break;
                case "За 1 день":
                    offsetMillis = 24 * 60 * 60 * 1000L;
                    break;
                // Добавьте другие варианты
            }
            return targetCalendar.getTimeInMillis() - offsetMillis;

        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return -1; // Ошибка парсин
        }
    }
}