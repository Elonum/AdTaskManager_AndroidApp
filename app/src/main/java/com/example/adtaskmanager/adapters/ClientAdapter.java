package com.example.adtaskmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adtaskmanager.R;
import com.example.adtaskmanager.models.Client; // Убедитесь, что ваш класс Client существует

import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {

    private Context context;
    private List<Client> clientList;
    private OnItemClickListener listener;

    public ClientAdapter(Context context, List<Client> clientList, OnItemClickListener listener) {
        this.context = context;
        this.clientList = clientList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_client, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        Client client = clientList.get(position);
        holder.clientName.setText(client.getName());

        if (client.getPhone() != null && !client.getPhone().isEmpty()) {
            holder.clientPhone.setText("Телефон: " + client.getPhone());
            holder.clientPhone.setVisibility(View.VISIBLE);
        } else {
            holder.clientPhone.setVisibility(View.GONE);
        }

        if (client.getEmail() != null && !client.getEmail().isEmpty()) {
            holder.clientEmail.setText("Email: " + client.getEmail());
            holder.clientEmail.setVisibility(View.VISIBLE);
        } else {
            holder.clientEmail.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClientClick(client);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    // Метод для получения объекта клиента по позиции (для свайпов)
    public Client getClientAtPosition(int position) {
        if (position >= 0 && position < clientList.size()) {
            return clientList.get(position);
        }
        return null;
    }

    // Метод для удаления клиента (для свайпов)
    public void removeClientAtPosition(int position) {
        if (position >= 0 && position < clientList.size()) {
            clientList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView clientName;
        TextView clientPhone;
        TextView clientEmail;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            clientName = itemView.findViewById(R.id.text_client_name);
            clientPhone = itemView.findViewById(R.id.text_client_phone);
            clientEmail = itemView.findViewById(R.id.text_client_email);
        }
    }

    // Интерфейс для обработки нажатий и свайпов
    public interface OnItemClickListener {
        void onClientClick(Client client);
        void onClientSwipeRight(Client client, int position); // Для будущего функционала (например, архивация)
        void onClientSwipeLeft(Client client, int position);  // Для будущего функционала (например, удаление)
    }
}