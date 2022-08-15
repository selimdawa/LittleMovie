package com.flatcode.littlemovieadmin.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemovieadmin.Adapter.CastMovieAdapter;
import com.flatcode.littlemovieadmin.Adapter.CommentAdapter;
import com.flatcode.littlemovieadmin.Model.Cast;
import com.flatcode.littlemovieadmin.Model.Comment;
import com.flatcode.littlemovieadmin.Model.Movie;
import com.flatcode.littlemovieadmin.Model.User;
import com.flatcode.littlemovieadmin.MyApplication;
import com.flatcode.littlemovieadmin.R;
import com.flatcode.littlemovieadmin.Unit.CLASS;
import com.flatcode.littlemovieadmin.Unit.DATA;
import com.flatcode.littlemovieadmin.Unit.THEME;
import com.flatcode.littlemovieadmin.Unit.VOID;
import com.flatcode.littlemovieadmin.databinding.ActivityMovieDetailsBinding;
import com.flatcode.littlemovieadmin.databinding.DialogCommentAddBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MovieDetailsActivity extends AppCompatActivity {

    private ActivityMovieDetailsBinding binding;
    Activity activity = MovieDetailsActivity.this;

    String movieId, movieLink, title;

    private ProgressDialog dialog;

    private ArrayList<String> itemCast;
    private ArrayList<Comment> listComment;
    private ArrayList<Cast> listCast;
    private CommentAdapter adapterComment;
    private CastMovieAdapter adapterCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        movieId = intent.getStringExtra(DATA.MOVIE_ID);
        movieLink = intent.getStringExtra(DATA.MOVIE_LINK);

        binding.toolbar.nameSpace.setText(R.string.details_movie);

        dialog = new ProgressDialog(activity);
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);

        VOID.nrLoves(binding.loves, movieId);

        binding.favorite.setOnClickListener(v -> VOID.checkFavorite(binding.favorite, movieId));
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.view.setOnClickListener(v ->
                VOID.IntentExtra(activity, CLASS.MOVIE_VIEW, DATA.MOVIE_LINK, movieLink));

        binding.addComment.setOnClickListener(v -> {
            if (DATA.FIREBASE_USER == null) {
                Toast.makeText(activity, "You're not logged in...", Toast.LENGTH_SHORT).show();
            } else {
                addCommentDialog();
            }
        });

        //binding.recyclerView.setHasFixedSize(true);
        listCast = new ArrayList<>();
        binding.recyclerCast.setAdapter(adapterCast);
        adapterCast = new CastMovieAdapter(activity, listCast);
    }

    private void start() {
        loadDetails();
        loadComments();
        getCast();
        VOID.isFavorite(binding.favorite, movieId, DATA.FirebaseUserUid);
    }

    private void loadComments() {
        listComment = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES);
        ref.child(movieId).child(DATA.COMMENTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listComment.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Comment comment = data.getValue(Comment.class);
                    listComment.add(comment);
                }
                adapterComment = new CommentAdapter(activity, listComment);
                binding.recyclerComment.setAdapter(adapterComment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCast() {
        itemCast = new ArrayList();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE)
                .child(movieId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemCast.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    itemCast.add(snapshot.getKey());
                }
                loadCast();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadCast() {
        Query ref = FirebaseDatabase.getInstance().getReference(DATA.CAST);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listCast.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Cast cast = snapshot.getValue(Cast.class);
                    for (String id : itemCast) {
                        assert cast != null;
                        if (cast.getId() != null)
                            if (cast.getId().equals(id)) {
                                listCast.add(cast);
                            }
                    }
                }

                binding.recyclerCast.setAdapter(adapterCast);
                adapterCast.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String comment = DATA.EMPTY;

    private void addCommentDialog() {
        DialogCommentAddBinding commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(this));

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setView(commentAddBinding.getRoot());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        commentAddBinding.back.setOnClickListener(v -> alertDialog.dismiss());
        commentAddBinding.submit.setOnClickListener(v -> {
            comment = commentAddBinding.comment.getText().toString().trim();

            if (TextUtils.isEmpty(comment)) {
                Toast.makeText(activity, "Enter your comment...", Toast.LENGTH_SHORT).show();
            } else {
                alertDialog.dismiss();
                addComment();
            }
        });
    }

    private void addComment() {
        dialog.setMessage("Adding comment...");
        dialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES);
        String id = ref.push().getKey();
        //setup data to add in db for comment
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(DATA.ID, id);
        hashMap.put(DATA.MOVIE_ID, DATA.EMPTY + movieId);
        hashMap.put(DATA.TIMESTAMP, System.currentTimeMillis());
        hashMap.put(DATA.COMMENT, DATA.EMPTY + comment);
        hashMap.put(DATA.PUBLISHER, DATA.EMPTY + DATA.FirebaseUserUid);

        assert id != null;
        ref.child(movieId).child(DATA.COMMENTS).child(id).setValue(hashMap).addOnSuccessListener(unused -> {
            Toast.makeText(activity, "Comment Added...", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadComments();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(activity, "Failed to add comment duo to  " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES);
        ref.child(movieId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get data
                Movie item = snapshot.getValue(Movie.class);
                assert item != null;
                title = DATA.EMPTY + item.getName();
                String description = DATA.EMPTY + item.getDescription();
                String categoryId = DATA.EMPTY + item.getCategoryId();
                String viewsCount = DATA.EMPTY + item.getViewsCount();
                String timestamp = DATA.EMPTY + item.getTimestamp();
                String image = DATA.EMPTY + item.getImage();
                String publisher = DATA.EMPTY + item.getPublisher();
                String durations = DATA.EMPTY + item.getDuration();
                String year = DATA.EMPTY + item.getYear();

                //format date
                String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                VOID.loadCategory(DATA.EMPTY + categoryId, binding.category);
                //set data
                VOID.Glide(false, activity, image, binding.image);
                VOID.Glide(false, activity, image, binding.cover);
                binding.title.setText(title);
                binding.description.setText(description);
                binding.views.setText(viewsCount);
                binding.date.setText(date);
                binding.duration.setText(VOID.convertDuration(Long.parseLong(durations)));
                binding.year.setText(year);
                userInfo(publisher);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo(String userId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DATA.USERS).child(userId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get data
                User item = snapshot.getValue(User.class);
                assert item != null;
                //String userId = DATA.EMPTY + item.getId();
                String imageProfile = DATA.EMPTY + item.getProfileImage();
                String username = DATA.EMPTY + item.getUsername();

                binding.publisherName.setText(username);
                VOID.Glide(true, activity, imageProfile, binding.publisherImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onRestart() {
        start();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        start();
        super.onResume();
    }
}