package com.example.adtaskmanager.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.adtaskmanager.fragments.ClientsFragment;
import com.example.adtaskmanager.fragments.ProjectsFragment;
import com.example.adtaskmanager.fragments.TasksFragment;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProjectsFragment();
            case 1:
                return new TasksFragment();
            case 2:
                return new ClientsFragment();
            default:
                return new ProjectsFragment(); // Fallback
        }
    }

    @Override
    public int getItemCount() {
        return 3; // У нас 3 вкладки: Проекты, Задачи, Клиенты
    }
}