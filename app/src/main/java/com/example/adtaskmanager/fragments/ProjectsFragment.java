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
import com.example.adtaskmanager.activities.ProjectDetailsActivity;
import com.example.adtaskmanager.adapters.ProjectAdapter;
import com.example.adtaskmanager.models.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectsFragment extends Fragment implements ProjectAdapter.OnItemClickListener {

    private RecyclerView recyclerViewProjects;
    private ProjectAdapter projectAdapter;
    private List<Project> projectList;
    private TextView textNoProjects;

    public ProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        recyclerViewProjects = view.findViewById(R.id.recycler_view_projects_fragment);
        textNoProjects = view.findViewById(R.id.text_no_projects_fragment);

        projectList = new ArrayList<>();
        projectAdapter = new ProjectAdapter(getContext(), projectList, this);
        recyclerViewProjects.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProjects.setAdapter(projectAdapter);

        setupSwipeToActions(); // Настройка свайпов

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProjects(); // Загружаем проекты при возвращении на экран
    }

    private void loadProjects() {
        // TODO: Здесь будет запрос к ASP.NET Core Web API для получения списка проектов
        // Пока что используем заглушку
        List<Project> fetchedProjects = new ArrayList<>();
        fetchedProjects.add(new Project(1, "Мобильное приложение для банка", "Банк Авангард", "Разработка нового мобильного приложения для физических лиц с расширенным функционалом.", "В работе", "01.06.2024", "31.12.2024", 75));
        fetchedProjects.add(new Project(2, "Корпоративный сайт", "ООО \"СтройГрад\"", "Создание современного корпоративного сайта с каталогом продукции и формой обратной связи.", "Новый", "15.07.2024", "30.09.2024", 10));
        fetchedProjects.add(new Project(3, "CRM-система", "IT-Solutions", "Внедрение и кастомизация CRM-системы для автоматизации продаж и маркетинга.", "Завершен", "01.01.2024", "31.05.2024", 100));

        projectList.clear();
        projectList.addAll(fetchedProjects);
        projectAdapter.notifyDataSetChanged();
        updateNoProjectsVisibility();
    }

    private void updateNoProjectsVisibility() {
        if (projectList.isEmpty()) {
            textNoProjects.setVisibility(View.VISIBLE);
            recyclerViewProjects.setVisibility(View.GONE);
        } else {
            textNoProjects.setVisibility(View.GONE);
            recyclerViewProjects.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProjectClick(Project project) {
        Intent intent = new Intent(getContext(), ProjectDetailsActivity.class);
        intent.putExtra("project_id", project.getId());
        intent.putExtra("project_name", project.getName());
        intent.putExtra("client_name", project.getClientName());
        intent.putExtra("project_status", project.getStatus());
        intent.putExtra("project_start_date", project.getStartDate());
        intent.putExtra("project_end_date", project.getEndDate());
        intent.putExtra("project_description", project.getDescription());
        startActivity(intent);
    }

    @Override
    public void onProjectSwipeRight(Project project, int position) {
        Toast.makeText(getContext(), "Свайп вправо по проекту: " + project.getName(), Toast.LENGTH_SHORT).show();
        projectAdapter.notifyItemChanged(position); // Возвращаем элемент на место
    }

    @Override
    public void onProjectSwipeLeft(Project project, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Удалить проект")
                .setMessage("Вы уверены, что хотите удалить проект \"" + project.getName() + "\"? Это действие необратимо.")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    // TODO: Отправить запрос на API для удаления проекта
                    projectAdapter.removeProjectAtPosition(position);
                    updateNoProjectsVisibility();
                    Toast.makeText(getContext(), "Проект удален: " + project.getName(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", (dialog, which) -> {
                    projectAdapter.notifyItemChanged(position); // Возвращаем элемент в исходное состояние
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
                Project project = projectAdapter.getProjectAtPosition(position);

                if (direction == ItemTouchHelper.LEFT) {
                    onProjectSwipeLeft(project, position);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    onProjectSwipeRight(project, position);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewProjects);
    }
}