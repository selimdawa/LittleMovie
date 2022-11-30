package com.flatcode.littlemovieadmin.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemovieadmin.Adapter.CastMovieAddAdapter;
import com.flatcode.littlemovieadmin.Model.Cast;
import com.flatcode.littlemovieadmin.R;
import com.flatcode.littlemovieadmin.Unit.DATA;
import com.flatcode.littlemovieadmin.Unit.THEME;
import com.flatcode.littlemovieadmin.databinding.ActivityCastMovieBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class CastMovieAddActivity extends AppCompatActivity {

    private ActivityCastMovieBinding binding;
    Activity activity = CastMovieAddActivity.this;

    ArrayList<Cast> list;
    CastMovieAddAdapter adapter;

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityCastMovieBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.toolbar.nameSpace.setText(R.string.add_cast);
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        type = DATA.TIMESTAMP;

        //binding.recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new CastMovieAddAdapter(activity, list);
        binding.recyclerView.setAdapter(adapter);

    }

    private void getData() {
        Query ref = FirebaseDatabase.getInstance().getReference(DATA.CAST);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Cast item = data.getValue(Cast.class);
                    assert item != null;
                    list.add(item);
                }
                //for (int i = 0; i < 10; i++) {
                //    Cast cast = new Cast((DATA.EMPTY + i), (DATA.EMPTY + i), "basic");
                //    list.add(cast);
                //}

                Collections.reverse(list);
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
    protected void onRestart() {
        getData();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }
}