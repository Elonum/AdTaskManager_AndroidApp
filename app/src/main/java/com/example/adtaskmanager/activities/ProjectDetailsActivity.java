package com.example.adtaskmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.view.Window;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.adtaskmanager.R; // Ваш R файл
import com.example.adtaskmanager.adapters.TaskAdapter; // Ваш TaskAdapter
import com.example.adtaskmanager.models.Project; // Ваша модель Project
import com.example.adtaskmanager.models.Task;     // Ваша модель Task
import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;
import java.util.List;

public class ProjectDetailsActivity extends AppCompatActivity implements TaskAdapter.OnItemClickListener {

    private int projectId;
    private TextView toolbarTitle;
    private TextView textProjectName, textClientName, textProjectDescription,
            textProjectStatus, textProjectDates;
    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private TextView textNoTasks;
    private FloatingActionButton fabAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        // Прозрачный status bar и immersive mode
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        window.getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        Toolbar toolbar = findViewById(R.id.toolbar_project_details);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageButton btnBack = findViewById(R.id.btn_back_project_details);
        ImageButton btnEditProject = findViewById(R.id.btn_edit_project);
        toolbarTitle = findViewById(R.id.toolbar_title_project_details);

        textProjectName = findViewById(R.id.text_project_name_details);
        textClientName = findViewById(R.id.text_client_name_details);
        textProjectDescription = findViewById(R.id.text_project_description_details);
        textProjectStatus = findViewById(R.id.text_project_status_details);
        textProjectDates = findViewById(R.id.text_project_dates_details);
        recyclerViewTasks = findViewById(R.id.recycler_view_tasks);
        textNoTasks = findViewById(R.id.text_no_tasks);
        fabAddTask = findViewById(R.id.fab_add_task);

