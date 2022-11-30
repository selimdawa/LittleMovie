package com.flatcode.littlemovieadmin.Adapter;

import static com.flatcode.littlemovieadmin.Unit.DATA.castMovie;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flatcode.littlemovieadmin.Model.Cast;
import com.flatcode.littlemovieadmin.Unit.DATA;
import com.flatcode.littlemovieadmin.Unit.VOID;
import com.flatcode.littlemovieadmin.databinding.ItemCastMovieAddBinding;

import java.util.ArrayList;

public class CastMovieAddAdapter extends RecyclerView.Adapter<CastMovieAddAdapter.ViewHolder> {

    private ItemCastMovieAddBinding binding;
    private final Activity activity;

    public ArrayList<Cast> list;

    public CastMovieAddAdapter(Activity activity, ArrayList<Cast> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemCastMovieAddBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Cast item = list.get(position);
        String id = DATA.EMPTY + item.getId();
        String name = DATA.EMPTY + item.getName();
        String image = DATA.EMPTY + item.getImage();

        VOID.Glide(true, activity, image, holder.image);

        if (name.equals(DATA.EMPTY)) {
            holder.name.setVisibility(View.GONE);
        } else {
            holder.name.setVisibility(View.VISIBLE);
            holder.name.setText(name);
        }

        checkRemove(id, holder.add, holder.remove);
        checkAdd(id, holder.add, holder.remove);

        holder.add.setOnClickListener(v -> {
            castMovie.add(id);
            checkRemove(id, holder.add, holder.remove);
            checkAdd(id, holder.add, holder.remove);
        });
        holder.remove.setOnClickListener(v -> {
            if (castMovie.size() == 1) {
                castMovie.clear();
                holder.add.setVisibility(View.VISIBLE);
                holder.remove.setVisibility(View.GONE);
            } else {
                for (int i = 0; i < castMovie.size(); i++) {
                    if (!castMovie.get(i).equals(id)) {
                        castMovie.remove(id);
                        checkRemove(id, holder.add, holder.remove);
                        checkAdd(id, holder.add, holder.remove);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image, add, remove;
        TextView name;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            image = binding.image;
            name = binding.name;
            add = binding.add;
            remove = binding.remove;
            item = binding.item;
        }
    }

    private void checkAdd(String id, ImageView add, ImageView remove) {
        for (int i = 0; i < castMovie.size(); i++) {
            if (castMovie.get(i).equals(id)) {
                add.setVisibility(View.GONE);
                remove.setVisibility(View.VISIBLE);
            }
        }
    }

    private void checkRemove(String id, ImageView add, ImageView remove) {
        for (int i = 0; i < castMovie.size(); i++) {
            if (!castMovie.get(i).equals(id)) {
                add.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
            }
        }
    }
}