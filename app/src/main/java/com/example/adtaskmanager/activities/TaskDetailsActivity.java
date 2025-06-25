package com.example.adtaskmanager.activities;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.view.Window;
import android.graphics.Color;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AlertDialog; // Для диалога смены статуса

import com.example.adtaskmanager.R;
import com.example.adtaskmanager.models.Task; // Ваша модель Task

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class TaskDetailsActivity extends AppCompatActivity {

    private int taskId;
    private TextView toolbarTitle;
    private TextView textTaskTitle, textTaskDescription, textTaskAssignee,
            textTaskDueDate, textTaskStatus;
    private ProgressBar progressBarTaskDetails;
    private TextView textTaskPercentage;
    private Button buttonChangeTaskStatus;

    // Заглушка для текущей задачи
    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        // Прозрачный status bar и immersive mode
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        window.getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        Toolbar toolbar = findViewById(R.id.toolbar_task_details);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageButton btnBack = findViewById(R.id.btn_back_task_details);
        ImageButton btnEditTask = findViewById(R.id.btn_edit_task);
        toolbarTitle = findViewById(R.id.toolbar_title_task_details);

        textTaskTitle = findViewById(R.id.text_task_title_details);
        textTaskDescription = findViewById(R.id.text_task_description_details);
        textTaskAssignee = findViewById(R.id.text_task_assignee_details);
        textTaskDueDate = findViewById(R.id.text_task_due_date_details);
        textTaskStatus = findViewById(R.id.text_task_status_details);
        progressBarTaskDetails = findViewById(R.id.progress_bar_task_details);
        textTaskPercentage = findViewById(R.id.text_task_percentage_details);
        buttonChangeTaskStatus = findViewById(R.id.button_change_task_status);

        // Получаем ID задачи
        taskId = getIntent().getIntExtra("task_id", -1);
        if (taskId == -1) {
            Toast.makeText(this, "Ошибка: ID задачи не передан.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Загрузка данных задачи
        loadTaskDetails(taskId);

        // Обработчики нажатий
        btnBack.setOnClickListener(v -> onBackPressed());
        btnEditTask.setOnClickListener(v -> navigateToAddEditTask());
        buttonChangeTaskStatus.setOnClickListener(v -> showStatusChangeDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Перезагружаем данные при возвращении на экран, если задача была отредактирована
        loadTaskDetails(taskId);
    }

    private void loadTaskDetails(int taskId) {
        // TODO: Здесь будет запрос к ASP.NET Core Web API для получения данных задачи по taskId.
        // Пока что используем заглушку, имитирующую получение данных
        // В реальном приложении, эти данные могут быть переданы из предыдущего экрана (ProjectDetailsActivity)
        // или запрошены по API.

        // Пример заглушки данных для задачи
        currentTask = new Task(
                taskId,
                1, // project_id
                "user1", // assignee_id
                "Разработать макет главной страницы", // title
                "Детальное описание задачи: необходимо проработать UI/UX, сделать кликабельный прототип и утвердить с клиентом. Особое внимание уделить мобильной версии.", // description
                "25.06.2025 18:00", // due_date
                "В работе", // status
                70, // completion_percentage
                "Иван Иванов" // assignee_name
        );

        // Обновляем UI элементами
        toolbarTitle.setText(currentTask.getTitle());
        textTaskTitle.setText(currentTask.getTitle());
        textTaskDescription.setText("Описание задачи: " + currentTask.getDescription());
        textTaskAssignee.setText("Исполнитель: " + currentTask.getAssigneeName());
        textTaskDueDate.setText("Срок выполнения: " + currentTask.getDueDate());
        textTaskStatus.setText("Статус: " + currentTask.getStatus());
        progressBarTaskDetails.setProgress(currentTask.getCompletionPercentage());
        textTaskPercentage.setText(currentTask.getCompletionPercentage() + "%");

        // Обновление текста кнопки в зависимости от статуса (опционально)
        updateButtonTextBasedOnStatus(currentTask.getStatus());
    }

    private void updateButtonTextBasedOnStatus(String status) {
        switch (status) {
            case "Завершена":
                buttonChangeTaskStatus.setText("Задача завершена");
                buttonChangeTaskStatus.setEnabled(false); // Отключить кнопку для завершенных
                break;
            case "В работе":
                buttonChangeTaskStatus.setText("Перевести в 'На проверке'");
                buttonChangeTaskStatus.setEnabled(true);
                break;
            case "На проверке":
                buttonChangeTaskStatus.setText("Перевести в 'Завершена' или 'В работу'");
                buttonChangeTaskStatus.setEnabled(true);
                break;
            case "Новая":
                buttonChangeTaskStatus.setText("Начать выполнение");
                buttonChangeTaskStatus.setEnabled(true);
                break;
            case "Отложена":
                buttonChangeTaskStatus.setText("Возобновить задачу");
                buttonChangeTaskStatus.setEnabled(true);
                break;
            default:
                buttonChangeTaskStatus.setText("Изменить статус задачи");
                buttonChangeTaskStatus.setEnabled(true);
                break;
        }
    }


    private void navigateToAddEditTask() {
        // Переход на экран редактирования задачи
        Intent intent = new Intent(TaskDetailsActivity.this, AddEditTaskActivity.class);
        intent.putExtra("task_id", taskId); // Передаем ID текущей задачи для редактирования
        intent.putExtra("project_id", currentTask.getProjectId()); // Также передаем ID проекта
        startActivity(intent);
    }

    private void showStatusChangeDialog() {
        if (currentTask == null) return;

        final String[] possibleStatuses = {"Новая", "В работе", "На проверке", "Завершена", "Отложена"};
        // Находим текущий статус и исключаем его из списка, чтобы не было смысла выбирать тот же
        List<String> statusList = new ArrayList<>(Arrays.asList(possibleStatuses));
        statusList.remove(currentTask.getStatus());

        final String[] displayStatuses = statusList.toArray(new String[0]);

        if (displayStatuses.length == 0) {
            Toast.makeText(this, "Нет доступных статусов для изменения.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Изменить статус задачи на");
        builder.setItems(displayStatuses, (dialog, which) -> {
            String selectedStatus = displayStatuses[which];
            updateTaskStatus(selectedStatus);
        });
        builder.show();
    }

    private void updateTaskStatus(String newStatus) {
        if (currentTask == null) return;

        // TODO: Здесь будет отправка PUT/PATCH запроса на API для изменения статуса задачи
        // (ID: currentTask.getId(), newStatus)

        // Имитация успешного обновления:
        currentTask.setStatus(newStatus); // Обновляем локальную модель
        loadTaskDetails(currentTask.getId()); // Перезагружаем UI с новыми данными

        Toast.makeText(this, "Статус задачи обновлен на: " + newStatus, Toast.LENGTH_SHORT).show();
    }
}
