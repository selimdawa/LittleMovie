package com.flatcode.littlemovie.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.flatcode.littlemovie.Adapter.MovieAdapter;
import com.flatcode.littlemovie.Model.Movie;
import com.flatcode.littlemovie.Unit.DATA;
import com.flatcode.littlemovie.databinding.FragmentMyMoviesBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class myMoviesFragment extends Fragment {

    private FragmentMyMoviesBinding binding;

    List<String> check;
    ArrayList<Movie> list;
    MovieAdapter adapter;

    private String type;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyMoviesBinding.inflate(LayoutInflater.from(getContext()), container, false);

        type = DATA.TIMESTAMP;

        list = new ArrayList<>();
        binding.recyclerView.setAdapter(adapter);
        adapter = new MovieAdapter(getContext(), list, true);

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

        return binding.getRoot();
    }

    private void getData(String orderBy) {
        check = new ArrayList();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
                .child(DATA.FirebaseUserUid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                check.clear();
                for (DataSnapshot snapshot : dataSnapshot.child(DATA.CATEGORIES).getChildren())
                    check.add(snapshot.getKey());
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
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Movie song = snapshot.getValue(Movie.class);
                    for (String id : check) {
                        assert song != null;
                        if (song.getId() != null)
                            if (song.getCategoryId().equals(id))
                                list.add(song);
                    }
                }

                Collections.reverse(list);
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
    public void onStart() {
        getData(type);
        super.onStart();
    }
}