package com.example.adtaskmanager.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adtaskmanager.R; // Убедитесь, что это ваш R файл
import com.example.adtaskmanager.models.Task; // Убедитесь, что это ваш класс Task

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private Context context;
    private OnItemClickListener listener; // Для обработки нажатий на элемент списка

    public interface OnItemClickListener {
        void onTaskClick(Task task);
        // Можно добавить методы для свайпов, если задачи будут поддерживать свайп
        void onTaskSwipeRight(Task task, int position);
        void onTaskSwipeLeft(Task task, int position);
    }

    public TaskAdapter(Context context, List<Task> taskList, OnItemClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskTitle.setText(task.getTitle());
        holder.taskAssignee.setText("Исполнитель: " + task.getAssigneeName());
        holder.taskDueDate.setText("Срок: " + task.getDueDate());
        holder.taskStatus.setText(task.getStatus());
        holder.progressBar.setProgress(task.getCompletionPercentage());
        holder.taskPercentage.setText(task.getCompletionPercentage() + "%");

        // Установка цвета статуса в зависимости от значения
        switch (task.getStatus()) {
            case "Новая":
                holder.taskStatus.setBackgroundResource(R.drawable.rounded_task_status_new);
                break;
            case "В работе":
                holder.taskStatus.setBackgroundResource(R.drawable.rounded_task_status_inprogress);
                break;
            case "На проверке":
                holder.taskStatus.setBackgroundResource(R.drawable.rounded_task_status_onreview);
                break;
            case "Завершена":
                holder.taskStatus.setBackgroundResource(R.drawable.rounded_task_status_completed);
                break;
            case "Отложена":
                holder.taskStatus.setBackgroundResource(R.drawable.rounded_task_status_deferred);
                break;
            default:
                holder.taskStatus.setBackgroundColor(Color.LTGRAY); // Статус по умолчанию
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public Task getTaskAtPosition(int position) {
        return taskList.get(position);
    }

    public void removeTaskAtPosition(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    public void updateTasks(List<Task> newTasks) {
        this.taskList.clear();
        this.taskList.addAll(newTasks);
        notifyDataSetChanged();
    }


    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TextView taskAssignee;
        TextView taskDueDate;
        TextView taskStatus;
        ProgressBar progressBar;
        TextView taskPercentage;
        CardView cardView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.text_task_title);
            taskAssignee = itemView.findViewById(R.id.text_task_assignee);
            taskDueDate = itemView.findViewById(R.id.text_task_due_date);
            taskStatus = itemView.findViewById(R.id.text_task_status);
            progressBar = itemView.findViewById(R.id.progress_bar_task);
            taskPercentage = itemView.findViewById(R.id.text_task_percentage);
            cardView = (CardView) itemView;
        }
    }
}