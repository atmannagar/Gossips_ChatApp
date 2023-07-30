package com.example.chatsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatsapp.Activities.ChatActivity;
import com.example.chatsapp.R;
import com.example.chatsapp.Models.User;
import com.example.chatsapp.databinding.RowConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersVeiwHolder> {

    Context context ;
    ArrayList<User> users ;

    public UsersAdapter(Context context , ArrayList<User> users){
        this.context = context;
        this.users = users ;

    }

    @NonNull
    @Override
    public UsersVeiwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation , parent , false);
        return new UsersVeiwHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersVeiwHolder holder, int position) {
        User user = users.get(position);

        String senderId = FirebaseAuth.getInstance().getUid();

        String senderRoom = senderId + user.getUid();

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String lasrMsg = snapshot.child("lastMsg").getValue(String.class);
                            Long time = snapshot.child("lasMsgTime").getValue(Long.class);

                            holder.binding.lastMsg.setText(lasrMsg);
                        } else{
                            holder.binding.lastMsg.setText("Tap to chat");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.binding.username.setText(user.getName());

        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , ChatActivity.class);
                intent.putExtra("name" , user.getName());
                intent.putExtra("uid" , user.getUid());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersVeiwHolder extends RecyclerView.ViewHolder {

        RowConversationBinding binding ;

        public UsersVeiwHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
