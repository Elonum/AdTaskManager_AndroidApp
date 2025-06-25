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
import com.example.adtaskmanager.activities.ClientDetailsActivity;
import com.example.adtaskmanager.adapters.ClientAdapter; // Вам нужно будет создать этот адаптер
import com.example.adtaskmanager.models.Client; // Вам нужно будет создать эту модель

import java.util.ArrayList;
import java.util.List;

public class ClientsFragment extends Fragment implements ClientAdapter.OnItemClickListener {

    private RecyclerView recyclerViewClients;
    private ClientAdapter clientAdapter;
    private List<Client> clientList;
    private TextView textNoClients;

    public ClientsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clients, container, false);

        recyclerViewClients = view.findViewById(R.id.recycler_view_clients_fragment);
        textNoClients = view.findViewById(R.id.text_no_clients_fragment);

        clientList = new ArrayList<>();
        clientAdapter = new ClientAdapter(getContext(), clientList, this);
        recyclerViewClients.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewClients.setAdapter(clientAdapter);

        setupSwipeToActions(); // Настройка свайпов

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadClients(); // Загружаем клиентов при возвращении на экран
    }

    private void loadClients() {
        // TODO: Здесь будет запрос к ASP.NET Core Web API для получения списка клиентов
        // Пока что используем заглушку
        List<Client> fetchedClients = new ArrayList<>();
        fetchedClients.add(new Client(1, "ООО \"Рога и Копыта\"", "89001234567", "rogi.kopyta@example.com", "ул. Ленина, д.1", "Комментарий 1"));
        fetchedClients.add(new Client(2, "ИП Смирнов", "89123456789", "smirnov@example.com", "пр. Победы, д.10", "Комментарий 2"));

        clientList.clear();
        clientList.addAll(fetchedClients);
        clientAdapter.notifyDataSetChanged();
        updateNoClientsVisibility();
    }

    private void updateNoClientsVisibility() {
        if (clientList.isEmpty()) {
            textNoClients.setVisibility(View.VISIBLE);
            recyclerViewClients.setVisibility(View.GONE);
        } else {
            textNoClients.setVisibility(View.GONE);
            recyclerViewClients.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClientClick(Client client) {
        Intent intent = new Intent(getContext(), ClientDetailsActivity.class);
        intent.putExtra("client_id", client.getId());
        intent.putExtra("client_name", client.getName());
        intent.putExtra("client_phone", client.getPhone());
        intent.putExtra("client_email", client.getEmail());
        intent.putExtra("client_address", client.getAddress());
        intent.putExtra("client_notes", client.getNotes());
        startActivity(intent);
    }

    @Override
    public void onClientSwipeRight(Client client, int position) {
        Toast.makeText(getContext(), "Свайп вправо по клиенту: " + client.getName(), Toast.LENGTH_SHORT).show();
        clientAdapter.notifyItemChanged(position); // Возвращаем элемент на место
    }

    @Override
    public void onClientSwipeLeft(Client client, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Удалить клиента")
                .setMessage("Вы уверены, что хотите удалить клиента \"" + client.getName() + "\"? Это действие необратимо.")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    // TODO: Отправить запрос на API для удаления клиента
                    clientAdapter.removeClientAtPosition(position);
                    updateNoClientsVisibility();
                    Toast.makeText(getContext(), "Клиент удален: " + client.getName(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", (dialog, which) -> {
                    clientAdapter.notifyItemChanged(position); // Возвращаем элемент в исходное состояние
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
                Client client = clientAdapter.getClientAtPosition(position);

                if (direction == ItemTouchHelper.LEFT) {
                    onClientSwipeLeft(client, position);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    onClientSwipeRight(client, position);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewClients);
    }
}