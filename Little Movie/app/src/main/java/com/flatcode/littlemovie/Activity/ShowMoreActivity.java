package com.flatcode.littlemovie.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemovie.Adapter.MovieAdapter;
import com.flatcode.littlemovie.Model.Movie;
import com.flatcode.littlemovie.Unit.DATA;
import com.flatcode.littlemovie.Unit.THEME;
import com.flatcode.littlemovie.databinding.ActivityShowMoreBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

public class ShowMoreActivity extends AppCompatActivity {

    private ActivityShowMoreBinding binding;
    Activity activity = ShowMoreActivity.this;

    ArrayList<Movie> list;
    MovieAdapter adapter;

    String type, name, isReverse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityShowMoreBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        type = intent.getStringExtra(DATA.SHOW_MORE_TYPE);
        name = intent.getStringExtra(DATA.SHOW_MORE_NAME);
        isReverse = intent.getStringExtra(DATA.SHOW_MORE_BOOLEAN);

        binding.toolbar.nameSpace.setText(name);

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

        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        //binding.recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        binding.recyclerView.setAdapter(adapter);
        adapter = new MovieAdapter(activity, list, true);
    }

    private void getData(String orderBy) {
        Query ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES);
        ref.orderByChild(orderBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                int i = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Movie item = data.getValue(Movie.class);
                    assert item != null;
                    if (orderBy.equals(DATA.EDITORS_CHOICE)) {
                        if (item.getEditorsChoice() > 0) {
                            list.add(item);
                            i++;
                        }
                    } else {
                        list.add(item);
                        i++;
                    }
                    if (isReverse.equals("true"))
                        Collections.reverse(list);
                    binding.toolbar.number.setText(MessageFormat.format("( {0} )", i));
                    binding.recyclerView.setAdapter(adapter);
                }
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