package com.example.adtaskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.view.Window;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.adtaskmanager.activities.AddEditClientActivity;
import com.example.adtaskmanager.activities.AddEditProjectActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.adtaskmanager.R; // Убедитесь, что R импортируется
import com.example.adtaskmanager.adapters.FragmentAdapter; // Новый адаптер для ViewPager2
import com.example.adtaskmanager.fragments.ProjectsFragment; // Импортируем фрагменты, если нужно получить ссылку
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAdd;
    private ImageButton btnAddToolbar;
    private TextView toolbarTitle;
    private ImageButton btnFilterSort; // Объявляем здесь, чтобы использовать в updateUiForSelectedTab

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Прозрачный status bar и immersive mode
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        window.getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Инициализация элементов UI
        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabAdd = findViewById(R.id.fab_add);
        btnFilterSort = findViewById(R.id.btn_filter_sort); // Инициализация
        btnAddToolbar = findViewById(R.id.btn_add);
        toolbarTitle = findViewById(R.id.toolbar_title);

        // Настройка ViewPager2 с адаптером фрагментов
        FragmentManager fm = getSupportFragmentManager();
        FragmentAdapter adapter = new FragmentAdapter(this);
        viewPager.setAdapter(adapter);

        // Обработка выбора элементов BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_projects) {
                viewPager.setCurrentItem(0); // Первая вкладка: Проекты
                return true;
            } else if (itemId == R.id.nav_tasks) {
                viewPager.setCurrentItem(1); // Вторая вкладка: Задачи
                return true;
            } else if (itemId == R.id.nav_clients) {
                viewPager.setCurrentItem(2); // Третья вкладка: Клиенты
                return true;
            }
            return false;
        });

        // Обработка свайпов ViewPager2 для синхронизации с BottomNavigationView и Toolbar
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                updateUiForSelectedTab(position);
            }
        });

        // Устанавливаем начальное состояние UI для первой вкладки (Проекты)
        viewPager.setCurrentItem(0);
        updateUiForSelectedTab(0);

        // Обработчики нажатий на кнопки
        btnFilterSort.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem == 0) { // Проекты
                Toast.makeText(MainActivity.this, "Фильтр и Сортировка проектов", Toast.LENGTH_SHORT).show();
            } else if (currentItem == 1) { // Задачи
                Toast.makeText(MainActivity.this, "Фильтр и Сортировка задач", Toast.LENGTH_SHORT).show();
            } else if (currentItem == 2) { // Клиенты
                Toast.makeText(MainActivity.this, "Фильтр и Сортировка клиентов", Toast.LENGTH_SHORT).show();
            }
        });

        fabAdd.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem == 0) { // Проекты
                Intent intent = new Intent(MainActivity.this, AddEditProjectActivity.class);
                startActivity(intent);
            } else if (currentItem == 1) { // Задачи
                // Для задач, пока что, прямое добавление с этой вкладки может быть нелогичным
                // без привязки к проекту. Можно открыть диалог выбора проекта,
                // или перевести на экран добавления задачи, где можно выбрать проект.
                // В этом примере просто Toast.
                Toast.makeText(MainActivity.this, "Задачи обычно добавляются внутри конкретных проектов.", Toast.LENGTH_LONG).show();
            } else if (currentItem == 2) { // Клиенты
                Intent intent = new Intent(MainActivity.this, AddEditClientActivity.class);
                startActivity(intent);
            }
        });

        btnAddToolbar.setOnClickListener(v -> {
            // Поведение кнопки в тулбаре такое же, как у FAB
            fabAdd.performClick();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    // Метод для обновления заголовка тулбара и описания FAB/Toolbar кнопки
    private void updateUiForSelectedTab(int position) {
        switch (position) {
            case 0: // Проекты
                toolbarTitle.setText("Проекты");
                fabAdd.setContentDescription("Добавить новый проект");
                btnAddToolbar.setContentDescription("Добавить новый проект");
                fabAdd.show(); // Показывать FAB для добавления проекта
                btnAddToolbar.setVisibility(View.VISIBLE);
                btnFilterSort.setVisibility(View.VISIBLE); // Показать кнопку фильтрации
                break;
            case 1: // Задачи
                toolbarTitle.setText("Задачи");
                fabAdd.setContentDescription("Добавить новую задачу");
                btnAddToolbar.setContentDescription("Добавить новую задачу");
                // Можно скрыть FAB или изменить его поведение, так как задачи обычно добавляются в рамках проекта
                fabAdd.hide(); // Скрыть FAB для задач
                btnAddToolbar.setVisibility(View.GONE); // Скрыть кнопку добавления в тулбаре для задач
                btnFilterSort.setVisibility(View.VISIBLE); // Показать кнопку фильтрации
                break;
            case 2: // Клиенты
                toolbarTitle.setText("Клиенты");
                fabAdd.setContentDescription("Добавить нового клиента");
                btnAddToolbar.setContentDescription("Добавить нового клиента");
                fabAdd.show(); // Показывать FAB для добавления клиента
                btnAddToolbar.setVisibility(View.VISIBLE);
                btnFilterSort.setVisibility(View.GONE); // Скрыть кнопку фильтрации для клиентов (по вашим скриншотам)
                break;
        }
    }
}