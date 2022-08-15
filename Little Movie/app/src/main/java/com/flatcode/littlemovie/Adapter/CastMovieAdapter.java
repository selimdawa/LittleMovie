package com.flatcode.littlemovie.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flatcode.littlemovie.Model.Cast;
import com.flatcode.littlemovie.Unit.CLASS;
import com.flatcode.littlemovie.Unit.DATA;
import com.flatcode.littlemovie.Unit.VOID;
import com.flatcode.littlemovie.databinding.ItemCastMovieBinding;

import java.util.ArrayList;

public class CastMovieAdapter extends RecyclerView.Adapter<CastMovieAdapter.ViewHolder> {

    private ItemCastMovieBinding binding;
    private final Activity activity;

    public ArrayList<Cast> list;

    public CastMovieAdapter(Activity activity, ArrayList<Cast> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemCastMovieBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Cast item = list.get(position);
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();
        String image = DATA.EMPTY + item.getImage();
        String aboutMy = DATA.EMPTY + item.getAboutMy();

        VOID.Glide(true, activity, image, holder.image);

        if (item.getName().equals(DATA.EMPTY)) {
            holder.name.setVisibility(View.GONE);
        } else {
            holder.name.setVisibility(View.VISIBLE);
            holder.name.setText(name);
        }

        holder.item.setOnClickListener(view ->
                VOID.IntentExtra4(activity, CLASS.CAST_DETAILS, DATA.CAST_ID, id, DATA.CAST_NAME,
                        name, DATA.CAST_IMAGE, image, DATA.CAST_ABOUT, aboutMy));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            image = binding.image;
            name = binding.name;
            item = binding.item;
        }
    }
}