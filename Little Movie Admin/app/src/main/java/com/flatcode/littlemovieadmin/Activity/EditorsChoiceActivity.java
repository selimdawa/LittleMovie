package com.flatcode.littlemovieadmin.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.flatcode.littlemovieadmin.Adapter.EditorsChoiceAdapter;
import com.flatcode.littlemovieadmin.Model.EditorsChoice;
import com.flatcode.littlemovieadmin.R;
import com.flatcode.littlemovieadmin.Unit.THEME;
import com.flatcode.littlemovieadmin.databinding.ActivityEditorsChoiceBinding;

import java.util.ArrayList;

public class EditorsChoiceActivity extends AppCompatActivity {

    private ActivityEditorsChoiceBinding binding;
    Activity activity = EditorsChoiceActivity.this;

    ArrayList<EditorsChoice> list;
    EditorsChoiceAdapter adapter;
    EditorsChoice editorsChoice = new EditorsChoice();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        THEME.setThemeOfApp(activity);
        super.onCreate(savedInstanceState);
        binding = ActivityEditorsChoiceBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.toolbar.nameSpace.setText(R.string.editors_choice);
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        //binding.recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new EditorsChoiceAdapter(activity, list);
        binding.recyclerView.setAdapter(adapter);

        IdeaPosts();
    }

    public void IdeaPosts() {
        list.clear();
        for (int i = 0; i < 50; i++) {
            list.add(editorsChoice);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}