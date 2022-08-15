package com.flatcode.littlemovie.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemovie.Adapter.MovieAdapter;
import com.flatcode.littlemovie.Model.Movie;
import com.flatcode.littlemovie.R;
import com.flatcode.littlemovie.Unit.DATA;
import com.flatcode.littlemovie.Unit.THEME;
import com.flatcode.littlemovie.Unit.VOID;
import com.flatcode.littlemovie.databinding.ActivityCastDetailsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CastDetailsActivity extends AppCompatActivity {

    private ActivityCastDetailsBinding binding;
    Activity activity = CastDetailsActivity.this;

    List<String> item;
    ArrayList<Movie> list;
    MovieAdapter adapter;

    String type, castId, castName, castImage, castAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityCastDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        castId = getIntent().getStringExtra(DATA.CAST_ID);
        castName = getIntent().getStringExtra(DATA.CAST_NAME);
        castImage = getIntent().getStringExtra(DATA.CAST_IMAGE);
        castAbout = getIntent().getStringExtra(DATA.CAST_ABOUT);
        type = DATA.TIMESTAMP;

        VOID.Glide(true, activity, castImage, binding.image);
        VOID.GlideBlur(true, activity, castImage, binding.imageBlur, 50);

        VOID.isInterested(binding.add, castId, DATA.CAST);
        binding.add.setOnClickListener(V -> VOID.checkInterested(binding.add, DATA.CAST, castId));

        binding.toolbar.nameSpace.setText(R.string.cast_details);
        binding.name.setText(castName);
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        type = DATA.TIMESTAMP;

        binding.toolbar.search.setOnClickListener(v -> {
            binding.toolbar.toolbar.setVisibility(View.GONE);
            binding.toolbar.toolbarSearch.setVisibility(View.VISIBLE);
            DATA.searchStatus = true;
        });

        binding.toolbar.close.setOnClickListener(v -> onBackPressed());

        binding.toolbar.textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapter.getFilter().filter(s);
                } catch (Exception e) {
                    //None
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.go.setOnClickListener(v -> VOID.dialogAboutArtist(activity, castImage, castName, castAbout));

        //binding.recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new MovieAdapter(activity, list, true);
        binding.recyclerView.setAdapter(adapter);

        binding.switchBar.all.setOnClickListener(v -> {
            type = DATA.TIMESTAMP;
            getData(type);
        });
        binding.switchBar.mostViews.setOnClickListener(v -> {
            type = DATA.VIEWS_COUNT;
            getData(type);
        });
        binding.switchBar.mostLoves.setOnClickListener(v -> {
            type = DATA.LOVES_COUNT;
            getData(type);
        });
        binding.switchBar.name.setOnClickListener(v -> {
            type = DATA.NAME;
            getData(type);
        });
    }

    private void getData(String orderBy) {
        item = new ArrayList();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                        if (snapshot2.getKey().equals(castId))
                            item.add(snapshot.getKey());
                    }
                }
                getItems(orderBy);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getItems(String orderBy) {
        Query ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES);
        ref.orderByChild(orderBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Movie movie = snapshot.getValue(Movie.class);
                    for (String id : item) {
                        assert movie != null;
                        if (movie.getId() != null)
                            if (movie.getId().equals(id)) {
                                list.add(movie);
                                i++;
                            }
                    }
                }

                Collections.reverse(list);
                binding.toolbar.number.setText(MessageFormat.format("( {0} )", i));
                binding.recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                binding.progress.setVisibility(View.GONE);
                if (!list.isEmpty()) {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.emptyText.setVisibility(View.GONE);
                } else {
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.emptyText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (DATA.searchStatus) {
            binding.toolbar.toolbar.setVisibility(View.VISIBLE);
            binding.toolbar.toolbarSearch.setVisibility(View.GONE);
            DATA.searchStatus = false;
            binding.toolbar.textSearch.setText(DATA.EMPTY);
        } else if (DATA.isChange) {
            onResume();
            DATA.isChange = false;
        } else
            super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        getData(type);
        super.onRestart();
    }

    @Override
    protected void onResume() {
        getData(type);
        super.onResume();
    }
}