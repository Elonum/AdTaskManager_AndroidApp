package com.example.adtaskmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adtaskmanager.R;
import com.example.adtaskmanager.models.Project;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private Context context;
    private List<Project> projectList;
    private OnItemClickListener listener; // Изменен на OnItemClickListener

    public ProjectAdapter(Context context, List<Project> projectList, OnItemClickListener listener) {
        this.context = context;
        this.projectList = projectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);
        holder.projectName.setText(project.getName());
        holder.clientName.setText("Имя клиента: " + project.getClientName());
        holder.projectDates.setText("Сроки: " + project.getStartDate() + " - " + project.getEndDate());
        holder.projectStatus.setText(project.getStatus());
        holder.progressBar.setProgress(project.getCompletionPercentage());

        // Установка цвета статуса
        switch (project.getStatus()) {
            case "Активен":
                holder.projectStatus.setBackgroundResource(R.drawable.rounded_task_status_deferred);
                break;
            case "На паузе":
                holder.projectStatus.setBackgroundResource(R.drawable.rounded_status_active);
                break;
            case "Завершен":
                holder.projectStatus.setBackgroundResource(R.drawable.rounded_task_status_onreview);
                break;
            case "Новый":
                holder.projectStatus.setBackgroundResource(R.drawable.rounded_task_status_inprogress); // Добавьте этот drawable
                break;
            default:
                holder.projectStatus.setBackgroundResource(R.drawable.rounded_task_status_new); // Дефолтный статус
                break;
        }


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProjectClick(project); // Используем onProjectClick
            }
        });
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public Project getProjectAtPosition(int position) {
        return projectList.get(position);
    }

    public void removeProjectAtPosition(int position) {
        projectList.remove(position);
        notifyItemRemoved(position);
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView projectName;
        TextView clientName;
        TextView projectDates;
        TextView projectStatus;
        ProgressBar progressBar;
        CardView cardView;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            projectName = itemView.findViewById(R.id.text_project_name);
            clientName = itemView.findViewById(R.id.text_client_name);
            projectDates = itemView.findViewById(R.id.text_project_dates);
            projectStatus = itemView.findViewById(R.id.text_project_status);
            progressBar = itemView.findViewById(R.id.progress_bar_project);
            cardView = (CardView) itemView; // Для получения ссылки на CardView
        }
    }

    // Измененный интерфейс OnItemClickListener
    public interface OnItemClickListener {
        void onProjectClick(Project project); // Изменено имя метода
        void onProjectSwipeRight(Project project, int position);
        void onProjectSwipeLeft(Project project, int position);
    }
}