package com.flatcode.littlemovieadmin.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flatcode.littlemovieadmin.Model.Comment;
import com.flatcode.littlemovieadmin.MyApplication;
import com.flatcode.littlemovieadmin.Unit.DATA;
import com.flatcode.littlemovieadmin.Unit.VOID;
import com.flatcode.littlemovieadmin.databinding.ItemCommentBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private final Context context;
    public ArrayList<Comment> list;

    private ItemCommentBinding binding;

    public CommentAdapter(Context context, ArrayList<Comment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemCommentBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Comment item = list.get(position);

        String commentId = DATA.EMPTY + item.getId();
        String movieId = DATA.EMPTY + item.getMovieId();
        String comment = DATA.EMPTY + item.getComment();
        String publisher = DATA.EMPTY + item.getPublisher();
        String timestamp = DATA.EMPTY + item.getTimestamp();

        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

        holder.date.setText(date);
        holder.comment.setText(comment);

        loadUserDetails(publisher, holder.name, holder.image);

        holder.item.setOnClickListener(v -> {
            if (publisher.equals(DATA.FirebaseUserUid))
                deleteComment(commentId, movieId);
        });
    }

    private void deleteComment(String commentId, String movieId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Comment").setMessage("Are you sure you want to delete this comment?")
                .setPositiveButton("DELETE", (dialog, which) -> {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES);
                    ref.child(movieId).child(DATA.COMMENTS).child(commentId).removeValue()
                            .addOnSuccessListener(unused -> Toast.makeText(context,
                                    "Deleted...", Toast.LENGTH_SHORT).show()).addOnFailureListener(e ->
                                    Toast.makeText(context, "Failed to delete duo to " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }).setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss()).show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, comment, date;
        LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            image = binding.image;
            name = binding.name;
            comment = binding.comment;
            date = binding.date;
            item = binding.item;
        }
    }

    private void loadUserDetails(String publisher, TextView name, ImageView image) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.USERS);
        ref.child(publisher).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = DATA.EMPTY + snapshot.child(DATA.USER_NAME).getValue();
                String profileImage = DATA.EMPTY + snapshot.child(DATA.PROFILE_IMAGE).getValue();

                VOID.Glide(true, context, profileImage, image);
                name.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}