        // Получаем ID проекта, который был передан из MainActivity
        projectId = getIntent().getIntExtra("project_id", -1);
        if (projectId == -1) {
            Toast.makeText(this, "Ошибка: ID проекта не передан.", Toast.LENGTH_SHORT).show();
            finish(); // Закрываем экран, если нет ID
            return;
        }

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, taskList, this);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);

        // Загрузка данных проекта и задач
        loadProjectAndTaskData();

        // Обработчики нажатий
        btnBack.setOnClickListener(v -> onBackPressed());
        btnEditProject.setOnClickListener(v -> navigateToAddEditProject());
        fabAddTask.setOnClickListener(v -> navigateToAddEditTask());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Перезагружаем данные при возвращении на экран, чтобы обновить список задач
        loadProjectAndTaskData();
    }

    private void loadProjectAndTaskData() {
        // TODO: Здесь будет запрос к ASP.NET Core Web API для получения данных проекта
        // и связанных с ним задач по projectId.
        // Пока что используем заглушки.

        // Загрузка данных проекта (можно использовать данные, переданные через Intent, если они есть)
        String projectName = getIntent().getStringExtra("project_name");
        String clientName = getIntent().getStringExtra("client_name");
        String projectStatus = getIntent().getStringExtra("project_status");
        String projectStartDate = getIntent().getStringExtra("project_start_date");
        String projectEndDate = getIntent().getStringExtra("project_end_date");
        // String projectDescription = getIntent().getStringExtra("project_description"); // Добавьте в MainActivity если передаете

        toolbarTitle.setText(projectName != null ? projectName : "Проект (ID: " + projectId + ")");
        textProjectName.setText(projectName != null ? projectName : "Проект (ID: " + projectId + ")");
        textClientName.setText("Клиент: " + (clientName != null ? clientName : "Неизвестно"));
        //textProjectDescription.setText("Описание: " + (projectDescription != null ? projectDescription : "Нет описания"));
        textProjectDescription.setText("Описание проекта: Подробное описание проекта, его цели и задачи."); // Заглушка
        textProjectStatus.setText("Статус: " + (projectStatus != null ? projectStatus : "Неизвестно"));
        textProjectDates.setText("Сроки: " + (projectStartDate != null ? projectStartDate : "N/A") + " - " + (projectEndDate != null ? projectEndDate : "N/A"));


        // Загрузка данных задач для этого проекта
        // Очищаем существующий список и добавляем новые данные
        List<Task> fetchedTasks = new ArrayList<>();
        if (projectId == 1) { // Пример: если это проект с ID 1
            fetchedTasks.add(new Task(101, 1, "mgr1", "Разработать макет главной страницы", "Детальное описание макета...", "25.06.2025 18:00", "В работе", 70, "Иван Иванов"));
            fetchedTasks.add(new Task(102, 1, "mgr2", "Согласовать медиаплан", "Получить подтверждение от клиента.", "20.06.2025 12:00", "На проверке", 90, "Мария Петрова"));
            fetchedTasks.add(new Task(103, 1, "mgr1", "Подготовить ТЗ для дизайнера", "Прописать все требования к дизайну баннеров.", "22.06.2025 10:00", "Новая", 0, "Иван Иванов"));
        } else if (projectId == 2) {
            fetchedTasks.add(new Task(201, 2, "mgr3", "Провести анализ конкурентов", "Собрать данные о конкурентах.", "15.07.2025 16:00", "Отложена", 20, "Ольга Сидорова"));
        }
        // ... другие задачи для других проектов

        taskList.clear();
        taskList.addAll(fetchedTasks);
        taskAdapter.notifyDataSetChanged();
        updateNoTasksVisibility();
    }

    private void updateNoTasksVisibility() {
        if (taskList.isEmpty()) {
            textNoTasks.setVisibility(View.VISIBLE);
            recyclerViewTasks.setVisibility(View.GONE);
        } else {
            textNoTasks.setVisibility(View.GONE);
            recyclerViewTasks.setVisibility(View.VISIBLE);
        }
    }

    private void navigateToAddEditProject() {
        // Переход на экран редактирования проекта
        Intent intent = new Intent(ProjectDetailsActivity.this, AddEditProjectActivity.class);
        intent.putExtra("project_id", projectId); // Передаем ID текущего проекта для редактирования
        startActivity(intent);
    }

    private void navigateToAddEditTask() {
        // Переход на экран добавления новой задачи, автоматически привязанной к текущему проекту
        Intent intent = new Intent(ProjectDetailsActivity.this, AddEditTaskActivity.class);
        intent.putExtra("project_id", projectId); // Очень важно: передаем ID проекта
        // Можно передать и название проекта, если нужно для заголовка на экране задачи
        intent.putExtra("project_name", textProjectName.getText().toString());
        startActivity(intent);
    }

    // Обработка нажатия на элемент списка задачи (из TaskAdapter.OnItemClickListener)
    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(ProjectDetailsActivity.this, TaskDetailsActivity.class);
        intent.putExtra("task_id", task.getId()); // Передаем ID задачи
        // Можете передать другие данные задачи, чтобы избежать лишнего запроса к API на следующем экране
        startActivity(intent);
    }

    // Методы для свайпов задач (если будем их реализовывать)
    @Override
    public void onTaskSwipeRight(Task task, int position) {
        Toast.makeText(this, "Свайп вправо по задаче: " + task.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskSwipeLeft(Task task, int position) {
        Toast.makeText(this, "Свайп влево по задаче: " + task.getTitle(), Toast.LENGTH_SHORT).show();
        // Пример: Удаление задачи
        new AlertDialog.Builder(this)
                .setTitle("Удалить задачу")
                .setMessage("Вы уверены, что хотите удалить задачу \"" + task.getTitle() + "\"?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    // Отправить запрос на API для удаления
                    // В случае успеха:
                    taskAdapter.removeTaskAtPosition(position);
                    updateNoTasksVisibility();
                    Toast.makeText(ProjectDetailsActivity.this, "Задача удалена: " + task.getTitle(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", (dialog, which) -> {
                    // Если пользователь отменил, возвращаем элемент в исходное состояние
                    taskAdapter.notifyItemChanged(position);
                    dialog.dismiss();
                })
                .show();
    }
}
