package com.example.adtaskmanager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adtaskmanager.R;
import com.example.adtaskmanager.activities.AddEditTaskActivity;
import com.example.adtaskmanager.activities.TaskDetailsActivity;
import com.example.adtaskmanager.adapters.TaskAdapter;
import com.example.adtaskmanager.models.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment implements TaskAdapter.OnItemClickListener {

    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private TextView textNoTasks;

    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        recyclerViewTasks = view.findViewById(R.id.recycler_view_tasks_fragment);
        textNoTasks = view.findViewById(R.id.text_no_tasks_fragment);

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(getContext(), taskList, this);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTasks.setAdapter(taskAdapter);

        setupSwipeToActions(); // Настройка свайпов

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAllTasks(); // Загружаем все задачи при возвращении на экран
    }

    private void loadAllTasks() {
        // TODO: Здесь будет запрос к ASP.NET Core Web API для получения ВСЕХ задач пользователя.
        // Пока что используем заглушку, имитируя получение задач из разных проектов.
        List<Task> fetchedTasks = new ArrayList<>();
        // Задачи из проекта 1
        fetchedTasks.add(new Task(101, 1, "mgr1", "Разработать макет главной страницы", "Детальное описание макета...", "25.06.2025 18:00", "В работе", 70, "Иван Иванов"));
        fetchedTasks.add(new Task(102, 1, "mgr2", "Согласовать медиаплан", "Получить подтверждение от клиента.", "20.06.2025 12:00", "На проверке", 90, "Мария Петрова"));
        fetchedTasks.add(new Task(103, 1, "mgr1", "Подготовить ТЗ для дизайнера", "Прописать все требования к дизайну баннеров.", "22.06.2025 10:00", "Новая", 0, "Иван Иванов"));
        // Задачи из проекта 2
        fetchedTasks.add(new Task(201, 2, "mgr3", "Провести анализ конкурентов", "Собрать данные о конкурентах.", "15.07.2025 16:00", "Отложена", 20, "Ольга Сидорова"));
        fetchedTasks.add(new Task(202, 2, "mgr1", "Составить список фич для MVP", "Определить минимально жизнеспособный продукт.", "01.08.2025 10:00", "В работе", 40, "Иван Иванов"));

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

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(getContext(), TaskDetailsActivity.class);
        intent.putExtra("task_id", task.getId());
        intent.putExtra("project_id", task.getProjectId());
        intent.putExtra("task_title", task.getTitle());
        intent.putExtra("task_description", task.getDescription());
        intent.putExtra("task_assignee_id", task.getAssigneeId());
        intent.putExtra("task_assignee_name", task.getAssigneeName());
        intent.putExtra("task_due_date", task.getDueDate());
        intent.putExtra("task_status", task.getStatus());
        intent.putExtra("task_completion_percentage", task.getCompletionPercentage());
        startActivity(intent);
    }

    @Override
    public void onTaskSwipeRight(Task task, int position) {
        Toast.makeText(getContext(), "Свайп вправо по задаче: " + task.getTitle(), Toast.LENGTH_SHORT).show();
        taskAdapter.notifyItemChanged(position); // Возвращаем элемент на место
    }

    @Override
    public void onTaskSwipeLeft(Task task, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Удалить задачу")
                .setMessage("Вы уверены, что хотите удалить задачу \"" + task.getTitle() + "\"?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    // TODO: Отправить запрос на API для удаления задачи
                    taskAdapter.removeTaskAtPosition(position);
                    updateNoTasksVisibility();
                    Toast.makeText(getContext(), "Задача удалена: " + task.getTitle(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", (dialog, which) -> {
                    taskAdapter.notifyItemChanged(position); // Возвращаем элемент в исходное состояние
                    dialog.dismiss();
                })
                .show();
    }

    private void setupSwipeToActions() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task task = taskAdapter.getTaskAtPosition(position);

                if (direction == ItemTouchHelper.LEFT) {
                    onTaskSwipeLeft(task, position);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    onTaskSwipeRight(task, position);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewTasks);
    }
}